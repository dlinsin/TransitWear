package com.furryfishapps.transitwear.app.location;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.preview.support.wearable.notifications.WearableNotifications;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.furryfishapps.transitwear.app.MainActivity;
import com.furryfishapps.transitwear.app.R;
import com.furryfishapps.transitwear.app.station.Station;
import com.furryfishapps.transitwear.app.station.StationService;
import com.furryfishapps.transitwear.app.station.StationServiceImpl;
import com.furryfishapps.transitwear.app.station.StationType;
import com.furryfishapps.transitwear.app.time.Time;
import com.furryfishapps.transitwear.app.time.TimeService;
import com.furryfishapps.transitwear.app.time.TimeServiceImpl;
import com.furryfishapps.transitwear.app.util.LineColor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationIntentService extends IntentService {
    static final String TAG = "LocationIntentService";
    static final String WEARABLE_STACK_KEY = "WEARABLE_STACK_KEY";
    private StationService stationService;
    private TimeService timeService;

    public LocationIntentService() {
        this(null);
    }

    public LocationIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Location Intent Service called");
        handleLocationUpdate(intent);
    }

    void handleLocationUpdate(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.w(TAG, "No extras in intent");
            return;
        }

//        Location location = (Location) extras.get(LocationClient.KEY_LOCATION_CHANGED); // TODO faked for DUS
        Location location = new Location("flp");
        location.setLatitude(50.9662686);
        location.setLongitude(7.0067891);
        location.setAccuracy(3.0f);

        if (location != null) { // TODO check if first location is okay
            Log.i(TAG, "Location: " + location);

            if (stationService == null) {
                stationService = new StationServiceImpl();
            }

            List<Station> stationsNearby = stationService.getStationsNearby(location);

            if (timeService == null) {
                timeService = new TimeServiceImpl();
            }

            NotificationManagerCompat.from(this).cancelAll();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            for (Station station : stationsNearby) {
                List<Time> times;
                try {
                    times = timeService.getTimes(station);
                    if (times.isEmpty()) {
                        Log.w(TAG, "No times for station " + station);
                        continue;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                    continue;
                }
                Notification notification = createWearableNotification(station, times);
                Log.i(TAG, "Posting wearable notification: " + notification);
                notificationManager.notify(station.getCode(), notification); // TODO use tag to remove old notifications

                notification = createNotification(station, times);
                Log.i(TAG, "Posting notification: " + notification);
//                notificationManager.notify(station.getCode(), notification); // TODO use tag to remove old notifications
            }
        } else {
            Log.e(TAG, "Location null!");
        }
    }

    Notification createWearableNotification(Station station, List<Time> times) {
        List<Notification> timePages = new ArrayList<Notification>();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(station.getName())
                .setContentText(times.get(0).getLine() + " to " + times.get(0).getDestination() + " in " + times.get(0).getMinutes() + " mins")
                .setSmallIcon(R.drawable.ic_launcher);

        PendingIntent resultPendingIntent = createContentIntent();
        builder.setContentIntent(resultPendingIntent);

        Intent cancelIntent = createCancelIntent();
        PendingIntent cancelPendingIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0);
        builder.addAction(R.drawable.ic_action_cancel, getString(R.string.cancel), cancelPendingIntent);

        Intent resumeIntent = createResumeIntent();
        PendingIntent resumePendingIntent = PendingIntent.getActivity(this, 0, resumeIntent, 0);
        builder.addAction(R.drawable.ic_action_refresh, getString(R.string.resume), resumePendingIntent);

        times.remove(0);

        for (Time time : times) {
            if (timePages.size() >= 3) {
                break;
            }
            Notification extraPageNotification = new NotificationCompat.Builder(this)
                    .setContentTitle(station.getName())
                    .setContentText(time.getLine() + " to " + time.getDestination() + " in " + time.getMinutes() + " mins")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .build();
            timePages.add(extraPageNotification);
        }


        return new WearableNotifications.Builder(builder)
                .addPages(timePages)
                .build();
    }

    Notification createNotification(Station station, List<Time> times) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        PendingIntent resultPendingIntent = createContentIntent();
        mBuilder.setContentIntent(resultPendingIntent);

        Intent cancelIntent = createCancelIntent();
        PendingIntent cancelPendingIntent = PendingIntent.getActivity(this, 0, cancelIntent, 0);

        Intent resumeIntent = createResumeIntent();
        PendingIntent resumePendingIntent = PendingIntent.getActivity(this, 0, resumeIntent, 0);

        int notificationLayout = R.layout.notification_four_lines;
        if (times.size() == 3) {
            notificationLayout = R.layout.notification_three_lines;
        } else if (times.size() == 2) {
            notificationLayout = R.layout.notification_two_lines;
        } else if (times.size() == 1) {
            notificationLayout = R.layout.notification_one_line;
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), notificationLayout);

        int widthOfLine = getWidth(station);
        int lineTextID = R.id.line_text2;
        int lineTextID1 = R.id.line_text2_1;
        int lineTextID2 = R.id.line_text2_2;
        int lineTextID3 = R.id.line_text2_3;
        if (widthOfLine == 2) {
            remoteViews.setViewVisibility(R.id.line_text2, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_1, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text3_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_1, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_2, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text3_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_2, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_3, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text3_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_3, View.GONE);
        } else if (widthOfLine == 3) {

            lineTextID = R.id.line_text3;
            lineTextID1 = R.id.line_text3_1;
            lineTextID2 = R.id.line_text3_2;
            lineTextID3 = R.id.line_text3_3;

            remoteViews.setViewVisibility(R.id.line_text2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_1, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_1, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_2, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_2, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_3, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_3, View.GONE);

        } else if (widthOfLine == 4) {
            lineTextID = R.id.line_text4;
            lineTextID1 = R.id.line_text4_1;
            lineTextID2 = R.id.line_text4_2;
            lineTextID3 = R.id.line_text4_3;

            remoteViews.setViewVisibility(R.id.line_text2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4plus, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_1, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4plus_1, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_2, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4plus_2, View.GONE);

            remoteViews.setViewVisibility(R.id.line_text2_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_3, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.line_text4plus_3, View.GONE);
        } else if (widthOfLine > 4) {
            lineTextID = R.id.line_text4plus;
            lineTextID1 = R.id.line_text4plus_1;
            lineTextID2 = R.id.line_text4plus_2;
            lineTextID3 = R.id.line_text4plus_3;

            remoteViews.setViewVisibility(R.id.line_text2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus, View.VISIBLE);

            remoteViews.setViewVisibility(R.id.line_text2_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_1, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_1, View.VISIBLE);

            remoteViews.setViewVisibility(R.id.line_text2_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_2, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_2, View.VISIBLE);

            remoteViews.setViewVisibility(R.id.line_text2_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text3_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4_3, View.GONE);
            remoteViews.setViewVisibility(R.id.line_text4plus_3, View.VISIBLE);
        }

        remoteViews.setImageViewResource(R.id.notification_image, R.drawable.ic_launcher);

        remoteViews.setTextViewText(R.id.notification_title, station.getName());

        DateFormat timeInstance;
        try {
            timeInstance = android.text.format.DateFormat.getTimeFormat(this);
        } catch (Exception e) {
            timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        }
        String departureTime = timeInstance.format(new Date(System.currentTimeMillis()));
        remoteViews.setTextViewText(R.id.notification_time_text, departureTime);

        Bitmap bitmap = createBitmapWithColor(Color.parseColor(LineColor.getColor(times.get(0).getLine())));
        remoteViews.setImageViewBitmap(R.id.line_image, bitmap);
        remoteViews.setTextViewText(lineTextID, times.get(0).getLine());
        remoteViews.setTextViewText(R.id.destination_text, times.get(0).getDestination());
        remoteViews.setTextViewText(R.id.minutes_text, times.get(0).getMinutes() + " min"); // TODO 2 mins red?

        if (times.size() > 1) {
            remoteViews.setViewVisibility(R.id.layout_line_1, View.VISIBLE);
            Bitmap bitmap2 = createBitmapWithColor(Color.parseColor(LineColor.getColor(times.get(1).getLine())));
            remoteViews.setImageViewBitmap(R.id.line_image_1, bitmap2);
            remoteViews.setTextViewText(lineTextID1, times.get(1).getLine());
            remoteViews.setTextViewText(R.id.destination_text_1, times.get(1).getDestination());
            remoteViews.setTextViewText(R.id.minutes_text_1, times.get(1).getMinutes() + " min");
        } else {
            remoteViews.setViewVisibility(R.id.layout_line_1, View.GONE);
        }

        if (times.size() > 2) {
            remoteViews.setViewVisibility(R.id.layout_line_2, View.VISIBLE);
            Bitmap bitmap3 = createBitmapWithColor(Color.parseColor(LineColor.getColor(times.get(2).getLine())));
            remoteViews.setImageViewBitmap(R.id.line_image_2, bitmap3);
            remoteViews.setTextViewText(lineTextID2, times.get(2).getLine());
            remoteViews.setTextViewText(R.id.destination_text_2, times.get(2).getDestination());
            remoteViews.setTextViewText(R.id.minutes_text_2, times.get(2).getMinutes() + " min");
        } else {
            remoteViews.setViewVisibility(R.id.layout_line_2, View.GONE);
        }

        if (times.size() > 3) {
            remoteViews.setViewVisibility(R.id.layout_line_3, View.VISIBLE);
            Bitmap bitmap4 = createBitmapWithColor(Color.parseColor(LineColor.getColor(times.get(3).getLine())));
            remoteViews.setImageViewBitmap(R.id.line_image_3, bitmap4);
            remoteViews.setTextViewText(lineTextID3, times.get(3).getLine());
            remoteViews.setTextViewText(R.id.destination_text_3, times.get(3).getDestination());
            remoteViews.setTextViewText(R.id.minutes_text_3, times.get(3).getMinutes() + " min");
        } else {
            remoteViews.setViewVisibility(R.id.layout_line_3, View.GONE);
        }

        remoteViews.setOnClickPendingIntent(R.id.cancel_button, cancelPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.resume_button, resumePendingIntent);

        mBuilder.setContent(remoteViews);
        Notification notification = mBuilder.build();
        notification.bigContentView = remoteViews;

        return new WearableNotifications.Builder(mBuilder).setLocalOnly(true).build();  // TODO this way the notification is not expandable on the device
    }

    private Intent createResumeIntent() {
        Intent snoozeIntent = new Intent(this, MainActivity.class);
        snoozeIntent.setAction(Intent.ACTION_RUN);
        return snoozeIntent;
    }

    private Intent createCancelIntent() {
        Intent dismissIntent = new Intent(this, MainActivity.class);
        dismissIntent.setAction(Intent.ACTION_DELETE);
        return dismissIntent;
    }

    private PendingIntent createContentIntent() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, 0, resultIntent, 0);
    }

    Bitmap createBitmapWithColor(int color) {
        Bitmap returnedBitmap = Bitmap.createBitmap(8, 16, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(color);
        return returnedBitmap;
    }

    int getWidth(Station station) {
        if (!station.getOtherLines().isEmpty() && station.getMaxWidthOfTimes() > 4) {
            return 5;
        } else if (!station.getOtherLines().isEmpty() && station.getMaxWidthOfTimes() == 4) {
            return 4;
        } else if ((!station.getOtherLines().isEmpty() && station.getMaxWidthOfTimes() == 3) || station.getType() == StationType.Bus) {
            return 3;
        } else {
            return 2;
        }
    }
}
