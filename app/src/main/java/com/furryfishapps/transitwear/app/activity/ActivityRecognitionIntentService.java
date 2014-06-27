package com.furryfishapps.transitwear.app.activity;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.furryfishapps.transitwear.app.location.LocationIntentService;
import com.furryfishapps.transitwear.app.playservices.PlayServiceConnector;
import com.furryfishapps.transitwear.app.playservices.PlayServiceLocationConnectorCallback;
import com.furryfishapps.transitwear.app.playservices.PlayServiceLocationConnectorImpl;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class ActivityRecognitionIntentService extends IntentService implements PlayServiceLocationConnectorCallback {
    private static final String TAG = "ActivityRecognitionIntentService";
    private static final int MOVING_NOTIFICATION_COUNT_MIN = 1;
    private static int movingCount = 0;
    private static boolean playConnectionIsBeingEstablished = false;

    private PlayServiceConnector playServiceConnector;

    public ActivityRecognitionIntentService() {
        this(null);
    }

    public ActivityRecognitionIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int confidence = mostProbableActivity.getConfidence();
            if (confidence > 65) {
                if (moving(mostProbableActivity)) {
                    movingCount++;
                    Log.i(TAG, "Moving! Will increase count to " + movingCount);
                } else {
                    movingCount--;
                    movingCount = Math.max(0, movingCount);
                    Log.d(TAG, "Not Moving! Will decrease count to " + movingCount);
                }

                if (movingCount >= MOVING_NOTIFICATION_COUNT_MIN && !locationRetrievalStillInProgress() && !playConnectionIsBeingEstablished) {
                    playConnectionIsBeingEstablished = true;
                    movingCount = 0;
                    Log.i(TAG, "Retrieving times for stations nearby");
                    if (playServiceConnector == null) {
                        playServiceConnector = new PlayServiceLocationConnectorImpl(getApplicationContext(), this);
                    }
                    playServiceConnector.connectToPlayServices();
                } else {
                    Log.d(TAG, "Not retrieving times: " + movingCount + " | " + locationRetrievalStillInProgress() + " | " + playConnectionIsBeingEstablished);
                }
            } else {
                Log.v(TAG, "Not enough confidence");
            }

            Log.v(TAG, "Moving count " + movingCount + " Activity: " + mostProbableActivity.getType());
        }
    }

    private boolean moving(DetectedActivity mostProbableActivity) {
        return mostProbableActivity.getType() == DetectedActivity.IN_VEHICLE ||
                mostProbableActivity.getType() == DetectedActivity.ON_BICYCLE ||
                mostProbableActivity.getType() == DetectedActivity.ON_FOOT ||
                mostProbableActivity.getType() == DetectedActivity.RUNNING ||
                mostProbableActivity.getType() == DetectedActivity.WALKING;
    }

    /**
     * Play Service Callback
     */

    @Override
    public void connectionEstablished(LocationClient connectedClient) {

        if (connectedClient == null || !connectedClient.isConnected()) {
            Log.w(TAG, "Location client null/disconnected " + connectedClient);
            playConnectionIsBeingEstablished = false;
            return;
        }

        if (!locationRetrievalStillInProgress()) {
            Log.d(TAG, "No running request, will start retrieving location");

            LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                    setNumUpdates(1).setInterval(7000).setExpirationDuration(15000); // TODO check if this is enough


            Log.v(TAG, "Requesting location: " + request);

            PendingIntent pendingIntent = createLocationPendingIntent();
            connectedClient.requestLocationUpdates(request, pendingIntent);
            playConnectionIsBeingEstablished = false;
        } else {
            Log.w(TAG, "Location request still running, will skip this one");
            playConnectionIsBeingEstablished = false;
        }
    }

    PendingIntent createLocationPendingIntent() {
        Intent intent = new Intent(this, LocationIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void connectionNotEstablished(boolean locationDisabledByUser) {
        Log.d(TAG, "Not Connected to play services");
        playConnectionIsBeingEstablished = false;
    }

    @Override
    public void disconnected() {
        Log.d(TAG, "Disconnected from play services");
        playConnectionIsBeingEstablished = false;
    }

    boolean locationRetrievalStillInProgress() {
        Intent intent = new Intent(this, LocationIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }
}
