package com.example.cj.mapsdb;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Marker mark1, mark2;
    private Button btnDistance,btnAdd, btnSave;
    private Context context = this;
    private LatLng pointer;
    float[] distance = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference(); //get the instance of the firebase database
        btnDistance = (Button) findViewById(R.id.btnDistance);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Luneta and move the camera
        LatLng manila = new LatLng(14.583118, 120.979417);
        mMap.addMarker(new MarkerOptions().position(manila).title("Marker in Luneta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manila, 17));
        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(final LatLng point) {
        mMap.addMarker(new MarkerOptions()
                .position(point)); // Adds marker when upon long click.
        pointer = point;


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mark1 == null)
                {
                    mark1 = marker;
                    mark1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                }

                else if (marker != mark1)
                {
                    mark2 = marker;
                    mark2.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    btnDistance.setAlpha(1f);
                }
                return false;
            }
        });

        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location.distanceBetween(mark1.getPosition().latitude, mark1.getPosition().longitude,
                        mark2.getPosition().latitude, mark2.getPosition().longitude, distance);
                Toast.makeText( MapsActivity.this, "Distance is "+ distance[2] + " m", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoder geocoder = new Geocoder(context); //initialized geocoder
                List<Address> addressList = null; //initialized addreslist
                try {
                    addressList = geocoder.getFromLocation(point.latitude, point.longitude, 1); // returns address based on latitude and longitude
                }
                catch (IOException e) //Di ko alam to. hahaha. Lols
                {
                    e.printStackTrace();
                }

                String vName = addressList.get(0).getAddressLine(0); // Stored the street addtress into the vName variable.

                VerticesDB verticesDB = new VerticesDB(vName, point.latitude, point.longitude); //initalized the class with vName, lat, long as constructors.
                verticesDB.forAddress(); //calls the method.
                mDatabase.child("vertex").child(verticesDB.vID).setValue(verticesDB); //posts or uploads the public variables in the class.
                //the vertex represents the table name.
                //the vID represents the Primary Key
                //verticesDB represents the public variables and acts as table entry.

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(MapsActivity.this,"SAVED", Toast.LENGTH_SHORT).show(); //notification to know if it is posted.
                        //pero feel ko may bug pa.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }
}
