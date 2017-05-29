package com.example.iedream.cameraswitchandroid;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;

import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestToken;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.Structure;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView cameraListView;
    private ArrayList<String> cameraNames = new ArrayList<String>();
    int tokenCode = 123;
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

        nest = NestAPI.getInstance();
        nest.setConfig(clientID, clientSecret, redirectURL);
        NestToken token = readToken();
        if (token != null) {
            authorized(token);
        } else {
            nest.launchAuthFlow(this, tokenCode);
        }
        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cameraNames);
        //cameraListView.setAdapter(adapter);
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
                nest.addCameraListener(new CameraListener() {
                    @Override
                    public void onUpdate(ArrayList<Camera> cameras) {
                        int i = 0;
                    }

                });
            }

            @Override
            public void onAuthFailure(NestException e) {
                int i = 1;
            }

            @Override
            public void onAuthRevoked() {
                int i = 1;
            }
        });
    }
}
