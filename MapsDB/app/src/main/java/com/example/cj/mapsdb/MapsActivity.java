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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private Marker mark1=null, mark2=null, mark;
    private Button btnDistance, btnSave, btnClear;
    private Context context = this;
    float[] distance = new float[3];
    String vName, startVertex, endVertex;
    VerticesDB vertex;


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
        btnSave = (Button) findViewById(R.id.btnSave);
        btnClear = (Button) findViewById(R.id.btnClear);
        getVertexDB();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Luneta and move the camera
        LatLng manila = new LatLng(14.583118, 120.979417);
        //mMap.addMarker(new MarkerOptions().position(manila).title("Marker in Luneta"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manila, 17));
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        mMap.addMarker(new MarkerOptions().position(point).draggable(true)); // Adds marker when upon long click.


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){

            @Override
            public boolean onMarkerClick(Marker marker) {
                mark = marker;
                btnSave.setAlpha(1f);
                vName = getAddress(marker);
                vertex = new VerticesDB(vName, marker.getPosition().latitude, marker.getPosition().longitude); //initalized the class with vName, lat, long as constructors.
                marker.setTitle(vName);
                if (mark1 == null)
                {
                    mark1 = marker;
                    mark1.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    startVertex = vertex.vID;

                }

                else if (marker != mark1)
                {
                    mark2 = marker;
                    mark2.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    endVertex = vertex.vID;

                    btnDistance.setAlpha(1f);
                    btnClear.setAlpha(1f);
                }

                return false;

            }
        });


        btnDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location.distanceBetween(mark1.getPosition().latitude, mark1.getPosition().longitude,
                        mark2.getPosition().latitude, mark2.getPosition().longitude, distance);
                PathsDB path = new PathsDB(startVertex, endVertex, Math.abs(distance[0]));
                mDatabase.child("path").push().setValue(path);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(MapsActivity.this, "SAVED", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mMap.addPolyline(new PolylineOptions().add(
                        new LatLng(mark1.getPosition().latitude, mark1.getPosition().longitude),
                        new LatLng(mark2.getPosition().latitude, mark2.getPosition().longitude)
                ));
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mark1.setIcon(BitmapDescriptorFactory.defaultMarker());
                mark2.setIcon(BitmapDescriptorFactory.defaultMarker());
                mark1 = null;
                mark2 = null;
                btnDistance.setAlpha(0f);
                btnClear.setAlpha(0f);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child("vertex").child(vertex.vID).setValue(vertex); //posts or uploads the public variables in the class.
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
                mark.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            }
        });



    }

    public String getAddress(Marker marker)
    {
        Geocoder geocoder = new Geocoder(context); //initialized geocoder
        List<Address> addressList = null; //initialized addreslist
        try {
            addressList = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1); // returns address based on latitude and longitude
        }
        catch (IOException e) //Di ko alam to. hahaha. Lols
        {
            e.printStackTrace();
        }

        return addressList.get(0).getAddressLine(0); // Stored the street addtress into the vName variable.
    }

    public void getVertexDB()
    {
        //try{
            mDatabase.child("vertex").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        VerticesDB vertices = new VerticesDB();
                        vertices.setVertexLat(ds.child("").getValue(VerticesDB.class).getVertexLat());
                        vertices.setVertexLong(ds.child("").getValue(VerticesDB.class).getVertexLong());
                        vertices.setvName(ds.child("").getValue(VerticesDB.class).getvName());
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(vertices.vertexLat, vertices.vertexLong))
                                .title(vertices.vName)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        //}
        //catch (NullPointerException e)
        //{

        //}

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
