package com.dev.cromer.jason.takeastand;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MarkerCreationActivity extends AppCompatActivity {

    static ArrayAdapter<String> spinnerArrayAdapter;
    private Spinner religionSpinner;
    private String[] spinnerItems = {"Choose Your Religion", "Christian", "Islam", "Catholic", "Hindu", "Buddhist", "Agnostic", "Athiest"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_creation);

        religionSpinner = (Spinner) findViewById(R.id.spinner);
        setUpSpinnerAdapter();
    }




    private void setUpSpinnerAdapter() {
        spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item,
                spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_item);
        religionSpinner.setAdapter(spinnerArrayAdapter);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_marker_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
