package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.PercentFormatter;

public class PieChartItem extends ChartItem {

    private Typeface mTf;
    private String description;

    public PieChartItem(ChartData<?> cd, Context c) {
        super(cd);

        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    public PieChartItem(ChartData<?> cd, Context c, String description) {
        super(cd);

        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
        this.description = description;
    }

    @Override
    public int getItemType() {
        return TYPE_PIECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_piechart, null);
            holder.chart = (PieChart) convertView.findViewById(R.id.chart);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.setDescription("");
        holder.chart.setHoleRadius(52f);
        holder.chart.setTransparentCircleRadius(57f);
        //holder.chart.setCenterText("MPChart\nAndroid");
        holder.chart.setCenterTextTypeface(mTf);
        holder.chart.setCenterTextSize(18f);
        holder.chart.setUsePercentValues(true);

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTypeface(mTf);
        mChartData.setValueTextSize(11f);
        mChartData.setValueTextColor(Color.WHITE);
        // set data
        holder.chart.setData((PieData) mChartData);

        Legend l = holder.chart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        holder.chart.animateXY(900, 900);

        // set title
        holder.title.setText(description);
        return convertView;
    }

    private static class ViewHolder {
        PieChart chart;
        TextView title;
    }
}
