package com.adammcneilly.helloworld.datatransferobjects;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.adammcneilly.helloworld.data.HWContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a location of a Hello World office.
 *
 * Created by adammcneilly on 2/22/16.
 */
public class HWLocation implements Parcelable{
    private String name;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String fax;
    private long latitude;
    private long longitude;
    private String image;

    public static Creator<HWLocation> CREATOR = new Creator<HWLocation>() {
        @Override
        public HWLocation createFromParcel(Parcel source) {
            return new HWLocation(source);
        }

        @Override
        public HWLocation[] newArray(int size) {
            return new HWLocation[size];
        }
    };

    public HWLocation(Cursor cursor) {
        setName(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_NAME)));
        setAddress(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_ADDRESS)));
        setAddress2(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_ADDRESS_TWO)));
        setCity(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_CITY)));
        setState(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_STATE)));
        setZip(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_ZIP)));
        setPhone(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_PHONE)));
        setFax(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_FAX)));
        setLatitude(cursor.getLong(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_LATITUDE)));
        setLongitude(cursor.getLong(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_LONGITUDE)));
        setImage(cursor.getString(cursor.getColumnIndex(HWContract.LocationEntry.COLUMN_IMAGE)));
    }

    public HWLocation(JSONObject jsonObject) throws JSONException{
        setName(jsonObject.getString(HWContract.LocationEntry.COLUMN_NAME));
        setAddress(jsonObject.getString(HWContract.LocationEntry.COLUMN_ADDRESS));
        setAddress2(jsonObject.getString(HWContract.LocationEntry.COLUMN_ADDRESS_TWO));
        setCity(jsonObject.getString(HWContract.LocationEntry.COLUMN_CITY));
        setState(jsonObject.getString(HWContract.LocationEntry.COLUMN_STATE));
        setZip(jsonObject.getString(HWContract.LocationEntry.COLUMN_ZIP));
        setPhone(jsonObject.getString(HWContract.LocationEntry.COLUMN_PHONE));
        setFax(jsonObject.getString(HWContract.LocationEntry.COLUMN_FAX));
        setLatitude(jsonObject.getLong(HWContract.LocationEntry.COLUMN_LATITUDE));
        setLongitude(jsonObject.getLong(HWContract.LocationEntry.COLUMN_LONGITUDE));
        setImage(jsonObject.getString(HWContract.LocationEntry.COLUMN_IMAGE));
    }

    private HWLocation(Parcel parcel) {
        setName(parcel.readString());
        setAddress(parcel.readString());
        setAddress2(parcel.readString());
        setCity(parcel.readString());
        setState(parcel.readString());
        setZip(parcel.readString());
        setPhone(parcel.readString());
        setFax(parcel.readString());
        setLatitude(parcel.readLong());
        setLongitude(parcel.readLong());
        setImage(parcel.readString());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Builds the full address of the location.
     * @return A string representing the entire address of this location.
     */
    public String getFullAddress() {
        return String.format("%s\n%s\n%s, %s %s", getAddress(), getAddress2(), getCity(), getState(), getZip());
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        // If image uses http:// instead of https://, change it.
        this.image = image.replace("http:", "https:");
    }

    /**
     * Builds the contentvalues for this object.
     * @return A ContentValues object used to insert an entry for this object in the database.
     */
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(HWContract.LocationEntry.COLUMN_NAME, getName());
        values.put(HWContract.LocationEntry.COLUMN_ADDRESS, getAddress());
        values.put(HWContract.LocationEntry.COLUMN_ADDRESS_TWO, getAddress2());
        values.put(HWContract.LocationEntry.COLUMN_CITY, getCity());
        values.put(HWContract.LocationEntry.COLUMN_STATE, getState());
        values.put(HWContract.LocationEntry.COLUMN_ZIP, getZip());
        values.put(HWContract.LocationEntry.COLUMN_PHONE, getPhone());
        values.put(HWContract.LocationEntry.COLUMN_FAX, getFax());
        values.put(HWContract.LocationEntry.COLUMN_LATITUDE, getLatitude());
        values.put(HWContract.LocationEntry.COLUMN_LONGITUDE, getLongitude());
        values.put(HWContract.LocationEntry.COLUMN_IMAGE, getImage());
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeString(getAddress());
        dest.writeString(getAddress2());
        dest.writeString(getCity());
        dest.writeString(getState());
        dest.writeString(getZip());
        dest.writeString(getPhone());
        dest.writeString(getFax());
        dest.writeLong(getLatitude());
        dest.writeLong(getLongitude());
        dest.writeString(getImage());
    }
}
