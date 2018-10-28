package com.growatt.energymanagement.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/9/13.
 *
 */

public class GradientTextView extends android.support.v7.widget.AppCompatTextView {

    private LinearGradient mLinearGradient;
    private Rect mTextBound = new Rect();
    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = getPaint();
        String mTipText = getText().toString();
        mPaint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
        if (mLinearGradient != null) mPaint.setShader(mLinearGradient);
        canvas.drawText(mTipText, getMeasuredWidth() / 2 - mTextBound.width() / 2, getMeasuredHeight() / 2 +   mTextBound.height()/2, mPaint);
    }

    public void setShapeColors(int colors0, int colors1){
        mLinearGradient = new LinearGradient(0, 0, 0, getPaint().getTextSize(),colors0, colors1,Shader.TileMode.CLAMP);
        invalidate();
    }
}
