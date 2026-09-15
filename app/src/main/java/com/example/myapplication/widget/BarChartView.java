package com.example.myapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.myapplication.R;

/**
 * 自定义控件：柱状图。
 *
 * <p>大作业要求"通过画图展示数据库中的内容"，这里直接用 {@link Canvas} 画柱子，
 * 不依赖任何图表库。支持一组或两组数据（例如最高温 / 最低温）。
 */
public class BarChartView extends View {

    private String[] labels = new String[0];
    private float[] series1 = new float[0];
    private float[] series2;
    private String name1 = "";
    private String name2;

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF barRect = new RectF();

    private final int colorFirst;
    private static final int COLOR_SECOND = 0xFFFF8A3D;
    private static final int COLOR_GRID = 0xFFEDF0F6;
    private static final int COLOR_TEXT = 0xFF8A90A2;

    public BarChartView(Context context) {
        this(context, null);
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        colorFirst = context.getColor(R.color.brand);

        textPaint.setColor(COLOR_TEXT);
        textPaint.setTextSize(sp(11));

        gridPaint.setColor(COLOR_GRID);
        gridPaint.setStrokeWidth(dp(1));

        barPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 设置图表数据。
     *
     * @param series2 第二组数据，没有就传 null
     * @param name2   第二组数据的名字，没有就传 null
     */
    public void setData(String[] labels, float[] series1, float[] series2,
                        String name1, String name2) {
        this.labels = labels == null ? new String[0] : labels;
        this.series1 = series1 == null ? new float[0] : series1;
        this.series2 = series2;
        this.name1 = name1 == null ? "" : name1;
        this.name2 = name2;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = (int) dp(200);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (labels.length == 0 || series1.length == 0) {
            return;
        }

        float left = dp(34);
        float right = getWidth() - dp(10);
        float top = dp(30);
        float bottom = getHeight() - dp(24);
        if (right <= left || bottom <= top) {
            return;
        }

        // 算数据里的最大值
        float rawMax = 0;
        for (float value : series1) {
            rawMax = Math.max(rawMax, value);
        }
        if (series2 != null) {
            for (float value : series2) {
                rawMax = Math.max(rawMax, value);
            }
        }
        if (rawMax <= 0) {
            rawMax = 1;
        }

        // 挑一个整齐的刻度间隔，保证纵轴刻度都是整数
        int gridLines = 4;
        float step = 1;
        float[] candidateSteps = {1, 2, 5, 10, 20, 25, 50, 100, 200, 500};
        for (float candidate : candidateSteps) {
            step = candidate;
            if (candidate * gridLines >= rawMax) {
                break;
            }
        }
        float max = step * gridLines;

        // 横向网格线 + 纵轴刻度
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i <= gridLines; i++) {
            float y = bottom - (bottom - top) * i / gridLines;
            canvas.drawLine(left, y, right, y, gridPaint);
            canvas.drawText(String.valueOf((int) (step * i)),
                    left - dp(6), y + dp(4), textPaint);
        }

        drawLegend(canvas, left, dp(14));

        boolean twoSeries = series2 != null && series2.length == series1.length;
        float slot = (right - left) / labels.length;
        float barWidth = slot * (twoSeries ? 0.26f : 0.4f);
        float gap = dp(3);

        textPaint.setTextAlign(Paint.Align.CENTER);
        for (int i = 0; i < labels.length; i++) {
            float center = left + slot * i + slot / 2;

            if (twoSeries) {
                drawBar(canvas, center - gap / 2 - barWidth, bottom, barWidth,
                        series1[i], max, top, colorFirst);
                drawBar(canvas, center + gap / 2, bottom, barWidth,
                        series2[i], max, top, COLOR_SECOND);
            } else {
                drawBar(canvas, center - barWidth / 2, bottom, barWidth,
                        series1[i], max, top, colorFirst);
            }

            canvas.drawText(labels[i], center, getHeight() - dp(8), textPaint);
        }
    }

    private void drawBar(Canvas canvas, float left, float bottom, float width,
                         float value, float max, float top, int color) {
        float height = (bottom - top) * Math.max(value, 0) / max;
        barPaint.setColor(color);
        barRect.set(left, bottom - height, left + width, bottom);
        canvas.drawRoundRect(barRect, dp(4), dp(4), barPaint);
    }

    private void drawLegend(Canvas canvas, float left, float y) {
        textPaint.setTextAlign(Paint.Align.LEFT);
        float x = left;

        barPaint.setColor(colorFirst);
        canvas.drawRoundRect(x, y - dp(5), x + dp(10), y + dp(5), dp(2), dp(2), barPaint);
        x += dp(14);
        canvas.drawText(name1, x, y + dp(4), textPaint);
        x += textPaint.measureText(name1) + dp(16);

        if (name2 != null && !name2.isEmpty()) {
            barPaint.setColor(COLOR_SECOND);
            canvas.drawRoundRect(x, y - dp(5), x + dp(10), y + dp(5), dp(2), dp(2), barPaint);
            x += dp(14);
            canvas.drawText(name2, x, y + dp(4), textPaint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
