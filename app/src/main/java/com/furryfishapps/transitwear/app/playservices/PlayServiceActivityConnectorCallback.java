package com.furryfishapps.transitwear.app.playservices;

import com.google.android.gms.location.ActivityRecognitionClient;

public interface PlayServiceActivityConnectorCallback extends PlayServiceLocationConnectorCallback {
    void connectionEstablished(ActivityRecognitionClient connectedClient);
}
