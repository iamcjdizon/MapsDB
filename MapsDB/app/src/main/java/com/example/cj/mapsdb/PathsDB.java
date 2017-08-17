package com.example.cj.mapsdb;

/**
 * Created by CJ on 8/16/2017.
 */

public class PathsDB {

    private String startingNode;
    private String finalNode;
    private Double distance;

    public PathsDB()
    {

    }

    public PathsDB(String startingNode, String finalNode, double distance)
    {
        this.startingNode = startingNode;
        this.finalNode = finalNode;
        this.distance = distance;
    }

    public String getStartingNode() {return startingNode;}
    public String getFinalNode() {return finalNode;}
    public Double getDistance() {return distance;}
    public void setDistance(Double distance) {this.distance = distance;}
    public void setFinalNode(String finalNode) {this.finalNode = finalNode;}
    public void setStartingNode(String startingNode) {this.startingNode = startingNode;}

    public double vertexLat(String startingNode)
    {
        String[] node = startingNode.split("x");
        char[] lat = node[0].toCharArray();
        for (int x= 0; x<lat.length; x++)
            if (lat[x] == ',')
                lat[x] = '.';

        return Double.parseDouble(String.valueOf(lat));

    }

    public double vertexLong(String nodes)
    {
        String[] node = nodes.split("x");
        char[] longi = node[1].toCharArray();
        for (int x= 0; x<longi.length; x++)
            if (longi[x] == ',')
                longi[x] = '.';

        return Double.parseDouble(String.valueOf(longi));
    }

}
