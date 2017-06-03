package com.example.iedream.cameraswitchandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.NestToken;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by iedream on 2017-05-31.
 */

public class StoringManager {
    AppCompatActivity context;

    public StoringManager(AppCompatActivity mainContext) {
        context = mainContext;
    }

    public NestToken readToken() {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch", 0);
        String tokenString = settings.getString("token", null);
        Long expireIn = settings.getLong("ExpiresIn", -1);
        if (tokenString != null && expireIn != -1) {
            NestToken token = new NestToken(tokenString, expireIn);
            return token;
        }
        return null;
    }

    public void saveToken(NestToken token) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("token", token.getToken());
        editor.putLong("ExpiresIn", token.getExpiresIn());
        editor.apply();
    }

    public Proximity readProximitySettings(String cameraId) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch",0);
        int proximityInt = settings.getInt(cameraId, 0);
        Proximity proximity = Proximity.values()[proximityInt];
        return proximity;
    }

    public void writeProximitySetting(CameraModel camera) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch",0);
        SharedPreferences.Editor editor = settings.edit();
        String key = camera.id + "/proximity";
        editor.putInt(key, camera.proximity.ordinal());
        editor.apply();
    }

    public Set<String> readCameraBeaconsSetting(String cameraId) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch",0);
        Set <String> beacons = settings.getStringSet(cameraId, null);
        return beacons;
    }

    public void writeCameraBeaconsSetting(CameraModel camera) {
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("CameraSwitch",0);
        SharedPreferences.Editor editor = settings.edit();
        String key = camera.id + "/beacons";
        editor.putStringSet(key, camera.beacons);
        editor.apply();
    }
}
