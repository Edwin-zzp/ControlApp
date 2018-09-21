package com.example.zhang.controlapp;


import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import com.example.zhang.controlapp.data.DbProvider;

public class BaseActivity extends AppCompatActivity{
    protected DbProvider mDbProvider = null;

    /**
     * Upon activity creation opens a DB connection.
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openDb();
    }

    /**
     * Upon activity destruction closes DB connection.
     * @see android.support.v7.app.AppCompatActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDb();
    }

    /**
     * Instantiates new DbProvider and opens a DB connection.
     */
    private void openDb() {
        mDbProvider = new DbProvider(this);
        mDbProvider.open();
    }

    /**
     * Closes DB connection.
     */
    private void closeDb() {
        mDbProvider.close();
    }
}
