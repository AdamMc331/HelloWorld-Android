package com.adammcneilly.helloworld.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adammcneilly.helloworld.R;
import com.adammcneilly.helloworld.datatransferobjects.HWLocation;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * DialogFragment that shows a detailed view of the location.
 *
 * Created by adammcneilly on 2/22/16.
 */
public class DetailDialog extends DialogFragment{
    // Components
    private HWLocation mLocation;
    private double mLatitude;
    private double mLongitude;
    private ImageView mImageView;
    private Bitmap mImageBitmap;
    private TextView mName;
    private TextView mAddress;
    private TextView mDistance;
    private Button mCall;
    private Button mNavigate;

    // Arguments
    private static final String ARG_LOCATION = "argLocation";
    private static final String ARG_LATITUDE = "argLatitude";
    private static final String ARG_LONGITUDE = "argLongitude";

    public static DetailDialog NewInstance(HWLocation location, double latitude, double longitude) {
        DetailDialog dialog = new DetailDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = getArguments().getParcelable(ARG_LOCATION);
        mLatitude = getArguments().getDouble(ARG_LATITUDE);
        mLongitude = getArguments().getDouble(ARG_LONGITUDE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_detail, container, false);

        // Setup
        getUIElements(view);
        setFields();
        setClickListeners();

        // Load image
        new LoadImage().execute(mLocation.getImage());

        // Get window and set full width
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        return view;
    }

    /**
     * Retrieves all necessary UI components for this dialog.
     * @param view The root view that contains each of the components.
     */
    private void getUIElements(View view) {
        mImageView = (ImageView) view.findViewById(R.id.location_image);
        mName = (TextView) view.findViewById(R.id.location_name);
        mAddress = (TextView) view.findViewById(R.id.location_address);
        mDistance = (TextView) view.findViewById(R.id.location_distance);
        mCall = (Button) view.findViewById(R.id.call);
        mNavigate = (Button) view.findViewById(R.id.navigate);
    }

    /**
     * Displays all of the fields of this location in their appropriate component.
     */
    private void setFields() {
        mName.setText(mLocation.getName());
        mAddress.setText(mLocation.getFullAddress());

        // If latitude and longitude are 0, we don't have location so don't set this
        if(mLatitude == 0 && mLongitude == 0) {
            mDistance.setVisibility(View.GONE);
        } else {
            float[] distance = new float[1];
            Location.distanceBetween(mLatitude, mLongitude, mLocation.getLatitude(), mLocation.getLongitude(), distance);
            double kilometers = distance[0] / 1000.0;
            mDistance.setText(String.format(Locale.getDefault(), "%.2f km away", kilometers));
        }
    }

    /**
     * Sets the appropriate click listeners for the buttons in this dialog.
     */
    private void setClickListeners() {
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCallIntent();
            }
        });
        mNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigateIntent();
            }
        });
    }

    /**
     * Starts a navigation session from the user's current location to the location of this office.
     */
    private void startNavigateIntent() {
        // Navigate
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mLocation.getLatitude() + "," + mLocation.getLongitude());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    /**
     * Starts a phone call to the number given for this location.
     */
    private void startCallIntent() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mLocation.getPhone()));
        startActivity(intent);
    }

    /**
     * Asynchronous task that loads an image to be set to the ImageView.
     *
     * Asynchronous tasks are not a preferred solution, as they are tied to the activities lifecycle
     * and should not be used for long running tasks. However, since this is a quick individual call
     * and again I am trying to get something working, I have chosen to use AsyncTask.
     */
    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            try {
                mImageBitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImageBitmap;
        }

        protected void onPostExecute(Bitmap image) {
            if(image != null){
                mImageView.setImageBitmap(image);
            }
        }
    }
}
