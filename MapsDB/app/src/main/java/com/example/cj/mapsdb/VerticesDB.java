package com.example.cj.mapsdb;

import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by CJ on 8/12/2017.
 */

public class VerticesDB {
    public String vName;
    public String vID = "";
    public Double vertexLong;
    public Double vertexLat;

    public VerticesDB(String vName, double vertexLat, double vertexLong)
    {
        this.vName = vName;
        this.vertexLat = vertexLat;
        this.vertexLong = vertexLong;
    }

    public void forAddress()
    {

        String[] address = vName.split(" ");

        for (int x=0; x<address.length; x++)
        {
            if (!address[x].matches(".*\\d+.*"))
                vID = vID + address[x].substring(0, 1);
            else
                vID = vID + address[x];
        }
    }

}
