package com.example.myapplication.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 天气接口客户端。
 *
 * <p>用的是 Open-Meteo：免费、不需要 API key、不用注册，直接返回 JSON。
 * 一共请求两个接口：
 * <ol>
 *   <li>地理编码：把城市名换成经纬度</li>
 *   <li>天气预报：按经纬度取实时天气 + 未来 7 天</li>
 * </ol>
 *
 * <p>注意：这些方法都是阻塞的，必须在子线程里调用。
 */
public final class WeatherApiClient {

    private static final int TIMEOUT_MS = 10000;

    private WeatherApiClient() {
    }

    /** 一天的预报。 */
    public static class DayData {
        public final String condition;
        public final int high;
        public final int low;

        DayData(String condition, int high, int low) {
            this.condition = condition;
            this.high = high;
            this.low = low;
        }
    }

    /** 地理编码的结果：标准地名 + 经纬度。 */
    public static class Place {
        public final String name;
        public final double latitude;
        public final double longitude;

        Place(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /** 一次联网的结果。 */
    public static class Result {
        public final String city;
        public final int temp;
        public final String humidity;
        public final String wind;
        public final String air;
        public final List<DayData> days;

        Result(String city, int temp, String humidity, String wind, String air, List<DayData> days) {
            this.city = city;
            this.temp = temp;
            this.humidity = humidity;
            this.wind = wind;
            this.air = air;
            this.days = days;
        }
    }

    /** 城市名 -> 标准地名和经纬度，找不到返回 null。 */
    public static Place geocode(String cityName) throws IOException, JSONException {
        String url = "https://geocoding-api.open-meteo.com/v1/search?count=1&language=zh&format=json&name="
                + URLEncoder.encode(cityName, "UTF-8");
        JSONObject root = new JSONObject(request(url));
        JSONArray results = root.optJSONArray("results");
        if (results == null || results.length() == 0) {
            return null;
        }
        JSONObject first = results.getJSONObject(0);
        return new Place(first.getString("name"),
                first.getDouble("latitude"), first.getDouble("longitude"));
    }

    /** 按经纬度取实时天气和未来 7 天。 */
    public static Result fetch(double latitude, double longitude, String cityName)
            throws IOException, JSONException {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + latitude
                + "&longitude=" + longitude
                + "&current=temperature_2m,relative_humidity_2m,weather_code,"
                + "wind_speed_10m,wind_direction_10m"
                + "&daily=weather_code,temperature_2m_max,temperature_2m_min"
                + "&timezone=auto&forecast_days=7";
        JSONObject root = new JSONObject(request(url));

        JSONObject current = root.getJSONObject("current");
        int temp = (int) Math.round(current.getDouble("temperature_2m"));
        String humidity = current.getInt("relative_humidity_2m") + "%";
        String wind = windText(current.getDouble("wind_speed_10m"),
                current.getDouble("wind_direction_10m"));

        JSONObject daily = root.getJSONObject("daily");
        JSONArray codes = daily.getJSONArray("weather_code");
        JSONArray highs = daily.getJSONArray("temperature_2m_max");
        JSONArray lows = daily.getJSONArray("temperature_2m_min");
        List<DayData> days = new ArrayList<>();
        for (int i = 0; i < codes.length(); i++) {
            days.add(new DayData(describe(codes.getInt(i)),
                    (int) Math.round(highs.getDouble(i)),
                    (int) Math.round(lows.getDouble(i))));
        }

        return new Result(cityName, temp, humidity, wind, airQuality(latitude, longitude), days);
    }

    /** 空气质量，失败不影响主流程。 */
    private static String airQuality(double latitude, double longitude) {
        try {
            String url = "https://air-quality-api.open-meteo.com/v1/air-quality"
                    + "?latitude=" + latitude + "&longitude=" + longitude + "&current=european_aqi";
            JSONObject current = new JSONObject(request(url)).getJSONObject("current");
            return aqiText(current.getDouble("european_aqi"));
        } catch (Exception e) {
            return "—";
        }
    }

    private static String aqiText(double aqi) {
        if (aqi <= 20) {
            return "优";
        } else if (aqi <= 40) {
            return "良";
        } else if (aqi <= 60) {
            return "一般";
        } else if (aqi <= 80) {
            return "较差";
        } else if (aqi <= 100) {
            return "差";
        }
        return "极差";
    }

    /** 风向 + 风力等级。 */
    private static String windText(double speedKmh, double directionDeg) {
        String[] directions = {"北", "东北", "东", "东南", "南", "西南", "西", "西北"};
        int index = (int) Math.round(directionDeg / 45.0) % 8;
        return directions[index] + "风 " + beaufort(speedKmh) + " 级";
    }

    private static int beaufort(double kmh) {
        if (kmh < 1) {
            return 0;
        } else if (kmh < 6) {
            return 1;
        } else if (kmh < 12) {
            return 2;
        } else if (kmh < 20) {
            return 3;
        } else if (kmh < 29) {
            return 4;
        } else if (kmh < 39) {
            return 5;
        } else if (kmh < 50) {
            return 6;
        }
        return 7;
    }

    /** WMO 天气代码 -> 中文描述。 */
    private static String describe(int code) {
        switch (code) {
            case 0:
                return "晴";
            case 1:
                return "晴间多云";
            case 2:
                return "多云";
            case 3:
                return "阴";
            case 45:
            case 48:
                return "雾";
            case 51:
            case 53:
            case 55:
            case 56:
            case 57:
                return "毛毛雨";
            case 61:
                return "小雨";
            case 63:
                return "中雨";
            case 65:
                return "大雨";
            case 66:
            case 67:
                return "冻雨";
            case 71:
                return "小雪";
            case 73:
                return "中雪";
            case 75:
                return "大雪";
            case 77:
                return "雪粒";
            case 80:
                return "阵雨";
            case 81:
                return "强阵雨";
            case 82:
                return "暴雨";
            case 85:
                return "阵雪";
            case 86:
                return "强阵雪";
            case 95:
                return "雷阵雨";
            case 96:
            case 99:
                return "雷阵雨伴冰雹";
            default:
                return "未知";
        }
    }

    /** 发一个 GET 请求，返回响应体。 */
    private static String request(String urlString) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");

            int code = connection.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + code);
            }

            StringBuilder builder = new StringBuilder();
            try (InputStream input = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }
            return builder.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
