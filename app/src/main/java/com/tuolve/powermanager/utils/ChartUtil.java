package com.tuolve.powermanager.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.tuolve.powermanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/10.
 */

public class ChartUtil {

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    public static void lineSet(Context context,LineDataSet lineDataSet, int color, LineDataSet.Mode mode, int drawableID) {
        lineDataSet.setColor(color);
        lineDataSet.setLineWidth(2f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);

        //设置折线图填充
        if (drawableID == 0)lineDataSet.setDrawFilled(false);
        else {
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(context.getResources().getDrawable(drawableID));
        }
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(8.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
    }

}
