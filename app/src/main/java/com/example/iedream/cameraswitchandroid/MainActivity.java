package com.example.iedream.cameraswitchandroid;

import android.content.SharedPreferences;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
    private ArrayList<String> cameraNames = new ArrayList<String>();
    int tokenCode = 123;
    private BeaconManager beaconManager;
    boolean initAlready = false;
    NestAPI nest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NestAPI.setAndroidContext(this);
        String clientID = "85165356-5b33-4682-9979-c59145d68c55";
        String clientSecret = "YfbUUz0rbf8gkHis0vziATz2q";
        String redirectURL = "https://localhost:8080/auth/nest/callback";
        cameraNames.add("ref");

        cameraListView = (ListView) findViewById(R.id.camera_listview);

        nest = NestAPI.getInstance();
        nest.setConfig(clientID, clientSecret, redirectURL);
        NestToken token = readToken();
        if (token != null) {
            authorized(token);
        } else {
            nest.launchAuthFlow(this, tokenCode);
        }

        if (!initAlready) {
            initAlready = true;
            beaconManager = BeaconManager.getInstanceForApplication(this);
            beaconManager.getBeaconParsers().add(new BeaconParser().
                    setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.bind(this);
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cameraNames);
        cameraListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void saveToken(NestToken token) {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CameraSwitch",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token.getToken());
        editor.putLong("ExpiresIn", token.getExpiresIn());
        editor.apply();
    }

    private NestToken readToken() {
        SharedPreferences settings = getApplicationContext().getSharedPreferences("CameraSwitch", 0);
        String tokenString = settings.getString("token", null);
        Long expireIn = settings.getLong("ExpiresIn", -1);
        if (tokenString != null && expireIn != -1) {
            NestToken token = new NestToken(tokenString, expireIn);
            return token;
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK || requestCode != tokenCode) {
            return;
        }
        NestToken token = NestAPI.getAccessTokenFromIntent(intent);
        saveToken(token);
        authorized(token);
    }

    private void authorized(NestToken token) {
        nest.authWithToken(token, new NestListener.AuthListener() {
            @Override
            public void onAuthSuccess(){
//                nest.addCameraListener(new CameraListener() {
//                    @Override
//                    public void onUpdate(@NonNull ArrayList<Camera> cameras) {
//
//                    }
//
//                });
            }

            @Override
            public void onAuthFailure(NestException e) {

            }

            @Override
            public void onAuthRevoked() {

            }
        });
    }

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
}
