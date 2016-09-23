package com.example.android.customermaps;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.HttpGet;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

// FullSearcher https://www.drzon.net/how-to-create-a-clearable-autocomplete-dropdown-with-autocompletetextview/
// Searcher with LISTVIEW http://www.androidhive.info/2012/09/android-adding-search-functionality-to-listview/
// FUll Project https://github.com/stephenbaidu/android-place-picker/blob/master/placepicker/src/main/java/com/github/stephenbaidu/placepicker/PlacePicker.java
// SearchView http://stackoverflow.com/questions/11491515/turn-autocompletetextview-into-a-searchview-in-actionbar-instead
// search http://codetheory.in/adding-search-to-android/
//  http://stackoverflow.com/questions/36449891/how-to-make-a-custom-place-picker-for-andorid/36451463

public class InsertMapsActivity1 extends FragmentActivity{

    private GoogleMap googleMap;
    private LatLng garageLocation;
    Button btnSetGarageLocation;
    SearchView search;
    double lat = 20.423156, distance = 0, lng = -27.084917;
    Geocoder geocoder;
    List<Address> addresses;
    Global global;
    ArrayList<LatLng> MarkerPoints;
    private static final int PLACE_PICKER_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_maps);
        btnSetGarageLocation = (Button) findViewById(R.id.btn_set_garage_location);
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        global = new Global();
        // Getting GoogleMap object from the fragment
        MarkerPoints = new ArrayList<LatLng>();
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else { // Google Play Services are available
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = fm.getMap();
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            InitializeMapping(googleMap);
            // Getting reference to the SupportMapFragment of activity_main.xml
        }
        search = (SearchView) findViewById(R.id.search);
        search.setQueryHint("SearchView");
        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(InsertMapsActivity1.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

                /*              Intent intent = new Intent(InsertMapsActivity1.this, searchingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
*/            }
        });
search.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(InsertMapsActivity1.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
});

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), "\n" + query,                        Toast.LENGTH_SHORT).show();

           return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    Geocoder coder = new Geocoder(InsertMapsActivity1.this);
                    List<Address> address;
                    double x = 0, y = 0;
                    try {
                        address = coder.getFromLocationName(newText.toString(), 5);
                        if (address.toString().length() < 3) {
                            return Boolean.parseBoolean(null);
                        } else {
                            Address location = address.get(0);
                            x = location.getLatitude();
                            y = location.getLongitude();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    LatLng Position_mark = new LatLng(x, y);
                    Position_mark = new LatLng(x, y);

                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(Position_mark).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    garageLocation = Position_mark;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(Position_mark));
                    // Zoom in the Google Map
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    googleMap.getMaxZoomLevel();

 /*               googleMap.setOnMapClickListener(this);
                googleMap.setOnMapLongClickListener(this);
                googleMap.setOnMarkerDragListener(this);
*/

                }
                return false;
            }
        });

    }

    protected void InitializeMapping(final GoogleMap gogleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(false);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Enabling MyLocation Layer of Google Map
        gogleMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        btnSetGarageLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("location_lat", garageLocation.latitude);
                intent.putExtra("location_lng", garageLocation.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
//                onLocationChanged(location);
        }
        geocoder = new Geocoder(
                InsertMapsActivity1.this, Locale
                .getDefault());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
//                CameraToLocation(location);
                // Setting latitude and longitude in the TextView tv_location

                lat = location.getLatitude();
                lng = location.getLongitude();
                String area = null, CityName = null, country = null;
                try {
                    Log.v("log_tag", "latitude" + lat);
                    Log.v("log_tag", "longitude" + lng);
                    addresses = geocoder.getFromLocation(lat,
                            lng, 1);
                    Log.v("log_tag", "addresses+)_+++" + addresses);
//           CityName = addresses.get(0).getAddressLine(0);
                    if (addresses != null) {
                        area = addresses.get(0).getAddressLine(0);
                        CityName = addresses.get(0).getAddressLine(1);
                        country = addresses.get(0).getAddressLine(2);
                        Log.v("log_tag", "CityName" + CityName);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //      tv1.setText(addresses.toString());
                Toast.makeText(getApplicationContext(), "\n" + "\n" + area + "\n" + CityName + "\n" + country, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        gogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //googleMap.clear();
//                googleMap.addMarker(new MarkerOptions().position(latLng).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                garageLocation = latLng;
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                googleMap.getMaxZoomLevel();
                LatLng origin = new LatLng(lat, lng);
                LatLng dest = new LatLng(latLng.latitude, latLng.longitude);


                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    googleMap.clear();
                }

                MarkerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    options.title("getting me");
//                    http://stackoverflow.com/questions/35554796/rotate-marker-and-move-animation-on-map-like-uber-android
                    MapFragment mMapFragment;
//(in my case, MapFragment extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener)
                    Marker marker = googleMap.addMarker(options);
                    global.rotateMarker(marker, 250);
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    options.title("Destination");
                    origin = new LatLng(MarkerPoints.get(0).latitude, MarkerPoints.get(0).longitude);
                    dest = new LatLng(MarkerPoints.get(1).latitude, MarkerPoints.get(1).longitude);
                    googleMap.addMarker(options);
                    String url = getDirectionsUrl(origin, dest);
                    if (global.isNetworkAvailable(getApplicationContext())) {
                        DownloadTask downloadTask = new DownloadTask();
                        //           Toast.makeText(getApplicationContext(), String.valueOf(calculateDistance(lat, lng, latLng.latitude, latLng.longitude)), Toast.LENGTH_LONG).show();
                        downloadTask.execute(url);
                    } else {
                        global.CustomToast(getApplicationContext(), "Network Problem", "Cannot get Routing \n Check Internet Connection");
                    }
                }

                // Add new marker to the Google Map Android API V2
//                googleMap.addMarker(options);
                // Checks, whether start and end locations are captured
            }
        });
    }


    public void CameraToLocation(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.getMaxZoomLevel();


    }

    /*
FOR Searching //http://www.androidhive.info/2012/08/android-working-with-google-places-and-maps-tutorial/
VVIP //http://stackoverflow.com/questions/12460471/how-to-send-a-google-places-search-request-with-java
    */
    void textchangedListener(String s) {
        try {
            new GooglePlacesClient1().performSearch("establishment", 8.6668310, 50.1093060);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*

ROUTING
*/
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //      Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            lineOptions = new PolylineOptions();
            // Traversing through all the routes
            distance = 0;
            for (int i = 0; i < result.size(); i++) {
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                lineOptions = new PolylineOptions();
                points = new ArrayList<LatLng>();

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    distance += 0.01;
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route

            // Getting GoogleMap object from the fragment
            googleMap.addPolyline(lineOptions);
            Toast.makeText(getApplicationContext(), String.valueOf(distance), Toast.LENGTH_LONG).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                double x = 0, y = 0;


                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                Geocoder coder = new Geocoder(InsertMapsActivity1.this);
                List<Address> address;
                try {
                    address = coder.getFromLocationName(place.getName().toString(), 5);
                    if (address.toString().length() < 3) {
 //                       return (null);
                    } else {
                        Address location = address.get(0);
                        x = location.getLatitude();
                        y = location.getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                LatLng Position_mark = new LatLng(x, y);
                Position_mark = new LatLng(x, y);

                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(Position_mark).title("Custom location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                garageLocation = Position_mark;
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(Position_mark));
                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                googleMap.getMaxZoomLevel();
//https://developers.google.com/places/android-api/photos

                LatLng origin = new LatLng(lat, lng);
                LatLng dest = new LatLng(lat, lng);


                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    googleMap.clear();
                }

                MarkerPoints.add(Position_mark);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(Position_mark);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    options.title("getting me");
//                    http://stackoverflow.com/questions/35554796/rotate-marker-and-move-animation-on-map-like-uber-android
                    MapFragment mMapFragment;
//(in my case, MapFragment extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener)
                    Marker marker = googleMap.addMarker(options);
                    global.rotateMarker(marker, 250);
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    options.title("Destination");
                    origin = new LatLng(MarkerPoints.get(0).latitude, MarkerPoints.get(0).longitude);
                    dest = new LatLng(MarkerPoints.get(1).latitude, MarkerPoints.get(1).longitude);
                    googleMap.addMarker(options);
                    String url = getDirectionsUrl(origin, dest);
                    if (global.isNetworkAvailable(getApplicationContext())) {
                        DownloadTask downloadTask = new DownloadTask();
                        //           Toast.makeText(getApplicationContext(), String.valueOf(calculateDistance(lat, lng, latLng.latitude, latLng.longitude)), Toast.LENGTH_LONG).show();
                        downloadTask.execute(url);
                    } else {
                        global.CustomToast(getApplicationContext(), "Network Problem", "Cannot get Routing \n Check Internet Connection");
                    }
                }


            }
        }
    }

    }

//http://stackoverflow.com/questions/12460471/how-to-send-a-google-places-search-request-with-java

class GooglePlacesClient1 {
    private static final String GOOGLE_API_KEY = "AIzaSyAB2QlKFnyl30B8dhgGf-RjviprLKXtOk0";

    private final HttpClient client = new DefaultHttpClient();

    public void main(final String[] args) throws ParseException, IOException, URISyntaxException {
        new GooglePlacesClient1().performSearch("establishment", 8.6668310, 50.1093060);
    }

    public void performSearch(final String types, final double lon, final double lat) throws ParseException, IOException, URISyntaxException {
        final URIBuilder builder = new URIBuilder().setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/place/search/json");

        builder.addParameter("location", lat + "," + lon);
        builder.addParameter("radius", "5");
        builder.addParameter("types", types);
        builder.addParameter("sensor", "true");
        builder.addParameter("key", GooglePlacesClient1.GOOGLE_API_KEY);
        final HttpUriRequest request = new HttpGet(builder.build());

        final HttpResponse execute = this.client.execute(request);

        final String response = EntityUtils.toString(execute.getEntity());
//        Toast.makeText(getApplicationContext(), "\n" + s, Toast.LENGTH_LONG).show();

        System.out.println(response);
    }


}