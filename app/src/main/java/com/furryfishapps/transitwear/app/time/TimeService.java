package com.furryfishapps.transitwear.app.time;

import com.furryfishapps.transitwear.app.station.Station;

import java.util.List;

public interface TimeService {

    List<Time> getTimes(Station station);

}
