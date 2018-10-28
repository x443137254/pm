package com.growatt.energymanagement.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.growatt.energymanagement.R;


/**
 * Created by Administrator on 2018/9/26.
 * 饼状图
 */

public class CircleCake extends View {

    private int restColor;
    private int percentColor;
    private int max;
    private int percent;
    private Paint paint;
    private RectF rectF;

    public CircleCake(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleCake);
        restColor = typedArray.getColor(R.styleable.CircleCake_restColor, Color.RED);
        percentColor = typedArray.getColor(R.styleable.CircleCake_percentColor, Color.GREEN);
        max = typedArray.getInt(R.styleable.CircleCake_maxPercent,100);
        percent = typedArray.getInt(R.styleable.CircleCake_percent,0);
        typedArray.recycle();
        paint = new Paint();
        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(restColor);
        float r = getWidth()/2f;
        canvas.drawCircle(r,r,r,paint);
        paint.setColor(percentColor);
        rectF.set(0,0,getWidth(),getHeight());
        canvas.drawArc(rectF,-90,360f * percent/max,true,paint);
    }

    public void setPercent(int percent){
        if (percent > max) return;
        this.percent = percent;
        invalidate();
    }
}
