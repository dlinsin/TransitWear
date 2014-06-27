package com.furryfishapps.transitwear.app.playservices;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class PlayServiceLocationConnectorImpl implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, PlayServiceConnector, DialogInterface.OnCancelListener {
    static final String TAG = PlayServiceLocationConnectorImpl.class.getSimpleName();
    Context context;
    PlayServiceLocationConnectorCallback callbackHandler;
    LocationClient client;

    public PlayServiceLocationConnectorImpl(){};

    public PlayServiceLocationConnectorImpl(Context context, PlayServiceLocationConnectorCallback callbackHandler) {
        this.context = context;
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void connectToPlayServices() {
        Log.i(TAG, "Connecting to Play Service");

        int resultCode = googlePlayServicesAvailable();
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.v(TAG, "Google Play services available, will connect client");
            if (client == null) {
                Log.v(TAG, "New LocationClient necessary!");
                client = new LocationClient(context, this, this);
            }
            if (!client.isConnected()) {
                Log.v(TAG, "LocationClient not connected!");
                client.disconnect(); // this seems to be necessary to
                client.connect();
            } else {
                Log.v(TAG, "LocationClient connected, calling the callback handler directly!");
                callbackHandler.connectionEstablished(client);
            }
        }
    }

    int googlePlayServicesAvailable() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "Successfully established connection");
        callbackHandler.connectionEstablished(client);
    }

    @Override
    public void onDisconnected() {
        Log.v(TAG, "Disconnected from Play Services");
        callbackHandler.disconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Couldn't connect to Google Play Services");
        callbackHandler.connectionNotEstablished(false);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        Log.w(TAG, "User canceled Play Service installation");
        callbackHandler.connectionNotEstablished(true);
    }

    public void disconnect() {
        if (client != null) {
            client.disconnect();
        }
    }
}
