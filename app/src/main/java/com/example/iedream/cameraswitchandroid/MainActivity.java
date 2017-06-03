package com.example.iedream.cameraswitchandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.app.Activity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.BeaconParser;

import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestToken;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private ListView cameraListView;
    private ArrayList<CameraModel> cameraList = new ArrayList<CameraModel>();
    boolean initAlready = false;
    BeaconManager beaconManager;

    NestAutorizationManager nestAutorizationManager;
    NestCameraManager nestCameraManager;
    StoringManager storingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraListView = (ListView) findViewById(R.id.camera_listview);
        CameraAdapter adapter = new CameraAdapter(this, cameraList);
        cameraListView.setAdapter(adapter);


        if (!initAlready) {
            initAlready = true;
            NestAPI.setAndroidContext(this);
            NestAPI nest = NestAPI.getInstance();
            beaconManager = BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(this);

            storingManager = new StoringManager(this);
            nestCameraManager = new NestCameraManager(nest);
            nestAutorizationManager = new NestAutorizationManager(nest, storingManager, this);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(cameraListenerReceiver, new IntentFilter("StartListeningCamera"));

        cameraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CameraModel camera = cameraList.get(position);
                Intent myIntent = new Intent(MainActivity.this, CameraDetailActivity.class);
                myIntent.putExtra("camera", camera); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private BroadcastReceiver cameraListenerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nestCameraManager.listenToCamera();
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
                    double distance = beacon.getDistance();
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("8492E75F-4FD6-469D-B132-043FE94921D8", null, null, null));
        } catch (RemoteException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
       nestAutorizationManager.authFlowCompleted(requestCode, resultCode, intent);
    }
}
