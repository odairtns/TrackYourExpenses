package com.odairtns.trackyourexpenses.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.odairtns.trackyourexpenses.Data.DbHandler;
import com.odairtns.trackyourexpenses.Models.Trip;
import com.odairtns.trackyourexpenses.Models.TripRecord;
import com.odairtns.trackyourexpenses.R;
import com.github.mikephil.charting.charts.BarChart; // Import BarChart
import com.github.mikephil.charting.data.BarData; // Import BarData
import com.github.mikephil.charting.data.BarDataSet; // Import BarDataSet
import com.github.mikephil.charting.data.BarEntry; // Import BarEntry

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* Changing the chart type
public class TripChart extends AppCompatActivity {

    private Context context;
    private PieChart mPieChart;
    private BarChart mBarChart; // Change to BarChart
    private DbHandler dbHandler;
    private List<TripRecord> tripRecordList;
    private Trip mTrip;
    private int tripID;
    private TextView mChartObs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_chart);
        context = this;
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID",0);
        mTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getTripRecords(tripID);

        mPieChart = findViewById(R.id.tripchartPiechart);
        setTitle(getResources().getString(R.string.chart));
        if(tripRecordList.size()>0) {
            //Setting up de graph
            mPieChart.setUsePercentValues(true);
            mPieChart.getDescription().setEnabled(false);
            mPieChart.setExtraOffsets(5, 10, 5, 5);

            mPieChart.setDragDecelerationFrictionCoef(0.95f);

            //mPieChart.setCenterTextTypeface(tfLight);
            // mPieChart.setCenterText(generateCenterSpannableText());

            mPieChart.setDrawHoleEnabled(false);
            mPieChart.setHoleColor(Color.WHITE);

            mPieChart.setTransparentCircleColor(Color.WHITE);
            mPieChart.setTransparentCircleAlpha(110);

            mPieChart.setHoleRadius(58f);
            mPieChart.setTransparentCircleRadius(61f);

            mPieChart.setDrawCenterText(true);

            mPieChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mPieChart.setRotationEnabled(true);
            mPieChart.setHighlightPerTapEnabled(true);

            //To implement on Click Listener
            // mPieChart.setOnChartValueSelectedListener(this);

            mPieChart.animateY(1400, Easing.EaseInOutQuad);

            Legend l = mPieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            mPieChart.setEntryLabelColor(Color.DKGRAY);
            //mPieChart.setEntryLabelTypeface(tfRegular);
            mPieChart.setEntryLabelTextSize(12f);

            setPiChartData();

            mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);
                    Toast.makeText(context, getResources().getString(R.string.amount)+": "+(nf.format(e.getY())),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });
        }



    }


    private void setPiChartData() {

        tripRecordList = dbHandler.getExpenseTotal(tripID);
        ArrayList<PieEntry> values = new ArrayList<>();
        int i = 0;
        for(TripRecord c : tripRecordList){
            values.add(i,new PieEntry(c.getAmountStdCurrency().floatValue(),c.getExpType()));
            i = i + 1;
        }

        PieDataSet dataSet = new PieDataSet(values, getResources().getString(R.string.expense_results));

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(17f);
        data.setValueTextColor(Color.DKGRAY);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripChart.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }
}
*/

public class TripChart extends AppCompatActivity {

    private Context context;
    private BarChart mBarChart; // Change to BarChart
    private PieChart mPieChart;
    private DbHandler dbHandler;
    private List<TripRecord> tripRecordList;
    private Trip mTrip;
    private int tripID;
    private TextView mChartObs;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        context = this;
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID", 0);
        mTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getTripRecords(tripID);
        mChartObs = findViewById(R.id.tripchartInfo);
        mChartObs.setText(getResources().getString(R.string.chart_info) + " " + mTrip.getStdCurrency());

        mBarChart = findViewById(R.id.tripBarChart);  // Change to BarChart
        setTitle(getResources().getString(R.string.chart));
        if (tripRecordList.size() > 0) {
            // Setting up the graph
            mBarChart.getDescription().setEnabled(false);
            mBarChart.setExtraOffsets(5, 10, 5, 5);

            mBarChart.setDragDecelerationFrictionCoef(0.95f);

            mBarChart.setDrawBarShadow(false);
            mBarChart.setDrawValueAboveBar(true);

            mBarChart.setMaxVisibleValueCount(50);
            mBarChart.setPinchZoom(false);

            mBarChart.setDrawGridBackground(false);

            mBarChart.animateY(1400, Easing.EaseInOutQuad);

            Legend l = mBarChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            setBarChartData();

            mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);
                    // Handle the click event for the bar chart here
                    // You can display information about the selected bar if needed
                    // Example: Toast.makeText(context, getResources().getString(R.string.amount) + ": " + (nf.format(e.getY())), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });
        }
    }

    private void setBarChartData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        int i = 0;
        float stdAmount;
        for (TripRecord c : tripRecordList) {
            stdAmount =  dbHandler.getExgRate(c.getTripID(),c.getCurrency()) * c.getAmount().floatValue();
            values.add(new BarEntry(i, stdAmount,c.getExpType()));
            i++;
        }

        BarDataSet dataSet = new BarDataSet(values, getResources().getString(R.string.expense_results));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set your desired bar colors

        BarData data = new BarData(dataSet);
        data.setValueTextSize(10f);
        data.setBarWidth(0.9f);

        mBarChart.setData(data);
        mBarChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripChart.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }
}
