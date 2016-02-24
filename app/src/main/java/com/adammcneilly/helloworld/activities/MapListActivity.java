package com.adammcneilly.helloworld.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.adammcneilly.helloworld.R;
import com.adammcneilly.helloworld.adapters.LocationAdapter;
import com.adammcneilly.helloworld.data.HWContract;
import com.adammcneilly.helloworld.datatransferobjects.HWLocation;
import com.adammcneilly.helloworld.fragments.DetailDialog;
import com.adammcneilly.helloworld.service.LocationService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Activity that is responsible for displaying the map of all hello world locations as well as the
 * list of locations.
 */
public class MapListActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Adapter for displaying Hello World locations in a listview.
     */
    private LocationAdapter mLocationAdapter;

    /**
     * A GoogleMap fragment that displays a map of the hello world locations.
     */
    private GoogleMap mLocationMap;

    /**
     * The current latitude of the user's location.
     */
    private double mLatitude;

    /**
     * The current longitude of the user's location.
     */
    private double mLongitude;

    /**
     * Request identifier for asking the user location permissions.
     */
    private static final int LOCATION_REQUEST = 0;

    /**
     * Identifier for the location cursor loader.
     */
    private static final int LIST_LOCATION_LOADER = 0;

    /**
     * Identifier for the cursor loader for map items.
     */
    private static final int MAP_LOCATION_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);

        // Get toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // For ease of implementation, I have just created an IntentService that will run on a
        // separate thread to pull location information. In a formal system, a sync adapter should
        // be used to do this at a given interval.
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Prepares listview.
        setupListView();
    }

    /**
     * Creates a reference to the ListView component and sets the adapter to it.
     */
    private void setupListView() {
        // Get reference
        ListView mLocationListView = (ListView) findViewById(R.id.location_list_view);

        // Need Lat/Lng before creating adapter.
        getLatitudeLongitude();
        mLocationAdapter = new LocationAdapter(this, mLatitude, mLongitude);
        mLocationListView.setAdapter(mLocationAdapter);

        // When a location is clicked, show dialog for that location.
        mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetailDialog(new HWLocation((Cursor) mLocationAdapter.getItem(position)));
            }
        });
    }

    /**
     * Received help from StackOverflow: http://stackoverflow.com/a/9563438/3131147
     * Need this so we can set reasonable margins around map markers so it looks consistent
     * across multiple devices.
     *
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private int dpToPixels(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(LIST_LOCATION_LOADER, null, this);
    }

    /**
     * From the Maps Sample Activity:
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mLocationMap = googleMap;

        // Start loader when map is ready so that markers aren't added until the map is ready.
        getSupportLoaderManager().initLoader(MAP_LOCATION_LOADER, null, this);
    }

    /**
     * Displays a dialog fragment of details about a location.
     * @param location The location to show details for.
     */
    private void showDetailDialog(HWLocation location) {
        DetailDialog detailDialog = DetailDialog.NewInstance(location, mLatitude, mLongitude);
        detailDialog.show(getSupportFragmentManager(), "locationDetails");
    }

    /**
     * Retrieves the user's current latitude and longitude. If permissions are not granted, it will prompt the user for them.
     */
    private void getLatitudeLongitude() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case LOCATION_REQUEST:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLatitudeLongitude();
                    // Reset loader
                    getSupportLoaderManager().restartLoader(LIST_LOCATION_LOADER, null, this);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LIST_LOCATION_LOADER:
            case MAP_LOCATION_LOADER:
                // If we have a latitude and longitude, sort by distance.
                // Otherwise, sort by name.
                String sortColumn = (mLatitude != 0 && mLongitude != 0) ? LocationAdapter.DISTANCE_ALIAS : HWContract.LocationEntry.COLUMN_NAME;
                return new CursorLoader(
                        this,
                        HWContract.LocationEntry.CONTENT_URI,
                        mLocationAdapter.getLocationColumns(),
                        null,
                        null,
                        sortColumn
                );
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case LIST_LOCATION_LOADER:
                mLocationAdapter.swapCursor(data);
                break;
            case MAP_LOCATION_LOADER:
                // Set locations
                // Builder to show multiple markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                while(data.moveToNext()) {
                    LatLng marker = new LatLng(data.getLong(LocationAdapter.LATITUDE_INDEX), data.getLong(LocationAdapter.LONGITUDE_INDEX));
                    builder.include(marker);
                    mLocationMap.addMarker(new MarkerOptions().position(marker).title(data.getString(LocationAdapter.NAME_INDEX)).icon(BitmapDescriptorFactory.defaultMarker(16)));
                }

                // If no markers were added, just return otherwise we get an error.
                //TODO: Doesn't seem right, but can't think of a way around this yet.
                if(data.getCount() == 0) {
                    return;
                }

                // Set bounds so camera can include all markers
                LatLngBounds bounds = builder.build();
                int padding = dpToPixels(16);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mLocationMap.moveCamera(cu);
                break;

            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch(loader.getId()) {
            case LIST_LOCATION_LOADER:
                mLocationAdapter.swapCursor(null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id: " + loader.getId());
        }
    }
}
