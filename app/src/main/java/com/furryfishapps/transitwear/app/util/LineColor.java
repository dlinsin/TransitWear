package com.furryfishapps.transitwear.app.util;

public class LineColor {
    public static String getColor(String line) {
        try {
            int lineNumber = Integer.valueOf(line);
            if (lineNumber == 1) {
                return "#d62631";
            } else if (lineNumber == 3) {
                return "#e794b4";
            } else if (lineNumber == 4) {
                return "#e16f9f";
            } else if (lineNumber == 5) {
                return "#948fb7";
            } else if (lineNumber == 7) {
                return "#e7855d";
            } else if (lineNumber == 9) {
                return "#e7887e";
            } else if (lineNumber == 12) {
                return "#86bd4a";
            } else if (lineNumber == 13) {
                return "#967b68";
            } else if (lineNumber == 15) {
                return "#51ad4c";
            } else if (lineNumber == 16) {
                return "#00ab9e";
            } else if (lineNumber == 18) {
                return "#0096cb";
            } else if (lineNumber == 106) {
                return "#008bc1";
            } else if (lineNumber == 120) {
                return "#00b0dc";
            } else if (lineNumber == 121) {
                return "#92c249";
            } else if (lineNumber == 122) {
                return "#0068a3";
            } else if (lineNumber == 125) {
                return "#ec9e3c";
            } else if (lineNumber == 126) {
                return "#e893ad";
            } else if (lineNumber == 127) {
                return "#00a4d8";
            } else if (lineNumber == 130) {
                return "#65c5e3";
            } else if (lineNumber == 131) {
                return "#92c249";
            } else if (lineNumber == 132) {
                return "#ec9e3c";
            } else if (lineNumber == 133) {
                return "#d3a0bd";
            } else if (lineNumber == 135) {
                return "#e16f9f";
            } else if (lineNumber == 136) {
                return "#aa7e68";
            } else if (lineNumber == 138) {
                return "#da4b8c";
            } else if (lineNumber == 139) {
                return "#a9704f";
            } else if (lineNumber == 140) {
                return "#f7c53a";
            } else if (lineNumber == 141) {
                return "#3eb3a7";
            } else if (lineNumber == 142) {
                return "#9487b1";
            } else if (lineNumber == 143) {
                return "#9fc758";
            } else if (lineNumber == 144) {
                return "#ec9e3c";
            } else if (lineNumber == 145) {
                return "#1bb8de";
            } else if (lineNumber == 146) {
                return "#dd553c";
            } else if (lineNumber == 147) {
                return "#e794b4";
            } else if (lineNumber == 149) {
                return "#0068a3";
            } else if (lineNumber == 150) {
                return "#ec9e3c";
            } else if (lineNumber == 151) {
                return "#d1a83d";
            } else if (lineNumber == 152) {
                return "#f7c53a";
            } else if (lineNumber == 153) {
                return "#9487b1";
            } else if (lineNumber == 154) {
                return "#e1694a";
            } else if (lineNumber == 155) {
                return "#008bc1";
            } else if (lineNumber == 156) {
                return "#965d39";
            } else if (lineNumber == 157) {
                return "#00b0dc";
            } else if (lineNumber == 158) {
                return "#6fb54b";
            } else if (lineNumber == 159) {
                return "#da4b8c";
            } else if (lineNumber == 160) {
                return "#6fb54b";
            } else if (lineNumber == 161) {
                return "#00b0dc";
            } else if (lineNumber == 162) {
                return "#dd5893";
            } else if (lineNumber == 163) {
                return "#00a4d8";
            } else if (lineNumber == 164) {
                return "#81659b";
            } else if (lineNumber == 165) {
                return "#777776";
            } else if (lineNumber == 166) {
                return "#777776";
            } else if (lineNumber == 167) {
                return "#777776";
            } else if (lineNumber <= 180 && lineNumber < 190) {
                return "#8a8b8a";
            } else if (lineNumber == 190) {
                return "#0068a3";
            }
        } catch (NumberFormatException e) {
            if (line.startsWith("S")) { // s6, s11, s12, s13
                return "#eb8b2e";
            } else if (line.startsWith("RB")) {
                return "#00977f";
            } else if (line.startsWith("RE")) {
                return "#00977f";
            } else if (line.startsWith("MRB")) {
                return "#00977f";
            }
        }
        return "lightgrey";
    }
}
