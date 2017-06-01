package com.example.iedream.cameraswitchandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nestlabs.sdk.NestToken;

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
}
