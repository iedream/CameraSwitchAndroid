package com.example.iedream.cameraswitchandroid;

import java.util.Set;

/**
 * Created by iedream on 2017-06-04.
 */

public interface BeaconDetectionManagerInterface {
    public Set<CameraModel> getAllCameraModelForBeaconId(String beaconId);
    public void beaconUpdateCameraState(CameraModel cameraModel);
}
