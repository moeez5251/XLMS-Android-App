package com.xlms.librarymanagement.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StackedAreaChartView extends View {

    private List<MonthData> data = new ArrayList<>();
    private Paint desktopPaint;
    private Paint mobilePaint;
    private Paint axisPaint;
    private Paint gridPaint;
    private Path desktopPath;
    private Path mobilePath;

    public StackedAreaChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        desktopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        desktopPaint.setStyle(Paint.Style.FILL);
        desktopPaint.setColor(Color.parseColor("#f97316")); // Orange
        desktopPaint.setAlpha(120);

        mobilePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mobilePaint.setStyle(Paint.Style.FILL);
        mobilePaint.setColor(Color.parseColor("#22c55e")); // Green
        mobilePaint.setAlpha(120);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.GRAY);
        axisPaint.setTextSize(24f);
        axisPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.LTGRAY);
        gridPaint.setStrokeWidth(1f);
        gridPaint.setAlpha(50);

        desktopPath = new Path();
        mobilePath = new Path();
    }

    public void setData(List<MonthData> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null || data.isEmpty()) return;

        float width = getWidth();
        float height = getHeight() - 60f; // Leave space for X-axis labels
        float padding = 40f;
        float chartWidth = width - 2 * padding;
        float stepX = chartWidth / (data.size() - 1);

        // Find max value for scaling
        int maxVal = 0;
        for (MonthData d : data) {
            maxVal = Math.max(maxVal, d.getDesktop() + d.getMobile());
        }
        float scaleY = height / (maxVal * 1.2f);

        // Draw Grid Lines (Horizontal)
        for (int i = 0; i <= 4; i++) {
            float y = height - (height / 4 * i);
            canvas.drawLine(padding, y, width - padding, y, gridPaint);
        }

        mobilePath.reset();
        desktopPath.reset();

        // 1. Draw Mobile Area (Bottom layer)
        mobilePath.moveTo(padding, height);
        for (int i = 0; i < data.size(); i++) {
            float x = padding + (i * stepX);
            float y = height - (data.get(i).getMobile() * scaleY);
            if (i == 0) mobilePath.lineTo(x, y);
            else {
                // Smooth curve logic could be added here, using lineTo for simplicity/reliability
                mobilePath.lineTo(x, y);
            }
        }
        mobilePath.lineTo(padding + (data.size() - 1) * stepX, height);
        mobilePath.close();
        canvas.drawPath(mobilePath, mobilePaint);

        // 2. Draw Desktop Area (Stacked on top of Mobile)
        desktopPath.moveTo(padding, height);
        // Start from the top of the mobile path at first index
        desktopPath.lineTo(padding, height - (data.get(0).getMobile() * scaleY));
        
        for (int i = 0; i < data.size(); i++) {
            float x = padding + (i * stepX);
            float mobileY = height - (data.get(i).getMobile() * scaleY);
            float desktopY = mobileY - (data.get(i).getDesktop() * scaleY);
            desktopPath.lineTo(x, desktopY);
        }
        
        // Trace back the mobile path top edge to close the stacked area
        for (int i = data.size() - 1; i >= 0; i--) {
            float x = padding + (i * stepX);
            float mobileY = height - (data.get(i).getMobile() * scaleY);
            desktopPath.lineTo(x, mobileY);
        }
        desktopPath.close();
        canvas.drawPath(desktopPath, desktopPaint);

        // Draw X-Axis Labels (Months)
        for (int i = 0; i < data.size(); i++) {
            if (i % 2 == 0) { // Show every second month to avoid crowding
                float x = padding + (i * stepX);
                String label = data.get(i).getMonth().substring(0, 3);
                canvas.drawText(label, x, getHeight() - 10, axisPaint);
            }
        }
    }
}
