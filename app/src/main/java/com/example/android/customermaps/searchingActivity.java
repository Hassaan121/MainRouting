package com.example.android.customermaps;

import android.app.SearchManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// Search http://wptrafficanalyzer.in/blog/android-searchview-widget-with-google-places-api-using-actionbarsherlock-library/
// search2  http://www.viralandroid.com/2016/04/google-maps-android-api-adding-search-bar-part-3.html
public class searchingActivity extends AppCompatActivity {
    SearchView search;
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;

    // Search EditText


    // ArrayList for Listview
    ArrayList<HashMap<String, String>> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        search = (SearchView) findViewById(R.id.searchv);
        search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        final String products[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
                "iPhone 4S", "Samsung Galaxy Note 800",
                "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};

        lv = (ListView) findViewById(R.id.l1);

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.product_name, products);
        lv.setAdapter(adapter);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// make Toast when click
                Toast.makeText(getApplicationContext(),products[position]+ "\n", Toast.LENGTH_LONG).show();
            }
        });

        inititatesearching();
    }

  private void inititatesearching() {
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), "\n" + query,                        Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                Geocoder coder = new Geocoder(searchingActivity.this);
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
                return false;
            }
        });
    }

}
