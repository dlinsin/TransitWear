package com.furryfishapps.transitwear.app.time;

import android.content.Context;

import java.text.DateFormat;

public class TransitTimeFormatter {
    static TransitTimeFormatter instance;
    final Context context;

    TransitTimeFormatter(Context context) {
        this.context = context;
    }

    public static void initInstance(Context context) {
        if (instance == null) {
            instance = new TransitTimeFormatter(context.getApplicationContext());
        }
    }

    public static TransitTimeFormatter getInstance() {
        if (instance == null) {
            throw new RuntimeException("not inisitalized");
        }
        return instance;
    }

    public DateFormat getTimeFormatter() {
        return android.text.format.DateFormat.getTimeFormat(context);
    }
}
