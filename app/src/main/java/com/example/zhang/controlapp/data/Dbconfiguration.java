package com.example.zhang.controlapp.data;

public class Dbconfiguration {

    // DB info
    public static final String DATABASE_NAME = "TvConDB";
    public static final String DATABASE_TABLE = "device";
    public static final String DATABASE_TABLE_TMP = "tmp_device";
    public static final int DATABASE_VERSION = 2;

    // For logging
    protected static final String TAG = "DbProvider";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;

    // "device" table fields
    public static final String KEY_NAME = "name";

    public static final String KEY_HOST = "host";

    public static final String KEY_PORT = "port";



    // "device" table fields (old)
    //public static final String KEY_IP = "ip";

    // "device" table field numbers
    public static final int COL_NAME = 1;

    public static final int COL_HOST = 2;

    public static final int COL_PORT = 3;


    public static final String[] ALL_KEYS_V1 = new String[] {
            KEY_ROWID, KEY_NAME, KEY_PORT
    };

    public static final String[] ALL_KEYS_V2 = new String[] {
            KEY_ROWID, KEY_NAME,  KEY_HOST, KEY_PORT
    };

    protected static final String DATABASE_CREATE_SQL_V1 = ""
            //+ "drop table if exists " + DATABASE_TABLE + "; "
            + "create table " + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_NAME + " string not null, "
            + KEY_PORT + " integer not null "
            + "); ";

    protected static final String DATABASE_CREATE_SQL_V2 = ""
            //+ "drop table if exists " + DATABASE_TABLE + "; "
            + "create table " + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_NAME + " string not null, "
            + KEY_HOST + " string null, "
            + KEY_PORT + " integer not null "
            + "); ";

    // V1 - Adds default DB fields
    protected static final String DATABASE_PATCH_SQL_V1_1 = DATABASE_CREATE_SQL_V1;

    // V2 - Adds "HOST" field, and renames "IP" to "BROADCAST"
    protected static final String DATABASE_PATCH_SQL_V2_1 = ""
            + "alter table " + DATABASE_TABLE + " rename to " + DATABASE_TABLE_TMP;

    protected static final String DATABASE_PATCH_SQL_V2_2 = ""
            + DATABASE_CREATE_SQL_V2;

    protected static final String DATABASE_PATCH_SQL_V2_3 = ""
            + "insert into " + DATABASE_TABLE + " ("
            + KEY_ROWID + ", "
            + KEY_NAME + ", "
            + KEY_PORT + ", "
            + "select "
            + KEY_ROWID + ", "
            + KEY_NAME + ", "
            + KEY_PORT + ", "
            + "from " + DATABASE_TABLE_TMP;

    protected static final String DATABASE_PATCH_SQL_V2_4 = ""
            + "drop table if exists " + DATABASE_TABLE_TMP;

    public static final String[] ALL_KEYS = ALL_KEYS_V2;
    protected static final String DATABASE_CREATE_SQL = DATABASE_CREATE_SQL_V2;
}
