package com.odairtns.trackyourexpenses.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.odairtns.trackyourexpenses.Data.DbHandler;
import com.odairtns.trackyourexpenses.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mNewTrip, mSearchTrip, mConfigExpType, mBackup, mRestore;
    private ImageView passport;
    private static final int REQUEST_STORAGE_CODE = 101;

    private DbHandler dbHandler;


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
*/          dbHandler = new DbHandler(this);
            mNewTrip = findViewById(R.id.mainNewTrip);
            mNewTrip.setOnClickListener(this);
            mSearchTrip = findViewById(R.id.mainSearchTrip);
            mSearchTrip.setOnClickListener(this);
            mConfigExpType = findViewById(R.id.mainConfigExpType);
            mConfigExpType.setOnClickListener(this);
            passport = findViewById(R.id.passport);
            passport.setOnClickListener(this);
            mBackup = findViewById(R.id.mainBackup); // New button for backup/restore
            mBackup.setOnClickListener(this);
            mRestore = findViewById(R.id.mainRestore);
            mRestore.setOnClickListener(this);

            requestStoragePermissions();
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
            case R.id.mainBackup:
                backupDatabase();
                break;
            case R.id.mainRestore:
                restoreDatabase();
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

    public void backupDatabase() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    // Get Downloads directory
                    File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    if (downloadsDir != null && downloadsDir.canWrite()) {
                        String backupFilename = "track_your_expenses_backup.db"; // Replace with your desired filename
                        String databaseName = dbHandler.getDatabaseName();
                        File currentDB = new File(this.getDatabasePath(databaseName).toString());
                        File backupDB = new File(downloadsDir, backupFilename);

                        if (currentDB.exists()) {
                            FileChannel src = new FileInputStream(currentDB).getChannel();
                            FileChannel dst = new FileOutputStream(backupDB).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            Toast.makeText(this, "Database backed up to Downloads folder", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IOException e) {
                    Log.e("DatabaseHelper", "Error backing up database: " + e.getMessage());
                }
            }
        }

            // Restore database
            public void restoreDatabase () {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        if (downloadsDir != null) {
                            String backupFilename = "your_app_name_backup.db"; // Replace with your backup filename
                            File backupDB = new File(downloadsDir, backupFilename);
                            String databaseName = dbHandler.getDatabaseName();
                            File currentDB = new File(this.getDatabasePath(databaseName).toString());

                            if (backupDB.exists()) {
                                FileChannel src = new FileInputStream(backupDB).getChannel();
                                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                                dst.transferFrom(src, 0, src.size());
                                src.close();
                                dst.close();
                                Toast.makeText(this, "Database restored from Downloads folder", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Backup file not found in Downloads folder", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        Log.e("DatabaseHelper", "Error restoring database: " + e.getMessage());
                    }
                }
            }

    //Ask por storage permission

    private void requestStoragePermissions() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted
                // Perform your storage access operations here
            } else {
                // Permission denied
                // Handle the case where the user denies the permission
                Toast.makeText(this, "Storage permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
