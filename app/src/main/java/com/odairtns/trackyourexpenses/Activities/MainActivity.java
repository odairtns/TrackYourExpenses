package com.odairtns.trackyourexpenses.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.odairtns.trackyourexpenses.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mNewTrip, mSearchTrip, mConfigExpType;
    private ImageView passport;

    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        /*preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String lastActivity = preferences.getString("lastActivity", null);

        if (lastActivity != null) {
            try {
                Class<?> activityClass = Class.forName(lastActivity);
                Intent intent = new Intent(this, activityClass);
                startActivity(intent);
                finish(); // Finish the current MainActivity
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
*/
            mNewTrip = findViewById(R.id.mainNewTrip);
            mNewTrip.setOnClickListener(this);
            mSearchTrip = findViewById(R.id.mainSearchTrip);
            mSearchTrip.setOnClickListener(this);
            mConfigExpType = findViewById(R.id.mainConfigExpType);
            mConfigExpType.setOnClickListener(this);
            passport = findViewById(R.id.passport);
            passport.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainNewTrip:
                startActivity(new Intent(MainActivity.this,NewTrip.class));
                finish();
                break;
            case R.id.mainSearchTrip:
                startActivity(new Intent(MainActivity.this,SearchTrip.class));
                finish();
                break;
            case R.id.mainConfigExpType:
                startActivity(new Intent(MainActivity.this,ExpenseType.class));
                finish();
                break;

        }

        // Save the name of the current activity as the last used activity
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.apply();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_settings, menu);
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.mainsettingsAbout){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.aboutmessage));
            dialog.setTitle(getResources().getString(R.string.about));
            dialog.setIcon(android.R.drawable.ic_dialog_info);

            dialog.setCancelable(true);
            dialog.create().show();

        }
        return super.onOptionsItemSelected(item);
    }



}
