package com.xlms.librarymanagement.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PieChartView extends View {

    private Paint lendedPaint;
    private Paint availablePaint;
    private Paint overduePaint;
    private RectF rectF;

    private float lendedAngle = 0;
    private float availableAngle = 0;
    private float overdueAngle = 0;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        lendedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lendedPaint.setColor(Color.parseColor("#fe4c00"));
        lendedPaint.setStyle(Paint.Style.FILL);

        availablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        availablePaint.setColor(Color.parseColor("#00e597"));
        availablePaint.setStyle(Paint.Style.FILL);

        overduePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        overduePaint.setColor(Color.parseColor("#0092f6"));
        overduePaint.setStyle(Paint.Style.FILL);

        rectF = new RectF();
    }

    public void setData(int lended, int available, int overdue) {
        int total = lended + available + overdue;
        if (total == 0) {
            lendedAngle = 0;
            availableAngle = 360;
            overdueAngle = 0;
        } else {
            lendedAngle = 360f * lended / total;
            availableAngle = 360f * available / total;
            overdueAngle = 360f * overdue / total;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = Math.min(width, height) / 2;

        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        float startAngle = -90; // Start from top

        canvas.drawArc(rectF, startAngle, lendedAngle, true, lendedPaint);
        startAngle += lendedAngle;

        canvas.drawArc(rectF, startAngle, availableAngle, true, availablePaint);
        startAngle += availableAngle;

        canvas.drawArc(rectF, startAngle, overdueAngle, true, overduePaint);
    }
}
