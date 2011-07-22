package com.graviola.nojamforme;

import java.util.List;

public class Route { 
    private final int id;
    private final int number;
    private final String name;
    private final String description;
    private final String detail;
    private final String polyline;
    
    Route(int id, int number, String name, String description, String detail, String polyline) {
    	this.id = id;
    	this.number = number;
    	this.name = name;
    	this.description = description;
    	this.detail = detail;
    	this.polyline = polyline;
    }

    public String toJson(){
        return "{ 'route' : { 'id' : " + id + ", 'number':" + number + 
            ", 'name': '" + name + ", 'description': '" + description +
            ", 'detail': '" + detail + ", 'polyline': '" + polyline + "'}}";
    }

    public static String toJson(List<Route> routes){
        StringBuilder json = new StringBuilder("{'routes' : [");
        for (Route r : routes){
            json.append(r.toJson());
            json.append(',');
        }
        json.deleteCharAt(json.length() - 1);
        json.append("]}");
        return json.toString();
    }
}