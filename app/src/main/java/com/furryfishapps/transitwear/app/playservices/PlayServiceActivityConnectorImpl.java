package com.furryfishapps.transitwear.app.playservices;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.ActivityRecognitionClient;

public class PlayServiceActivityConnectorImpl extends PlayServiceLocationConnectorImpl {
    ActivityRecognitionClient client;

    public PlayServiceActivityConnectorImpl(Context context, PlayServiceActivityConnectorCallback callbackHandler) {
        super();
        super.context = context;
        super.callbackHandler = callbackHandler;
    }

    @Override
    public void connectToPlayServices() {
        Log.i(TAG, "Connecting to Play Service");

        int resultCode = googlePlayServicesAvailable();
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.v(TAG, "Google Play services available, will connect client");
            if (client == null) {
                Log.v(TAG, "New ActivityRecognitionClient necessary!");
                client = new ActivityRecognitionClient(context, this, this);
            }
            if (!client.isConnected()) {
                Log.v(TAG, "ActivityRecognitionClient not connected!");
                client.disconnect(); // this seems to be necessary to
                client.connect();
            } else {
                Log.v(TAG, "ActivityRecognitionClient connected, calling the callback handler directly!");
                ((PlayServiceActivityConnectorCallback)callbackHandler).connectionEstablished(client);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "Successfully established connection");
        ((PlayServiceActivityConnectorCallback)callbackHandler).connectionEstablished(client);
    }
}
