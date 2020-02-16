package edu.fullsail.mgems.cse.pather.christopherwest.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.fullsail.mgems.cse.pather.christopherwest.R;
import edu.fullsail.mgems.cse.pather.christopherwest.views.DrawSurface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawSurface ds;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemSettings) {
            goToSettingsActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSettingsActivity(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnCredits).setOnClickListener(this);
        findViewById(R.id.btnNewMap).setOnClickListener(this);
        ds = findViewById(R.id.dsField);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnNewMap) {
            ds.loadNewMap();
        }
        if (v.getId() == R.id.btnCredits) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
    }
}
