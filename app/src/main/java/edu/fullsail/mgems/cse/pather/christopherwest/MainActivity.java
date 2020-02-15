package edu.fullsail.mgems.cse.pather.christopherwest;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import edu.fullsail.mgems.cse.pather.christopherwest.views.DrawSurface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawSurface ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnCredits).setOnClickListener(this);
        ds = findViewById(R.id.dsField);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (v.getId() == R.id.btnCredits) {
            dialog.setTitle("Made by");
            dialog.setMessage("Christopher West\nMBG521-O | Computer Science for Engineers\n02/15/2020");
            dialog.setPositiveButton(" OK ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }
}
