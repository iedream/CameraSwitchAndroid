package com.example.iedream.cameraswitchandroid;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by iedream on 2017-06-02.
 */

public interface GetCameraProperty {
    public String getCameraAway(String structureId);
    public Proximity getCameraProximity(String cameraId);
    public Set <String> getCameraBeacons(String cameraId);
    public void cameraUpdated(ArrayList<CameraModel> newCameraList);
}
