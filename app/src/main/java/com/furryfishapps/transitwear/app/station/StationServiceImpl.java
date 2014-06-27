package com.furryfishapps.transitwear.app.station;


import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StationServiceImpl implements StationService {
    private static final String TAG = "StationServiceImpl";
    private boolean displayTrains = true;
    private boolean displayBusses = false; // TODO enable settings
    private List<Station> stations;

    private static final int BEST_RADIUS = 350;
    private static final int MEDIUM_RADIUS = 550;
    private static final int FALL_BACK_RADIUS = 1000;
    private static final int MAX_STATIONS = 3;
    private static final int MIN_STATIONS = 1;

    public StationServiceImpl() {
        this.stations = new ArrayList<Station>();
    }

    public List<Station> getStationsNearby(Location location) {
        Log.i(TAG, "Loading Stations nearby: " + location);
        List<Station> stationsInRadius = findStationsInRadiusNearLocation(BEST_RADIUS, location);
        if (stationsInRadius.size() < MIN_STATIONS) {
            stationsInRadius = findStationsInRadiusNearLocation(MEDIUM_RADIUS, location);
            if (stationsInRadius.size() < MIN_STATIONS) {
                stationsInRadius = findStationsInRadiusNearLocation(FALL_BACK_RADIUS, location);
            }
        }

        Collections.sort(stationsInRadius, new StationDistanceComparator());

        Log.d(TAG, "Found following stations neabry: " + stationsInRadius);

        return stationsInRadius;
    }

    List<Station> findStationsInRadiusNearLocation(int radius, Location location) {
        Log.d(TAG, "Finding stations with radius " + radius + " and location " + location);
        List<Station> stationsInRadius = new ArrayList<Station>();
        List<Station> stationsFilteredByType = filterStations(getStations());

        for (Station station : stationsFilteredByType) {
            float distance = determineDistance(location, station);
            station.setDistance(distance);
            if (distance <= radius) {
                stationsInRadius.add(station);
            }
            if (stationsInRadius.size() >= MAX_STATIONS) {
                break;
            }
        }

        return stationsInRadius;
    }

    private float determineDistance(Location location, Station station) {
        float[] result = new float[4];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(), station.getLocation().get(0), station.getLocation().get(1), result);
        return result[0];
    }

    List<Station> getStations() {
        if (stations.isEmpty()) {
            Station amsterdamerStr = new Station(317, Arrays.asList(50.9708333, 6.9690527), "Amsterdamer Str./Gürtel", Arrays.asList(13, 16), null, StationType.Train);
            stations = Arrays.asList(amsterdamerStr); // usually this would load from a datasource
        }
        return stations;
    }

    private List<Station> filterStations(List<Station> stations) {
        ArrayList<Station> newList = new ArrayList<Station>();

        for (Station station : stations) {
            if (station.getType() == StationType.Train && displayTrains) {
                newList.add(station);
            } else if (station.getType() == StationType.Bus && displayBusses) {
                newList.add(station);
            }
        }

        return newList;
    }
}
