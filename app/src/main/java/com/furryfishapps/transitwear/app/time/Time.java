package com.furryfishapps.transitwear.app.time;

import android.os.Parcel;
import android.os.Parcelable;


import com.furryfishapps.transitwear.app.station.Station;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Time implements Parcelable {
    private Station station;
    private Integer minutes;
    private String destination;
    private String line;
    String departureTime;

    public Time(Station station, Integer minutes, String destination, String line) {
        this.station = station;
        this.minutes = minutes;
        this.destination = destination;
        this.line = line;
        initDepartureTime(minutes);
    }

    void initDepartureTime(Integer minutes) {
        long now = System.currentTimeMillis();
        long then = now + (minutes * 60 * 1000);
        DateFormat timeInstance;
        try {
            timeInstance = TransitTimeFormatter.getInstance().getTimeFormatter();
        } catch (Exception e) {
            timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        }
        this.departureTime = timeInstance.format(new Date(then));
    }

    public static Time createDummyTime(Station station) {
        Time time = new Time(station, -99, "", "99");
        time.departureTime = "";
        return time;
    }

    public Station getStation() {
        return station;
    }

    public String getMinutes() {
        return String.valueOf(minutes);
    }

    public String getDestination() {
        return destination;
    }

    public String getLine() {
        return line;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        Time time = (Time) o;

        return destination.equals(time.destination) && line.equals(time.line) && minutes.equals(time.minutes) && station.equals(time.station);

    }

    @Override
    public int hashCode() {
        int result = station.hashCode();
        result = 31 * result + minutes.hashCode();
        result = 31 * result + destination.hashCode();
        result = 31 * result + line.hashCode();
        return result;
    }

    public boolean isDummy() {
        return this.equals(Time.createDummyTime(station));
    }

    public boolean isImmediateDeparture() {
        return minutes < 10;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(station, 0);
        parcel.writeInt(minutes);
        parcel.writeString(destination);
        parcel.writeString(line);
        parcel.writeString(departureTime);
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

    private Time(Parcel parcel) {
        station = parcel.readParcelable(((Object) this).getClass().getClassLoader());
        minutes = parcel.readInt();
        destination = parcel.readString();
        line = parcel.readString();
        departureTime = parcel.readString();
    }

    @Override
    public String toString() {
        return "Time{" +
                "station=" + station +
                ", minutes=" + minutes +
                ", destination='" + destination + '\'' +
                ", line='" + line + '\'' +
                ", departureTime='" + departureTime + '\'' +
                '}';
    }
}
