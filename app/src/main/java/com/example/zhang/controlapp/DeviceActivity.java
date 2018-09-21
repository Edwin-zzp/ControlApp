package com.example.zhang.controlapp;



import android.support.v7.app.ActionBar;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.zhang.controlapp.common.CustomTextWatcher;
import com.example.zhang.controlapp.models.DeviceModel;

import java.io.IOException;
import java.io.InputStream;

public class DeviceActivity extends BaseActivity {
    private DeviceModel mDevice = null;
    private FormItems mFormItems = null;

    /**
     * Upon activity creation initializes form items, set mDevice to existing
     * or new device sets form value, and registers callbacks.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        initializeFormItems();
        ensureDeviceExists();
        setFormValues();
//        registerSwitchCallbacks();
//        registerDiscoverSSIDCallback();
//        registerLinearLayoutButtonsCallbacks();
        registerAfterTextChangedCallbacks();
//        registerAfterMacTextChangedCallback();
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
        menu.findItem(R.id.action_new).setVisible(false);
        menu.findItem(R.id.action_help).setVisible(true);
        menu.findItem(R.id.action_settings).setVisible(false);

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
            case R.id.action_help:
                displayHelpDialog();
                break;
            case R.id.action_save:
                if (validateFormValues()) {
                    getFormValues();
                    saveDeviceToDb();
                    this.finish();
                }
                break;
        }
        return true;
    }


    /**
     * Registers new CustomTextWatcher() to all EditText fields listener callbacks. Used
     * to remove validation error when the user starts typing into an EditText field.
     */
    private void registerAfterTextChangedCallbacks() {
        mFormItems.nameEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.nameEdit));
        //mFormItems.macEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.macEdit));
        mFormItems.hostEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.hostEdit));
        mFormItems.portEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.portEdit));
       // mFormItems.ssidEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.ssidEdit));
       // mFormItems.idleTimeEdit.addTextChangedListener(new CustomTextWatcher(mFormItems.idleTimeEdit));
    }


    /**
     * Sets the mDevice entity. If the the serializable deviceObject
     * is null, creates a new mDevice entity.
     */
    private void ensureDeviceExists() {
        Intent intent = getIntent();
        DeviceModel device = (DeviceModel)intent.getSerializableExtra("deviceObject");

        if (device == null) {
            mDevice = new DeviceModel();
        } else {
            mDevice = device;
        }
    }

    /**
     * Saves mDevice object values to DB.
     */
    private void saveDeviceToDb() {
        if (mDevice.getId() == -1) {
            mDbProvider.insertDevice(mDevice);
        } else {
            mDbProvider.updateDevice(mDevice);
        }
    }

    /**
     * Reads mDevice entity property values and sets them as activity form
     * values. Also ensures that form switches are checked or unchecked
     * depending on whether their nested values are set or not.
     */
    private void setFormValues() {
        mFormItems.nameEdit.setText(mDevice.getName());
       // mFormItems.macEdit.setText(mDevice.getMac());

        if (mDevice.getHost() != null) {
            mFormItems.hostEdit.setText(mDevice.getHost());
        }

        if (mDevice.getPort() != null) {
            mFormItems.portEdit.setText(mDevice.getPort().toString());
        }


    }

    /**
     * Gets values from activity form and maps them to mDevice entity.
     * "Nulls" the values for unchecked switches.
     */
    private void getFormValues() {
        mDevice.setName(mFormItems.nameEdit.getText().toString());
        //mDevice.setMac(mFormItems.macEdit.getText().toString());
        mDevice.setHost(mFormItems.hostEdit.getText().toString());
        mDevice.setPort(Integer.parseInt(mFormItems.portEdit.getText().toString()));


    }

    /**
     * Validates form values and sets their error messages if the validation failed.
     * @return      Is form valid?
     */
    private Boolean validateFormValues() {
        Boolean isValid = true;
        resetFormErrors();

        // Name
        Editable name = mFormItems.nameEdit.getText();
        if (name == null || name.toString().isEmpty()) {
            mFormItems.nameEdit.setError("名称不能为空！");
            isValid = false;
        }


        // Ip
        Editable ip = mFormItems.hostEdit.getText();
        if (ip == null || ip.toString().isEmpty()) {
            mFormItems.hostEdit.setError("IP地址不能为空");
            isValid = false;
        }

        // Port
        Editable port = mFormItems.portEdit.getText();
        if (port == null || port.toString().isEmpty()) {
            mFormItems.portEdit.setError("端口不能为空");
            isValid = false;
        }


        return isValid;
    }

    /**
     * Clears all form errors.
     */
    private void resetFormErrors() {
        mFormItems.nameEdit.setError(null);
       // mFormItems.macEdit.setError(null);
        mFormItems.hostEdit.setError(null);
        mFormItems.portEdit.setError(null);
        mFormItems.nameEdit.setError(null);
    }

    /**
     * Displays the help dialog containing assets/Help.html data.
     */
    private void displayHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help")
                .setMessage(getHelpHtml())
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextView textView = (TextView)alertDialog.findViewById(android.R.id.message);
        textView.setTextSize(14);
    }

    /**
     * Reads assets/Help.html file data as a String.
     * @return      Help.html data as a string.
     */
    private CharSequence getHelpHtml() {
        InputStream inputStream;
        String html = null;

        try {
            inputStream = getAssets().open("Help.html");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            html = new String(buffer);
            inputStream.close();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
    }

    /**
     * Populates mFormItems entity with new FormItems entity.
     */
    private void initializeFormItems() {
        mFormItems = createFormItems();
    }

    /**
     * Creates a new FormItems entity containing form view entities found
     * by their R.Ids and cast to appropriate types.
     * @return      FormItems entity.
     */
    private FormItems createFormItems() {
        FormItems formItems = new FormItems();

        // EditText
        formItems.nameEdit = (EditText)findViewById(R.id.edit_name);
        formItems.hostEdit = (EditText)findViewById(R.id.edit_host);
        //formItems.macEdit = (EditText)findViewById(R.id.edit_mac);
        formItems.portEdit = (EditText)findViewById(R.id.edit_port);

        return formItems;
    }

    /**
     * FormItems class containing definitions of all needed view elements. Used in parent class
     * to access view elements more easily without the need to find them by Id each time.
     */
    private class FormItems {
        // EditText
        EditText nameEdit;
        EditText hostEdit;
       // EditText macEdit;
        EditText portEdit;
      }
}
