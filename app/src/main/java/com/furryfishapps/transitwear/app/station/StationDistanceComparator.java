package com.furryfishapps.transitwear.app.station;

import java.util.Comparator;

public class StationDistanceComparator implements Comparator<Station> {
    @Override
    public int compare(Station station, Station station2) {
        return station.getDistance() < station2.getDistance() ? -1 : station.getDistance() == station2.getDistance() ? 0 : 1;
    }
}
