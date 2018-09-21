package com.example.zhang.controlapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zhang.controlapp.models.DeviceModel;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



    }



    /**
     * Upon options menu creation inflates the menu and sets
     * button functionalities and visibility.
     * @param menu      Menu entity to inflate.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        menu.findItem(R.id.action_save).setVisible(true);
        menu.findItem(R.id.action_help).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);
        menu.findItem(R.id.action_new).setVisible(false);
        return true;
    }



    /**
     * Routes to appropriate action upon clicking on an item from the actionBar menu.
     * @param item      Clicked item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_save:
//                Intent settingsIntent = new Intent(MainActivity.this, SettingActivity.class);
//                startActivity(settingsIntent);
                break;
        }
        return true;
    }
}
