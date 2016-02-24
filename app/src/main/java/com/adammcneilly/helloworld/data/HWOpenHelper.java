package com.adammcneilly.helloworld.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the creation and updates of the Hello World database.
 *
 * Created by adammcneilly on 2/22/16.
 */
class HWOpenHelper extends SQLiteOpenHelper {
    /**
     * The name of the database to be created.
     */
    private static final String DATABASE_NAME = "helloworld.db";

    /**
     * The current database version.
     */
    private static final int DATABASE_VERSION = 1;

    public HWOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLocationTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Creates the location table.
     * @param db The database to create the location table in.
     */
    private void createLocationTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + HWContract.LocationEntry.TABLE_NAME + " (" +
                        HWContract.LocationEntry._ID + " INTEGER PRIMARY KEY, " +
                        HWContract.LocationEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_ADDRESS + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_ADDRESS_TWO + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_CITY + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_STATE + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_ZIP + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_FAX + " TEXT NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                        HWContract.LocationEntry.COLUMN_IMAGE + " TEXT NOT NULL);"
        );
    }
}
