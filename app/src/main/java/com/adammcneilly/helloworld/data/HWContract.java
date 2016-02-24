package com.adammcneilly.helloworld.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * This class maintains the schema and necessary URI paths for the Hello World database.
 *
 * Created by adammcneilly on 2/22/16.
 */
public class HWContract {
    /**
     * The name for the entire content provider. Since the package name for applications is
     * guaranteed to be unique for each app, it is a good name to use for the content authority.
     */
    public static final String CONTENT_AUTHORITY = "com.adammcneilly.helloworld";

    /**
     * The base of all URIs that connect to the content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path that is appended to the base URI for location queries.
     */
    public static final String PATH_LOCATION = "location";

    /**
     * This class defines the column names, URIs, and mime types for the Location table.
     *
     * Each column value is the same as it's appropriate key in the JSON object so that these
     * constants can be used to pull from the JSONObject.
     */
    public static final class LocationEntry implements BaseColumns {
        /**
         * Uri for a location query.
         */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        /**
         * Mime type for location queries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        /**
         * Mime type for individual location queries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Schema
        public static final String TABLE_NAME = "locationTable";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_ADDRESS_TWO = "address2";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip_postal_code";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_FAX = "fax";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_IMAGE = "office_image";

        /**
         * Builds a URI for a specific location.
         * @param id The identifier for the location.
         * @return A URI pointing to an individual location entry.
         */
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
