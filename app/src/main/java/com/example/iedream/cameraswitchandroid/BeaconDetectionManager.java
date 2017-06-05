package com.example.iedream.cameraswitchandroid;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by iedream on 2017-06-04.
 */

public class BeaconDetectionManager {

    BeaconDetectionManagerInterface beaconDetectionManagerInterface;

    public BeaconDetectionManager() {

    }

    public void beaconDetected(String beaconId, double distance) {
        ProximityHelper proximityComparator = new ProximityHelper();
        Proximity currentProximity = proximityComparator.convertDistanceToProximity(distance);
        Set<CameraModel>cameraModels = beaconDetectionManagerInterface.getAllCameraModelForBeaconId(beaconId);
        for (Iterator iterator = cameraModels.iterator(); iterator.hasNext();) {
            CameraModel cameraModel = (CameraModel) iterator.next();
            Boolean cameraOn = !proximityComparator.include(currentProximity, cameraModel.proximity);
            if (cameraModel.isOn != cameraOn) {
                cameraModel.isOn = cameraOn;
                beaconDetectionManagerInterface.beaconUpdateCameraState(cameraModel);
            }
        }
    }
}
