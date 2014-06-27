package com.furryfishapps.transitwear.app.station;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Station implements Parcelable {
    private Integer code;
    private List<Double> location;
    private String name;
    private List<Integer> lines;
    private List<String> otherLines;
    private StationType type;
    private boolean favorite;
    private float distance;
    private int maxWidthOfTimes;


    public Station(Integer code, List<Double> location, String name, List<Integer> lines, List<String> otherLines, StationType type) {
        this.code = code;
        this.location = location;
        this.name = name;
        this.lines = lines;
        this.type = type;
        this.otherLines = otherLines;
    }

    public Integer getCode() {
        return code;
    }

    public List<Double> getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getLines() {
        return lines;
    }

    public StationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object) this).getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return !(code != null ? !code.equals(station.code) : station.code != null) && type == station.type;

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getFormattedDistance() {
        if (distance <= 0) {
            return "";
        }

        NumberFormat formatter = NumberFormat.getInstance();
        if (distance >= 10000) {
            formatter.setMaximumFractionDigits(0);
            return formatter.format(distance / 1000) + " km";
        } else if (distance >= 700) {
            formatter.setMaximumFractionDigits(1);
            return formatter.format(distance / 1000) + " km";
        } else {
            formatter.setMaximumFractionDigits(0);
            return formatter.format(distance) + " m";
        }
    }

    public String getFavoriteCode() {
        return code.toString() + "," + type.toString();
    }

    public List<String> getOtherLines() {
        if (otherLines == null) {
            return new ArrayList<String>();
        }
        return otherLines;
    }

    public List<String> getDistinctOtherLines() {
        Set<String> tmp = new HashSet<String>();
        Set<String> tmpSBahn = new HashSet<String>();
        for (String otherLine : getOtherLines()) {
            if (otherLine.startsWith("RE")) {
                tmp.add("RE");
            } else if (otherLine.startsWith("RB")) {
                tmp.add("RB");
            } else if (otherLine.startsWith("MRB")) {
                tmp.add("MRB");
            } else {
                tmpSBahn.add(otherLine); // S6-S13
            }
        }
        ArrayList<String> distinctOtherLinesWithoutSBahn = new ArrayList<String>(tmp);
        Collections.sort(distinctOtherLinesWithoutSBahn, String.CASE_INSENSITIVE_ORDER);

        Collections.sort(distinctOtherLinesWithoutSBahn, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        if (lhs.length() == rhs.length()) {
                            return 0;
                        } else if (lhs.length() < rhs.length()) {
                            return -1;
                        }
                        return 1;
                    }
                }
        );

        ArrayList<String> distinctSBahn = new ArrayList<String>(tmpSBahn);
        Collections.sort(distinctSBahn, String.CASE_INSENSITIVE_ORDER);

        Collections.sort(distinctSBahn, new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        if (lhs.length() == rhs.length()) {
                            return 0;
                        } else if (lhs.length() < rhs.length()) {
                            return -1;
                        }
                        return 1;
                    }
                }
        );

        ArrayList<String> result = new ArrayList<String>(distinctSBahn);
        result.addAll(distinctOtherLinesWithoutSBahn);

        return result;
    }

    public int getMaxWidthOfTimes() {
        return maxWidthOfTimes;
    }

    public void setMaxWidthOfTimes(int maxWidthOfTimes) {
        this.maxWidthOfTimes = maxWidthOfTimes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(code);
        parcel.writeList(location);
        parcel.writeString(name);
        parcel.writeList(lines);
        parcel.writeSerializable(type);
        parcel.writeByte((byte) (favorite ? 1 : 0));
        parcel.writeFloat(distance);
        parcel.writeInt(maxWidthOfTimes);
        parcel.writeList(otherLines);
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    private Station(Parcel parcel) {
        code = parcel.readInt();
        List<Double> tmpLocation = new ArrayList<Double>();
        parcel.readList(tmpLocation, null);
        location = tmpLocation;
        name = parcel.readString();
        List<Integer> tmpLines = new ArrayList<Integer>();
        parcel.readList(tmpLines, null);
        lines = tmpLines;
        type = (StationType) parcel.readSerializable();
        favorite = parcel.readByte() != 0;
        distance = parcel.readFloat();
        maxWidthOfTimes = parcel.readInt();
        List<String> tmpOtherLines = new ArrayList<String>();
        parcel.readList(tmpOtherLines, null);
        otherLines = tmpOtherLines;
    }

    @Override
    public String toString() {
        return "Station{" +
                "code=" + code +
                ", location=" + location +
                ", name='" + name + '\'' +
                ", lines=" + lines +
                ", otherLines=" + otherLines +
                ", type=" + type +
                ", favorite=" + favorite +
                ", distance=" + distance +
                ", maxWidthOfTimes=" + maxWidthOfTimes +
                '}';
    }
}
