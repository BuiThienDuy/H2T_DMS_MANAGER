package com.H2TFC.H2T_DMS_MANAGER.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.H2TFC.H2T_DMS_MANAGER.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.LineData;

public class LineChartItem extends ChartItem {

    private Typeface mTf;
    private String description;

    public LineChartItem(ChartData<?> cd, Context c) {
        super(cd);
        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
    }

    public LineChartItem(ChartData<?> cd, Context c, String description) {
        super(cd);
        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
        this.description = description;
    }

    @Override
    public int getItemType() {
        return TYPE_LINECHART;
    }

    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_linechart, null);
            holder.chart = (LineChart) convertView.findViewById(R.id.chart);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        // holder.chart.setValueTypeface(mTf);
        holder.chart.setDescription("");
        holder.chart.setDrawGridBackground(false);

        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5);

        YAxis rightAxis = holder.chart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5);
        rightAxis.setDrawGridLines(false);

        // set data
        holder.chart.setData((LineData) mChartData);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        holder.chart.animateX(750);

        // set title
        holder.title.setText(description);

        return convertView;
    }

    private static class ViewHolder {
        LineChart chart;
        TextView title;
    }
}
