package com.furryfishapps.transitwear.app;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preview.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.furryfishapps.transitwear.app.location.LocationIntentService;
import com.furryfishapps.transitwear.app.playservices.PlayServiceActivityConnectorCallback;
import com.furryfishapps.transitwear.app.playservices.PlayServiceConnector;
import com.furryfishapps.transitwear.app.playservices.PlayServiceLocationConnectorImpl;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class MainActivity extends FragmentActivity implements PlayServiceActivityConnectorCallback {
    private static final String TAG = "MainActivity";
    public static final int FIFTEEN_MINUTES = 900000;
    public static final String SHARED_PREFS_START_TIME = "SHARED_PREFS_START_TIME";
    private PlayServiceConnector locationServiceConnector;
    private Switch notificationSwitch;
    SharedPreferences sharedPrefs;
    private boolean restartNotifications = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "On Create!");
        setContentView(R.layout.activity_main);
        sharedPrefs = getSharedPreferences("TRANSIT_WEAR", Context.MODE_PRIVATE);
        locationServiceConnector = new PlayServiceLocationConnectorImpl(this, this);
        initializeSwitch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "On resume");
        updateInitialNotificationSwitchState();
        if (restartNotifications) {
            notificationSwitch.setChecked(true);
            if (locationServiceConnector == null) {
                locationServiceConnector = new PlayServiceLocationConnectorImpl(this, this);
            }
            locationServiceConnector.connectToPlayServices();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "On new intent called!");
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_RUN)) {
            Log.d(TAG, "Started from notification, will restart intent");
            moveTaskToBack(true);
            restartNotifications = true;
        } else if (action != null && action.equals(Intent.ACTION_DELETE)) {
            Log.d(TAG, "Started from notification, will cancel intent");
            moveTaskToBack(true);
            restartNotifications = false;
            disconnect();
        } else {
            restartNotifications = false;
        }
    }

    void initializeSwitch() {
        notificationSwitch = (Switch) findViewById(R.id.notificationSwitch);

        updateInitialNotificationSwitchState();

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v(TAG, "Changing noti switch state: " + isChecked);
                MainActivity.this.updateSwitchAndDescription((Switch) buttonView, isChecked);
                if (isChecked) {
                    buttonView.setEnabled(false);
                    locationServiceConnector.connectToPlayServices();
                } else {
                    Log.d(TAG, "Disconnecting client!");
                    disconnect();
                }
            }
        });
    }

    private void updateInitialNotificationSwitchState() {
        boolean isChecked = isPendingIntentActive(createLocationPendingIntent(true));
        notificationSwitch.setChecked(isChecked);
        updateSwitchAndDescription(notificationSwitch, isChecked);
    }

    private boolean isPendingIntentActive(PendingIntent locationPendingIntent) {
        if (locationPendingIntent == null) {
            return false;
        }
        long startTime = sharedPrefs.getLong(SHARED_PREFS_START_TIME, System.currentTimeMillis());
        return (System.currentTimeMillis() - startTime) <= FIFTEEN_MINUTES;
    }

    void updateSwitchAndDescription(Switch notificationSwitch, boolean isChecked) {
        if (isChecked) {
            notificationSwitch.setText(getResources().getText(R.string.switchOnDescription));
        } else {
            notificationSwitch.setText(getResources().getText(R.string.switchOffDescription));
        }
    }

    /**
     * Activity Play Service Handling *
     */

    @Override
    public void connectionEstablished(ActivityRecognitionClient connectedClient) {/** not needed **/}

    PendingIntent createLocationPendingIntent(boolean forCheck) {
        Intent intent = new Intent(this, LocationIntentService.class);
        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (forCheck) {
            flag = PendingIntent.FLAG_NO_CREATE;
        }
        return PendingIntent.getService(this, 0, intent, flag);
    }


    @Override
    public void connectionEstablished(LocationClient connectedClient) {
        Log.d(TAG, "Connected to play services");
        notificationSwitch.setEnabled(true);

        if (notificationSwitch.isChecked()) {
            Log.d(TAG, "Requesting location updates");

            LocationRequest request = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).
                    setInterval(20000).setExpirationDuration(FIFTEEN_MINUTES); // TODO check if this is enough

            Log.v(TAG, "Requesting location: " + request);
            connectedClient.requestLocationUpdates(request, createLocationPendingIntent(false));

            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putLong(SHARED_PREFS_START_TIME, System.currentTimeMillis());
            editor.apply();
        } else {
            disconnect();
        }
    }

    @Override
    public void connectionNotEstablished(boolean locationDisabledByUser) {
        Log.d(TAG, "Not Connected to play services (disabled: " + locationDisabledByUser + ")");
        disconnect();
    }

    void disconnect() {
        NotificationManagerCompat.from(this).cancelAll();
        notificationSwitch.setEnabled(true);
        updateSwitchAndDescription(notificationSwitch, false);
        PendingIntent locationPendingIntent = createLocationPendingIntent(true);
        if (locationPendingIntent != null) {
            Log.d(TAG, "Removing location pending intent!");
            locationPendingIntent.cancel();
        } else {
            Log.d(TAG, "No Location pending intent exists");
        }
        if (locationServiceConnector != null) {
            locationServiceConnector.disconnect();
        }
    }

    @Override
    public void disconnected() {
        Log.d(TAG, "Disconnected from play services!");
        disconnect();
    }
}
