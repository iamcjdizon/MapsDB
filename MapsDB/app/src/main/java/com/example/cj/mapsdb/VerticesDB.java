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

    public VerticesDB()
    {

    }

    public VerticesDB(String vName, double vertexLat, double vertexLong)
    {
        this.vName = vName; // contains the street address. ex. 31 G. Manalo Street
        this.vertexLat = vertexLat;
        this.vertexLong = vertexLong;
        forAddress();
    }

    public void forAddress() //method inorder to create unique id for each vertex.
    {
//        String[] address = vName.split(" "); //spllits the address into an array with  space as a splitter
//
//        for (int x=0; x<address.length; x++)
//        {
//            if (!address[x].matches(".*\\d+.*")) // checks the array if it contains letters only
//                vID = vID + address[x].substring(0, 1); //adds the first letter of the string array into the string
//            else
//                vID = vID + address[x]; //adds all the numbers into the string.
//        }
        vID = vertexLat.toString() + "x" + vertexLong.toString();
        char[] forVID = vID.toCharArray();
        for (int x=0; x<forVID.length; x++)
            if (forVID[x] == '.')
                forVID[x] = ',';
        vID = String.valueOf(forVID);
        //based on the example above. the resulting vID will be 31GMS
    }

    public String getvName(){return vName;}
    public String getvID() {return vID;}
    public Double getVertexLong() {return vertexLong;}
    public Double getVertexLat() {return  vertexLat;}
    public void setvName(String vName){this.vName = vName;}
    public void setvID(String vID) {this.vID = vID;}
    public void setVertexLong (Double vertexLong) {this.vertexLong = vertexLong;}
    public void setVertexLat (Double vertexLat) {this.vertexLat=vertexLat;}

}
