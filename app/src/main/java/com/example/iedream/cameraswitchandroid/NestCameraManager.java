package com.example.iedream.cameraswitchandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestToken;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.Structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by iedream on 2017-05-31.
 */

public class NestCameraManager {
    NestAPI nest;
    Map<String, CameraModel> cameraMap = new HashMap<String, CameraModel>();
    GetCameraProperty getCameraProperty;

    public NestCameraManager(NestAPI nestAPI) {
        nest = nestAPI;
    }

    public void listenToCamera() {
        nest.addCameraListener(new NestListener.CameraListener() {
            @Override
            public void onUpdate(@NonNull ArrayList<Camera> cameras) {
                for (Camera camera: cameras) {
                    String cameraStructureId = camera.getStructureId();
                    String cameraAway = getCameraProperty.getCameraAway(cameraStructureId);
                    Proximity cameraProximity = getCameraProperty.getCameraProximity(camera.getDeviceId());
                    Set <String> cameraBeacons = getCameraProperty.getCameraBeacons(camera.getDeviceId());
                    CameraModel cameraModel = new CameraModel(camera, cameraAway, cameraProximity, cameraBeacons);
                    cameraMap.put(camera.getDeviceId(), cameraModel);
                    getCameraProperty.cameraUpdated(new ArrayList<CameraModel>(cameraMap.values()));
                }
            }
        });
    }

    public void setCameraState(CameraModel cameraModel) {
        nest.cameras.setIsStreaming(cameraModel.id, cameraModel.isOn);
    }
}
