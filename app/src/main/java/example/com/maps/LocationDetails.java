package example.com.maps;

import android.graphics.Bitmap;

public class LocationDetails {
String latitude,longitude,describe,city;
    Bitmap img;
    LocationDetails(String latitude, String longitude, String describe,String city,Bitmap img) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.describe = describe;
        this.city=city;
        this.img=img;
    }

    public String getCity() {
        return city;
    }

    public Bitmap getImg() {
        return img;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
