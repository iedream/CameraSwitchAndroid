package com.example.iedream.cameraswitchandroid;

/**
 * Created by iedream on 2017-06-02.
 */

public interface CameraUpdateActivity
{
    public void updateCameraState(CameraModel camera);
    public void updateProximitySetting(CameraModel camera);
    public void updateBeaconsSetting(CameraModel camera);
}
