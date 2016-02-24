package com.adammcneilly.helloworld.adapters;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adammcneilly.helloworld.R;
import com.adammcneilly.helloworld.data.HWContract;

import java.util.Locale;

/**
 * Adapter class used for displaying location information.
 *
 * Created by adammcneilly on 2/22/16.
 */
public class LocationAdapter extends CursorAdapter{
    // Create adapter with user coordinates
    private final double mLatitude;
    private final double mLongitude;

    /**
     * Alias for the derived distance column.
     */
    public static final String DISTANCE_ALIAS = "distance";

    /**
     * Returns a string for the column calculating distance between two points in SQLite.
     * @return A string used to query for locations by distance from the user.
     */
    private String getDistanceColumn() {
       return  "((" + mLatitude + " - " + HWContract.LocationEntry.COLUMN_LATITUDE + ") * (" + mLatitude + " - " + HWContract.LocationEntry.COLUMN_LATITUDE + ") " +
                " + " +
                "(" + mLongitude + " - " + HWContract.LocationEntry.COLUMN_LONGITUDE + ") * (" + mLongitude + " - " + HWContract.LocationEntry.COLUMN_LONGITUDE + ")) AS " + DISTANCE_ALIAS;
    }

    /**
     * Builds the array of location columns. A method is used in place of a static array because the distance column value changes based on the user's location.
     * @return A string array used for the projection of Location queries.
     */
    public String[] getLocationColumns() {
        return new String[] {
                HWContract.LocationEntry.TABLE_NAME + "." + HWContract.LocationEntry._ID,
                HWContract.LocationEntry.COLUMN_NAME,
                HWContract.LocationEntry.COLUMN_ADDRESS,
                HWContract.LocationEntry.COLUMN_ADDRESS_TWO,
                HWContract.LocationEntry.COLUMN_CITY,
                HWContract.LocationEntry.COLUMN_STATE,
                HWContract.LocationEntry.COLUMN_ZIP,
                HWContract.LocationEntry.COLUMN_PHONE,
                HWContract.LocationEntry.COLUMN_FAX,
                HWContract.LocationEntry.COLUMN_LATITUDE,
                HWContract.LocationEntry.COLUMN_LONGITUDE,
                HWContract.LocationEntry.COLUMN_IMAGE,
                getDistanceColumn()
        };
    }

    // Indexes of each column so their value can be read easily without calling `getColumnIndex()`
    // If the projection ever changes, these must change too.
    public static final int NAME_INDEX = 1;
    public static final int ADDRESS_INDEX = 2;
    public static final int ADDRESS_TWO_INDEX = 3;
    public static final int CITY_INDEX = 4;
    public static final int STATE_INDEX = 5;
    public static final int ZIP_INDEX = 6;
    public static final int LATITUDE_INDEX = 9;
    public static final int LONGITUDE_INDEX = 10;

    public LocationAdapter(Context context, double latitude, double longitude) {
        super(context, null, 0);
        mLatitude = latitude;
        mLongitude = longitude;
        Log.v("ADAM", latitude + ", " + longitude);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_location, parent, false);
        view.setTag(new LocationViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        LocationViewHolder viewHolder = (LocationViewHolder) view.getTag();
        viewHolder.bindCursor(cursor);
    }

    /**
     * ViewHolder class that is used to provide smoother scrolling of the ListView component.
     */
    public class LocationViewHolder {
        private final TextView mName;
        private final TextView mAddress;
        private final TextView mDistance;

        public LocationViewHolder(View view) {
            mName = (TextView) view.findViewById(R.id.location_name);
            mAddress = (TextView) view.findViewById(R.id.location_address);
            mDistance = (TextView) view.findViewById(R.id.location_distance);
        }

        /**
         * Binds an entry of the Location table to this list item.
         * @param cursor The entry that is to be displayed.
         */
        public void bindCursor(Cursor cursor) {
            mName.setText(cursor.getString(NAME_INDEX));

            String address = cursor.getString(ADDRESS_INDEX);
            String address2 = cursor.getString(ADDRESS_TWO_INDEX);
            String city = cursor.getString(CITY_INDEX);
            String state = cursor.getString(STATE_INDEX);
            String zip = cursor.getString(ZIP_INDEX);

            mAddress.setText(String.format("%s\n%s\n%s, %s %s", address, address2, city, state, zip));

            // If latitude and longitude are 0, we don't have location so don't set this
            if(mLatitude == 0 && mLongitude == 0) {
                mDistance.setVisibility(View.GONE);
            } else {
                double officeLatitude = cursor.getDouble(LATITUDE_INDEX);
                double officeLongitude = cursor.getDouble(LONGITUDE_INDEX);
                float[] distance = new float[1];
                Location.distanceBetween(mLatitude, mLongitude, officeLatitude, officeLongitude, distance);
                double kilometers = distance[0] / 1000.0;
                mDistance.setText(String.format(Locale.getDefault(), "%.2f km", kilometers));
            }
        }
    }
}
