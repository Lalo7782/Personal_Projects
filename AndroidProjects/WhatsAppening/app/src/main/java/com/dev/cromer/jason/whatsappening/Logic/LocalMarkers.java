package com.dev.cromer.jason.whatsappening.Logic;


import android.location.Location;

import com.dev.cromer.jason.whatsappening.Networking.HttpGetRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocalMarkers {

    Location markerLatLngLocation;
    GoogleMap mMap;
    List<String> markerItemsList = Collections.emptyList();

    public LocalMarkers(Location markerLatLngLocation, GoogleMap mMap) {
        this.markerLatLngLocation = markerLatLngLocation;
        this.mMap = mMap;
    }


    public void getLocalMarkers() {

        //url to endpoint containing user's local latitude and longitude
        final String url = "http://10.0.2.2:5000/api/get_markers/"+String.valueOf(this.markerLatLngLocation.getLatitude())+
                String.valueOf("/"+this.markerLatLngLocation.getLongitude());


        try{
            HttpGetRequest getRequest = new HttpGetRequest();

            //Returned data from API as String-list, i.e. [[item1, item2, item3,]]
            String receivedData = getRequest.execute(url).get();
            receivedData = receivedData.replace("[", "").replace("]", "").replace("\"", "");            //replace brackets and quotations
            this.markerItemsList = Arrays.asList(receivedData.split("\\s*,\\s*"));                     //filter out whitespace and turn into List
        }
        catch (ExecutionException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
    }


    public void mapLocalMarkers() {
        /*
            List includes a pattern of: [latitude, longitude, Title, latitude, long...]
            so we must assign values based on chunks of three, then iterate by 3.
         */

        if(markerItemsList.size() > 2) {                                            //If size is < 3, not a valid list
            for(int i = 0; i < markerItemsList.size(); i+=3) {
                final String thisLatitude = markerItemsList.get(i);
                final String thisLongitude = markerItemsList.get(i + 1);
                final String thisTitle = markerItemsList.get(i + 2);

                //Add new marker with the coordinates and title of each marker in the list
                this.mMap.addMarker(new MarkerOptions().position(new LatLng(Float.valueOf(thisLatitude), Float.valueOf(thisLongitude)))
                        .title(thisTitle).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            }
        }

        else{
            //If no markers in radius, clear markers on map
            mMap.clear();
        }
    }
}