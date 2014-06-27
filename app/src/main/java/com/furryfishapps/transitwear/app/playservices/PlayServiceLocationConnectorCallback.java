package com.furryfishapps.transitwear.app.playservices;

import com.google.android.gms.location.LocationClient;

public interface PlayServiceLocationConnectorCallback {
    void connectionEstablished(LocationClient connectedClient);
    void connectionNotEstablished(boolean locationDisabledByUser);
    void disconnected();
}
