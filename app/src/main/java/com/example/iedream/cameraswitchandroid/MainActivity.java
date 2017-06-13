package com.example.iedream.cameraswitchandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.content.Intent;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.BeaconParser;

import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.NestAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements BeaconConsumer, GetCameraProperty, BeaconDetectionManagerInterface, NestAuthorizationInterface{

    private ListView cameraListView;
    CameraAdapter adapter;
    private ArrayList<CameraModel> cameraList = new ArrayList<CameraModel>();
    boolean initAlready = false;
    LocalBroadcastManager broadcastManager;
    String currentSelectedCameraId;

    BeaconManager beaconManager;
    Region region;

    BeaconDetectionManager beaconDetectionManager;
    NestAutorizationManager nestAutorizationManager;
    NestCameraManager nestCameraManager;
    NestStructureManager nestStructureManager;
    StoringManager storingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        broadcastManager = LocalBroadcastManager.getInstance(this);

        cameraListView = (ListView) findViewById(R.id.camera_listview);

        if (!initAlready) {
            initAlready = true;
            NestAPI.setAndroidContext(this);
            NestAPI nest = NestAPI.getInstance();
            beaconManager = BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(this);
            region = new Region("beacon", null, null, null);

            beaconDetectionManager = new BeaconDetectionManager();
            beaconDetectionManager.beaconDetectionManagerInterface = this;

            storingManager = new StoringManager(this);

            nestCameraManager = new NestCameraManager(nest);
            nestCameraManager.getCameraProperty = this;

            nestStructureManager = new NestStructureManager(nest);
            nestStructureManager.cameraInterface = nestCameraManager;

            nestAutorizationManager = new NestAutorizationManager(nest, storingManager, this);
            nestAutorizationManager.nestAuthorizationInterface = this;
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(cameraUpdateReceiver, new IntentFilter("UpdateCamera"));
        LocalBroadcastManager.getInstance(this).registerReceiver(beaconsUpdateReceiver, new IntentFilter("UpdateBeacons"));
        LocalBroadcastManager.getInstance(this).registerReceiver(proximityUpdateReceiver, new IntentFilter("UpdateProximity"));

        cameraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CameraModel camera = cameraList.get(position);
                currentSelectedCameraId = camera.id;
                Intent myIntent = new Intent(MainActivity.this, CameraDetailActivity.class);
                myIntent.putExtra("camera", camera); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //beaconManager.unbind(this);
    }

    private BroadcastReceiver cameraUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CameraModel camera = (CameraModel)intent.getSerializableExtra("camera");
            nestCameraManager.setCameraState(camera);
            try {
                beaconManager.startRangingBeaconsInRegion(region);
            }catch (RemoteException e) {

            }
        }
    };

    private BroadcastReceiver beaconsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CameraModel camera = (CameraModel)intent.getSerializableExtra("camera");
            CameraModel oldModel = getCameraModel(camera.id);
            cameraList.remove(oldModel);
            cameraList.add(camera);
            storingManager.writeCameraBeaconsSetting(camera);
            try {
                beaconManager.startRangingBeaconsInRegion(region);
            }catch (RemoteException e) {

            }
        }
    };

    private BroadcastReceiver proximityUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CameraModel camera = (CameraModel)intent.getSerializableExtra("camera");
            CameraModel oldModel = getCameraModel(camera.id);
            cameraList.remove(oldModel);
            cameraList.add(camera);
            storingManager.writeProximitySetting(camera);
            try {
                beaconManager.startRangingBeaconsInRegion(region);
            }catch (RemoteException e) {

            }
        }
    };

    @Override
    public void onBeaconServiceConnect()  {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    beaconManager.startMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {

                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {

                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                if (beaconManager.getRangedRegions().contains(region) == false) {
                    try {
                        beaconManager.startRangingBeaconsInRegion(region);
                    } catch (RemoteException e) {

                    }
                }
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
                    Beacon beacon = (Beacon) iterator.next();
                    String id = beacon.getId1().toString();
                    double distance = beacon.getDistance();
                    beaconDetectionManager.beaconDetected(id, distance);
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("beacon", null, null, null));
        } catch (RemoteException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
       nestAutorizationManager.authFlowCompleted(requestCode, resultCode, intent);
    }

    public String getCameraAway(String structureId) {
        return nestStructureManager.getAwayForStructureId(structureId);
    }

    public Proximity getCameraProximity(String cameraId) {
        return storingManager.readProximitySettings(cameraId);
    }

    public Set<String> getCameraBeacons(String cameraId) {
        return storingManager.readCameraBeaconsSetting(cameraId);
    }

    public void cameraUpdated(ArrayList<CameraModel> newCameraList) {
        cameraList = newCameraList;
        adapter = new CameraAdapter(this, cameraList);
        cameraListView.setAdapter(adapter);
        Intent intent = new Intent("CameraUpdated");
        CameraModel camera = getCameraModel(currentSelectedCameraId);
        if (camera != null) {
            intent.putExtra("camera", camera);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private CameraModel getCameraModel(String cameraId) {
        if (cameraId == null) {
            return null;
        }
        for (Iterator iterator = cameraList.iterator(); iterator.hasNext();) {
            CameraModel cameraModel = (CameraModel) iterator.next();
            if (cameraModel.id.equalsIgnoreCase(cameraId)) {
                return cameraModel;
            }
        }
        return null;
    }

    public Set<CameraModel> getAllCameraModelForBeaconId(String beaconId) {
        Set<CameraModel> allCameras = new HashSet<CameraModel>();
        for (Iterator iterator = cameraList.iterator(); iterator.hasNext();) {
            CameraModel cameraModel = (CameraModel) iterator.next();
            if (cameraModel.beacons != null) {
                for (Iterator beaconIterator = cameraModel.beacons.iterator(); beaconIterator.hasNext();) {
                    beaconIterator.next();
                    Set<String> beacons = (Set<String>)cameraModel.beacons;
                    if (beacons.contains(beaconId.toUpperCase())) {
                        allCameras.add(cameraModel);
                    }
                }
            }
        }
        return allCameras;
    }

    public void beaconUpdateCameraState(CameraModel cameraModel) {
        nestCameraManager.setCameraState(cameraModel);
    }

    public void authorizationComplete() {
        nestStructureManager.listenToStructure();
        nestCameraManager.listenToCamera();
    }
}
