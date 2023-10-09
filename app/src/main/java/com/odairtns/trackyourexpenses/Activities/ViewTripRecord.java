package com.odairtns.trackyourexpenses.Activities;

import static com.odairtns.trackyourexpenses.R.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.odairtns.trackyourexpenses.Adapters.ViewTripRecordAdapter;
import com.odairtns.trackyourexpenses.Data.DbHandler;
import com.odairtns.trackyourexpenses.Models.Trip;
import com.odairtns.trackyourexpenses.Models.TripRecord;
import com.odairtns.trackyourexpenses.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.opencsv.CSVWriter;

public class ViewTripRecord extends AppCompatActivity implements View.OnClickListener {
    private TextView mTrip, mDescription, mAmount, mStdCurrency, mNoRecordAdded, mBudget, mBalance,
            mBudgetText, mBalanceText, mTripSummary, mIncomeText;
    private ViewTripRecordAdapter adapter;
    private  RecyclerView recyclerView;
    private DbHandler dbHandler;
    private Trip selectedTrip;
    private int tripID;
    private List<TripRecord> tripRecordList;
    private FloatingActionButton fab;
    private Button  viewChart, editExgRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_view_trip_record);
        setTitle(getResources().getString(string.view_expenses));

        dbHandler = new DbHandler(this);
        selectedTrip = new Trip();
        tripID = getIntent().getIntExtra("TripID",0);
        selectedTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getTripRecords(tripID);


        mTrip = findViewById(id.viewrecordTripLabel);
        mTrip.setText(selectedTrip.getLabel());
        mTripSummary = findViewById(id.viewrecordSummaryText);
        mTripSummary.setText(getResources().getString(string.trip_summary) + " " +selectedTrip.getLabel());
        mTrip.setVisibility(View.GONE);

        mDescription = findViewById(id.viewrecordTripDescription);
        mAmount = findViewById(id.viewrecordTotalAmount);
        mAmount.setText(getResources().getString(string.no_expense_added));

        mDescription.setText(selectedTrip.getDescription());
        mStdCurrency = findViewById(id.viewrecordStdCurrency);
        mStdCurrency.setText(selectedTrip.getStdCurrency());
        mNoRecordAdded = findViewById(id.viewrecordNoRecordText);
        mBudget  = findViewById(id.viewrecordTripBudget);

        mBalance  = findViewById(id.viewrecordBalance);
        mBudgetText  = findViewById(id.viewrecordTripBudgetText);
        mBalanceText  = findViewById(id.viewrecordBalanceText);
        mIncomeText  = findViewById(id.viewrecordTotalIncome);

        viewChart = findViewById(id.viewrecordViewChart);
        viewChart.setOnClickListener(this);

        editExgRate = findViewById(id.viewrecordExchanceRate);
        editExgRate.setOnClickListener(this);

        recyclerView = findViewById(id.viewrecordRecyclerView);

        setLayout();

        // To save the last activity name
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastActivity", "ViewTripRecord"); // Replace "YourActivityName" with the name of your activity
        editor.apply();

    }
    private void setLayout(){

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        if(tripRecordList.size()>0) {
            if(selectedTrip.getMultipleCurrency() == Boolean.TRUE){
                tripRecordList = getStandardAmountList(tripRecordList);
                Collections.reverse(tripRecordList);
                mAmount.setText(String.valueOf(nf.format(getExpStd(tripRecordList))));
                mIncomeText.setText(String.valueOf(nf.format(getIncomeStd(tripRecordList))));
            }else{
                mAmount.setText(String.valueOf(nf.format(getExpStd(tripRecordList))));
            }
            displayBudget();
            recyclerView.setVisibility(View.VISIBLE);
            mNoRecordAdded.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ViewTripRecordAdapter(tripRecordList, this);
            recyclerView.setAdapter(adapter);

            adapter.setRecordChangeListener(new ViewTripRecordAdapter.myRecordChangeListener() {
                @Override
                public void onRecordDeleted() {
                    tripRecordList = dbHandler.getTripRecords(tripID);
                    setNewLayout();
                }

                @Override
                public void onRecordChanged() {
                    tripRecordList = dbHandler.getTripRecords(tripID);
                    setNewLayout();
                }
            });


        }else{
            displayBudget();
            recyclerView.setVisibility(View.GONE);
            mNoRecordAdded.setVisibility(View.VISIBLE);
            String text = getResources().getString(string.no_record_added);
            mNoRecordAdded.setText(text);
        }

        fab = findViewById(id.viewrecordFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),InsertRecord.class);
                intent.putExtra("TripID",selectedTrip.getID());
                v.getContext().startActivity(intent);
                finish();
            }
        });

    }

    private void setNewLayout(){

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        if(tripRecordList.size()>0) {
            if(selectedTrip.getMultipleCurrency() == Boolean.TRUE){
                tripRecordList = getStandardAmountList(tripRecordList);
                Collections.reverse(tripRecordList);
                mAmount.setText(String.valueOf(nf.format(getExpStd(tripRecordList))));
            }else{
                mAmount.setText(String.valueOf(nf.format(getExpStd(tripRecordList))));
            }
            displayBudget();
            recyclerView.setVisibility(View.VISIBLE);
            mNoRecordAdded.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


        }else{
            displayBudget();
            recyclerView.setVisibility(View.GONE);
            mNoRecordAdded.setVisibility(View.VISIBLE);
            String text = getResources().getString(string.no_record_added);
            mNoRecordAdded.setText(text);
        }

        fab = findViewById(id.viewrecordFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),InsertRecord.class);
                intent.putExtra("TripID",selectedTrip.getID());
                v.getContext().startActivity(intent);
                finish();
            }
        });

    }
/*
    private double getAmount(List<TripRecord> recordList){
        double totalAmount = 0;
        float exgRate;
        for(TripRecord listEntry : recordList){
            exgRate = dbHandler.getExgRate(tripID, listEntry.getCurrency());
            totalAmount = totalAmount + (exgRate * listEntry.getAmount());
        }
        return totalAmount;
    }

 */

    public List<TripRecord> getStandardAmountList (List<TripRecord> recordList){
        List<TripRecord> newTripRecordList = new ArrayList<>();
        TripRecord currentTrip;
        for( TripRecord c : recordList){
            currentTrip = new TripRecord();
            currentTrip.setDetails(c.getDetails());
            currentTrip.setCurrency(c.getCurrency());
            currentTrip.setExpType(c.getExpType());
            currentTrip.setDate(c.getDate());
            currentTrip.setAmount(c.getAmount());
            currentTrip.setId(c.getId());
            currentTrip.setTripID(c.getTripID());
            currentTrip.setPaymentMethod(c.getPaymentMethod());
            currentTrip.setAmountStdCurrency(dbHandler.getExgRate(c.getTripID(),c.getCurrency()) * c.getAmount() );
            newTripRecordList.add(currentTrip);
        }

        return newTripRecordList;
    }

    public double getAmountStd (List<TripRecord> recordList){
        double totalAmount = 0;
        for(TripRecord listEntry : recordList){
            totalAmount = totalAmount + listEntry.getAmountStdCurrency();
        }
        return totalAmount;
    }
    public double getIncomeStd (List<TripRecord> recordList){
        double totalAmount = 0;
        for(TripRecord listEntry : recordList){
            if (listEntry.getAmountStdCurrency() >= 0)
                totalAmount = totalAmount + listEntry.getAmountStdCurrency();
        }
        return totalAmount;
    }

    public double getExpStd (List<TripRecord> recordList){
        double totalAmount = 0;
        for(TripRecord listEntry : recordList){
            if (listEntry.getAmountStdCurrency() < 0)
                totalAmount = totalAmount + listEntry.getAmountStdCurrency();
        }
        return totalAmount;
    }

    public double getIncomeAmount (List<TripRecord> recordList){
        double totalIncome = 0;
        for(TripRecord listEntry : recordList){
            totalIncome = totalIncome + listEntry.getAmountStdCurrency();
        }
        return totalIncome;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     /*   if(item.getItemId() == R.id.mainmenuAddCurrency){
            Intent intent = new Intent(this,TripCurrencies.class);
            intent.putExtra("TripID",tripID);
            startActivity(intent);
            finish();
        }

      */
        if(item.getItemId() == id.share){
            shareTrip();
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayBudget(){
        if(selectedTrip.getBudget().equals(0.0f)){
            mBudget.setVisibility(View.GONE);
            mBalance.setVisibility(View.GONE);
            mBudgetText.setVisibility(View.GONE);
            mBalanceText.setVisibility(View.GONE);
        }else {
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            mBudget.setText(String.valueOf(nf.format(selectedTrip.getBudget())));

            double balance = selectedTrip.getBudget().doubleValue() +
                    getAmountStd(tripRecordList);
            double percent = Math.abs(getAmountStd(tripRecordList)) / selectedTrip.getBudget().doubleValue();
            if(percent <= 0.5)
                mBalance.setTextColor(getResources().getColor(color.colorStdBlue,getTheme()));
            else if(percent <= 0.75)
                mBalance.setTextColor(getResources().getColor(color.colorYellow,getTheme()));
            else
                mBalance.setTextColor(getResources().getColor(color.colorRed,getTheme()));
            mBalance.setText(String.valueOf(nf.format(balance)));

        }
    }

    public void shareTrip() {
        Trip shareTrip = selectedTrip;
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        double stdAmount, balance, income, expense;
        stdAmount = getAmountStd(tripRecordList);
        income = getIncomeAmount(tripRecordList);
        expense = getExpStd(tripRecordList);

        balance = selectedTrip.getBudget().doubleValue() +
                stdAmount;
        StringBuilder emailDetails = new StringBuilder();

        emailDetails.append(getResources().getString(string.std_currency_) + ": " + shareTrip.getStdCurrency() + "\n");
        emailDetails.append(getResources().getString(string.budget) + ": " + nf.format(shareTrip.getBudget()) + "\n");
        emailDetails.append(getResources().getString(string.balance) + ": " + nf.format(balance) + "\n");
        emailDetails.append(getResources().getString(string.total_expense) + " " + shareTrip.getStdCurrency() + ": " + nf.format(expense) + "\n");
        emailDetails.append(getResources().getString(string.total_income) + " " + shareTrip.getStdCurrency() + ": " + nf.format(income) + "\n");
        emailDetails.append(getResources().getString(string.description) + ": " + shareTrip.getDescription() + "\n");
        emailDetails.append("\n\n");


        for (TripRecord c : tripRecordList) {
            emailDetails.append(getResources().getString(string.date) + ": " + c.getDate() + "\n");
            emailDetails.append(getResources().getString(string.type) + ": " + c.getExpType() + "\n");
            emailDetails.append(getResources().getString(string.payment) + ": " + c.getPaymentMethod() + "\n");
            emailDetails.append(getResources().getString(string.amount) + ": " + nf.format(c.getAmount()) + " " + c.getCurrency() + "\n");
            emailDetails.append(getResources().getString(string.amount_std_currency) + ": " + nf.format(c.getAmountStdCurrency()) + "\n");
            emailDetails.append(getResources().getString(string.details) + ": " + c.getDetails() + "\n");
            emailDetails.append("\n");
        }

        // Create a CSV file
        File csvFile = createCSVFile();

        // Attach the CSV file to the email intent
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(string.trip_share_subject) + ": " + shareTrip.getLabel());
        intent.putExtra(Intent.EXTRA_TEXT, emailDetails.toString());

        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", csvFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Launch the email chooser
        try {
            startActivity(Intent.createChooser(intent, getResources().getString(string.send_email)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ViewTripRecord.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        /*Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(string.trip_share_subject)+
                ": "+shareTrip.getLabel());
        intent.putExtra(Intent.EXTRA_TEXT, emailDetails.toString());

        try{
            startActivity(Intent.createChooser(intent,getResources().getString(string.send_email)));
        }catch(ActivityNotFoundException e){
            Toast.makeText(ViewTripRecord.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }*/

    }
    private File createCSVFile() {
        // Create a temporary CSV file
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        File csvFile = new File(getCacheDir(), "trip_data.csv");

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFile))) {
            // Write CSV data
            // You need to modify this part to write your data in the desired CSV format
            // For example, you can write headers and rows here
            writer.writeNext(new String[]{"Date", "Type", "Payment", "Amount", "Amount (Std Currency)", "Details"});
            for (TripRecord c : tripRecordList) {
                writer.writeNext(new String[]{
                        c.getDate(),
                        c.getExpType(),
                        c.getPaymentMethod(),
                        nf.format(c.getAmount()),
                        nf.format(c.getAmountStdCurrency()),
                        c.getDetails()
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFile;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewTripRecord.this,SearchTrip.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
    /*        case R.id.viewrecordEditButton:
                final AlertDialog.Builder updateTrip = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.activity_new_trip, null);
                updateTrip.setView(dialogView);
                updateTrip.setTitle(getResources().getString(R.string.update_trip));

                final EditText mLabel, mCurrency, mBudget;
                final TextInputEditText mDescription;
                Button mSave, mCancel;

                mLabel = dialogView.findViewById(R.id.newtripLabel);
                mCurrency = dialogView.findViewById(R.id.newtripCurrency);
                mBudget = dialogView.findViewById(R.id.newtripBudget);

                mDescription = dialogView.findViewById(R.id.newtripDescription);
                mSave = dialogView.findViewById(R.id.newtripSaveB);
                mSave.setVisibility(View.GONE);
                mCancel = dialogView.findViewById(R.id.newtripCancelB);
                mCancel.setVisibility(View.GONE);


                mLabel.setText(selectedTrip.getLabel());
                mCurrency.setText(selectedTrip.getStdCurrency());
                mBudget.setText(selectedTrip.getBudget().toString());
                mDescription.setText(selectedTrip.getDescription());
                final Context context = this;

                updateTrip.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!mCurrency.getText().toString().isEmpty()){
                            Trip mTrip = new Trip();
                            mTrip.setID(selectedTrip.getID());
                            mTrip.setLabel(mLabel.getText().toString());
                            mTrip.setStdCurrency(mCurrency.getText().toString());
                            mTrip.setMultipleCurrency(true);
                            mTrip.setDescription(mDescription.getText().toString());
                            if(mBudget.getText().toString().isEmpty())
                                mTrip.setBudget(0.0f);
                            else
                                mTrip.setBudget(Float.valueOf(mBudget.getText().toString()));
                            dbHandler.updateTrip(mTrip);
                            updateHeader();
                        }
                        else{
                            String message;
                            if(mCurrency.getText().toString().isEmpty())
                                message = getResources().getString(R.string.please_insert_currency);
                            else
                                message = getResources().getString(R.string.please_insert_label_currency);
                            final android.app.AlertDialog.Builder mDialog = new android.app.AlertDialog.Builder(context);
                            mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            mDialog.setTitle(getResources().getString(R.string.update_infomration));
                            mDialog.setMessage(message);
                            mDialog.create().show();
                        }
                        dialog.dismiss();
                    }
                });

                updateTrip.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        updateTrip.create().show();
                break;

     */
            case id.viewrecordExchanceRate:
                intent = new Intent(this,TripCurrencies.class);
                intent.putExtra("TripID",tripID);
                startActivity(intent);
                finish();
                break;

            case id.viewrecordViewChart:
                intent = new Intent(v.getContext(), TripChart.class);
                intent.putExtra("TripID",tripID);
                v.getContext().startActivity(intent);
                finish();
                break;
        }

    }

    /*
    public void updateHeader(){
        selectedTrip = dbHandler.getTrip(tripID);
        mTrip.setText(selectedTrip.getLabel());
        mDescription.setText(selectedTrip.getDescription());
        mStdCurrency.setText(selectedTrip.getStdCurrency());
        displayBudget();
    }

     */

}
