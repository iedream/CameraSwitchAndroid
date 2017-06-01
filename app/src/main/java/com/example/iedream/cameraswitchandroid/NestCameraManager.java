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

/**
 * Created by iedream on 2017-05-31.
 */

public class NestCameraManager {
    NestAPI nest;

    public NestCameraManager(NestAPI nestAPI) {
        nest = nestAPI;
    }

    public void listenToCamera() {
        nest.addCameraListener(new NestListener.CameraListener() {
            @Override
            public void onUpdate(@NonNull ArrayList<Camera> cameras) {

            }
        });
    }
}
