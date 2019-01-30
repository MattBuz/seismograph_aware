package com.aware.plugin.template;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.aware.utils.IContextCard;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class ContextCard implements IContextCard {


    //Constructor used to instantiate this card
    public ContextCard() {
    }
    private View card;
    private LineChart mChartX, mChartY, mChartZ;
    private Thread thread;
    private boolean plotData = true;
    @Override
    public View getContextCard(Context context) {
        //Load card layout
        card = LayoutInflater.from(context).inflate(R.layout.card, null);

        //Set charts properties - scalling, adjusting
        mChartX = (LineChart) card.findViewById(R.id.chartX);
        setChartProperties(mChartX);
        mChartY = (LineChart) card.findViewById(R.id.chartY);
        setChartProperties(mChartY);
        mChartZ = (LineChart) card.findViewById(R.id.chartZ);
        setChartProperties(mChartZ);

        //Create dataSets for each axis X,Y,Z
        LineData dataX = new LineData();
        //dataX.setValueTextColor(Color.WHITE);
        mChartX.setData(dataX);

        LineData dataY = new LineData();
        dataY.setValueTextColor(Color.WHITE);
        mChartY.setData(dataY);

        LineData dataZ = new LineData();
        dataZ.setValueTextColor(Color.WHITE);
        mChartZ.setData(dataZ);

        //Register the broadcast receiver that will update the UI from the background service (Plugin)
        IntentFilter filter = new IntentFilter("ACCELEROMETER_DATA");
        context.registerReceiver(accelerometerObserver, filter);

        //Return the card to AWARE/apps
        return card;
    }

    private void setChartProperties (LineChart chart){
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setBackgroundColor(Color.WHITE);
    }

    //This broadcast receiver is auto-unregistered because it's not static.
    private AccelerometerObserver accelerometerObserver = new AccelerometerObserver();
    public class AccelerometerObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("ACCELEROMETER_DATA")) {
                ContentValues data = intent.getParcelableExtra("data");

                //Get data from accelerometer
                float data0 = data.getAsFloat("double_values_0");
                float data1 = data.getAsFloat("double_values_1");
                float data2 = data.getAsFloat("double_values_2");
                addEntry(mChartX,data0,Color.GREEN);
                addEntry(mChartY,data1,Color.MAGENTA);
                addEntry(mChartZ,data2,Color.BLUE);

            }
        }
    }
    private void addEntry(LineChart lineChart, float newValue, int color) {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet(color);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), newValue), 0);
            data.notifyDataChanged();

            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(150);
            lineChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet(int color) {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(color);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
}
