package com.furryfishapps.transitwear.app.time;

import android.util.Log;

import com.furryfishapps.transitwear.app.station.Station;

import java.util.Arrays;
import java.util.List;

public class TimeServiceImpl implements TimeService {
    private static final String TAG = "TimeServiceImpl";

    @Override
    public List<Time> getTimes(Station station) {
        Log.i(TAG, "Retrieving times for station: " + station);

        Time one = new Time(station, 4, "Niehl Sebastian Str.", "16");
        Time two = new Time(station, 6, "Bonn Bad God", "16");
        Time three = new Time(station, 10, "Holweide", "13");
        Time four = new Time(station, 10, "Sülzgürtel", "13");

        List<Time> times = Arrays.asList(one, two, three, four);
        if (times.isEmpty()) {
            times.add(Time.createDummyTime(station));
        }

        return times;
    }
}
