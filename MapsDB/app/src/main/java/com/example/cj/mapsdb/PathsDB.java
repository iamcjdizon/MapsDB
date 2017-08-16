package com.example.cj.mapsdb;

/**
 * Created by CJ on 8/16/2017.
 */

public class PathsDB {

    public String startingNode;
    public String finalNode;
    public Double distance;

    public PathsDB(String startingNode, String finalNode, double distance)
    {
        this.startingNode = startingNode;
        this.finalNode = finalNode;
        this.distance = distance;
    }
}
