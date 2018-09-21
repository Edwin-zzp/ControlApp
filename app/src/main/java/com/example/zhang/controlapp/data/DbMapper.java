package com.example.zhang.controlapp.data;

import android.database.Cursor;

import com.example.zhang.controlapp.models.DeviceModel;

import java.util.ArrayList;
import java.util.List;

public class DbMapper extends Dbconfiguration{
    /**
     * Maps DB device records to DeviceModel entities.
     * @param cursor        DB cursor.
     * @return              A list of DeviceModel entities.
     */
    public List<DeviceModel> mapDevices(Cursor cursor) {
        List<DeviceModel> devices = new ArrayList<DeviceModel>();

        if (cursor.moveToFirst()) {
            do {
                devices.add(mapDevice(cursor));
            } while(cursor.moveToNext());
        }

        return devices;
    }

    /**
     * Maps DB device record to DeviceModel entity.
     * @param cursor        DB cursor.
     * @return              DeviceModel entity.
     */
    public DeviceModel mapDevice(Cursor cursor) {
        DeviceModel device = null;

        if (cursor != null) {
            device = new DeviceModel(
                    cursor.getLong(COL_ROWID),
                    cursor.getString(COL_NAME),

                    cursor.isNull(COL_HOST) ? "" : cursor.getString(COL_HOST), // otherwise null return an empty string

                    cursor.getInt(COL_PORT)

            );
        }

        return device;
    }

}
