package com.furryfishapps.transitwear.app.station;

import android.location.Location;

import java.util.List;
import java.util.Set;

public interface StationService {
    public List<Station> getStationsNearby(Location location);
}
