package com.adammcneilly.helloworld.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * A ContentProvider is used to connect to an SQLiteDatabase on the Android phone.
 *
 * Created by adammcneilly on 2/22/16.
 */
@SuppressWarnings("ConstantConditions")
public class HWContentProvider extends ContentProvider {
    /**
     * Identifier for a query of all locations.
     */
    private static final int LOCATION = 0;

    /**
     * Identifier for a query of an individual location entry.
     */
    private static final int LOCATION_ID = 1;

    /**
     * Provides a connection to the Hello World database.
     */
    private HWOpenHelper mOpenHelper;

    /**
     * Used to match URIs with their respective queries.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Creates a UriMatcher object.
     * @return A UriMatcher used to determine which query should be run for the given Uri.
     */
    private static UriMatcher buildUriMatcher() {
        // We don't need a separate string for this, just did it for readability.
        String contentAuthority = HWContract.CONTENT_AUTHORITY;

        // Queries that include an id at the end will use /* to identify a wildcard parameter.
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(contentAuthority, HWContract.PATH_LOCATION, LOCATION);
        matcher.addURI(contentAuthority, HWContract.PATH_LOCATION + "/*", LOCATION_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HWOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                retCursor = db.query(
                        HWContract.LocationEntry.TABLE_NAME, // Table name
                        projection, // Columns
                        selection, // Selection clause
                        selectionArgs, // Selection arguments
                        null, // Group by
                        null, // Having
                        sortOrder // Sort
                );
                break;
            case LOCATION_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        HWContract.LocationEntry.TABLE_NAME,
                        projection,
                        HWContract.LocationEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set notifications so that followers of this Uri will get updates.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                return HWContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return HWContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri retUri;

        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                long _id = db.insert(HWContract.LocationEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    retUri = HWContract.LocationEntry.buildLocationUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set notification for followers of this uri
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows = 0;

        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                rows = db.delete(HWContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If any rows were deleted send notification
        if(rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows = 0;

        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                rows = db.update(HWContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If any rows were updated send notification
        if(rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows = 0;

        switch(sUriMatcher.match(uri)) {
            case LOCATION:
                //NOTE: In a perfect system, the bulk insert method should not be responsible
                // for clearing out old rows, that is not a scalable solution. However, since this
                // is a quick app to demonstrate my Android development skills, I made the developer
                // decision of putting it here for ease of implementation. The reason this was
                // easier is because I want to use a transaction - in other words, don't delete
                // old location information unless all the new information is added. If any new
                // information is unable to be added, the transaction will roll back and we
                // don't lose any data.
                db.beginTransaction();
                try {
                    db.delete(HWContract.LocationEntry.TABLE_NAME, null, null);
                    for(ContentValues contentValues : values) {
                        if(db.insert(HWContract.LocationEntry.TABLE_NAME, null, contentValues) > 0) {
                            rows++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // Notify and return rows
                getContext().getContentResolver().notifyChange(uri, null);
                Log.v("ADAM", "Rows were inserted.");
                return rows;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
