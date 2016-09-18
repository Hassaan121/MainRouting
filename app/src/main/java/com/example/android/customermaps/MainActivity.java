package com.example.android.customermaps;

import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

//Implemented http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
// https://github.com/stephenbaidu/android-place-picker



// Routing http://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android
//https://www.youtube.com/watch?v=qpkcvUlc7ms
//https://code.tutsplus.com/articles/google-play-services-using-the-places-api--cms-23715
// PolyLines http://stackoverflow.com/questions/17425499/how-to-draw-interactive-polyline-on-route-google-maps-v2-android
// MAIN VVVVIP http://code.tutsplus.com/tutorials/getting-started-with-google-maps-for-android-basics--cms-24635
public class MainActivity extends AppCompatActivity {
    TextView tv;
    GoogleApiClient mClient;
    private static final int PLACE_PICKER_REQUEST = 101;
    private static LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        mClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mClient.connect();


        Intent intent = new Intent(MainActivity.this, InsertMapsActivity1.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     setLocation();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    protected void setLocation(double x, double y) {
        AddPlaceRequest place =
                new AddPlaceRequest(
                        "Manly Sea Life Sanctuary", // Name
                        // new LatLng(-33.7991, 151.2813), // Latitude and longitude
                        new LatLng(x, y), // Latitude and longitude
                        "W Esplanade, Manly NSW 2095", // Address
                        Collections.singletonList(Place.TYPE_AQUARIUM), // Place types
                        "+92 1800 199 742", // Phone number
                        //         Uri.parse("http://www.manlysealifesanctuary.com.au/") // Website
                        Uri.parse("karachi") // Website
                );

        Places.GeoDataApi.addPlace(mClient, place)

                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        //       Log.i(TAG, "Place add result: " + places.getStatus().toString());
                        //       Log.i(TAG, "Added place: " + places.get(0).getName().toString());
                        places.release();
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                double x = 0, y = 0;


                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                Geocoder coder = new Geocoder(this);
                List<Address> address;

                try {
                    address = coder.getFromLocationName(place.getName().toString(), 5);
                    if (address == null) {
                        //    return null;
                    }
                    Address location = address.get(0);
                    x = location.getLatitude();
                    y = location.getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setLocation(x, y);
                BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
                        new LatLng(x, y), new LatLng(x + 0.0000000000001, y + 0.000000000001));


                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                builder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

//https://developers.google.com/places/android-api/photos

            }
        }
    }
}



