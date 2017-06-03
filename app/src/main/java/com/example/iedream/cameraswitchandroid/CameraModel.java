package com.example.iedream.cameraswitchandroid;

import com.nestlabs.sdk.Camera;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by iedream on 2017-06-02.
 */

public class CameraModel implements Serializable {
    String name;
    String id;
    boolean isOn;
    boolean isOnline;
    String location;
    Proximity proximity;
    Set <String> beacons;

    public CameraModel (Camera newCamera, String newLocation, Proximity newProximity, Set <String> newBeacons) {
        name = newCamera.getName();
        isOnline = newCamera.isOnline();
        isOn = newCamera.isStreaming();
        id = newCamera.getDeviceId();
        location = newLocation;
        proximity = newProximity;
        beacons = newBeacons;
    }
}
