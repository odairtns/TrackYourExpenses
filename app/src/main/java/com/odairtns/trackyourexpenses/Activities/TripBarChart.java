package com.odairtns.trackyourexpenses.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.odairtns.trackyourexpenses.Data.DbHandler;
import com.odairtns.trackyourexpenses.Models.Trip;
import com.odairtns.trackyourexpenses.Models.TripRecord;
import com.odairtns.trackyourexpenses.R;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class TripBarChart extends AppCompatActivity {

    private Context context;
    private BarChart barChart;
    private DbHandler dbHandler;
    private List<TripRecord> tripRecordList;
    private Trip mTrip;
    private int tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_bar_chart);
// Adding the variables to handle data
        context = this;
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID", 0);
        mTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getExpensesSumGroupByType(tripID);

        barChart = findViewById(R.id.barChart);
        setTitle(getResources().getString(R.string.chart));
        if (tripRecordList.size() > 0) {
            // Customize the chart
            barChart.setDrawBarShadow(false);
            barChart.setDrawValueAboveBar(true);
            barChart.getDescription().setEnabled(false);
            barChart.setPinchZoom(false);
            barChart.setDrawGridBackground(false);
            barChart.setMaxVisibleValueCount(50);

            // Customize the X and Y axes
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(7);
            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setSpaceTop(15f);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);

            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);

            barChart.animateY(1400, Easing.EaseInOutQuad);

            barChart.getAxisRight().setEnabled(false);


            // Retrieve data for the chart
            List<BarEntry> entries = new ArrayList<>();
            int i = 0;
            float stdAmount;
            for (TripRecord c : tripRecordList) {
                stdAmount =  dbHandler.getExgRate(c.getTripID(),c.getCurrency()) * c.getAmount().floatValue();
                entries.add(new BarEntry(i, stdAmount, c.getExpType()));
                i++;
            }

            BarDataSet dataSet = new BarDataSet(entries, getResources().getString(R.string.expense_results));
            dataSet.setColors(getBarColors(entries.size()));
            //dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Set your desired bar colors
            dataSet.setForm(Legend.LegendForm.SQUARE);

            BarData barData = new BarData(dataSet);
            barData.setValueTextSize(10f); //3010
            barData.setBarWidth(0.9f);//3010
            barChart.setData(barData);

            // Create a list of expenses and their corresponding colors
            List<String> expenseNames = new ArrayList<>();
            List<Integer> expenseColors = new ArrayList<>();

            for (BarEntry entry : entries) {
                expenseNames.add(entry.getData().toString()); // Use the expense type as the label
                expenseColors.add(dataSet.getColor(entries.indexOf(entry)));
            }

// Set legend
            Legend legend = barChart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(true);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);


// Set custom labels for the legend
            LegendEntry[] legendEntries = new LegendEntry[expenseNames.size()];
            for (i = 0; i < expenseNames.size(); i++) {
                LegendEntry entry = new LegendEntry();
                entry.formColor = expenseColors.get(i);
                entry.label = expenseNames.get(i);
                legendEntries[i] = entry;
            }

            legend.setCustom(legendEntries);

            // Refresh the chart
            barChart.animateXY(2000, 2000);
            barChart.invalidate();
        }
    }

    private ArrayList<Integer> getBarColors(int size) {
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            colors.add(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        }
        return colors;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripBarChart.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }


}