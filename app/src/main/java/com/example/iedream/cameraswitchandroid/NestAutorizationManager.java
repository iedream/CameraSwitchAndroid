package com.example.iedream.cameraswitchandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestException;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.NestToken;

/**
 * Created by iedream on 2017-05-31.
 */

public class NestAutorizationManager extends AppCompatActivity{
    NestAPI nest;
    int tokenCode = 123;
    StoringManager storingManager;
    NestAuthorizationInterface nestAuthorizationInterface;
    AppCompatActivity context;

    public NestAutorizationManager(NestAPI nestAPI, StoringManager newStoringManager, AppCompatActivity mainContext) {
        nest = nestAPI;
        storingManager = newStoringManager;
        context = mainContext;

        String clientID = "85165356-5b33-4682-9979-c59145d68c55";
        String clientSecret = "YfbUUz0rbf8gkHis0vziATz2q";
        String redirectURL = "https://localhost:8080/auth/nest/callback";

        nest.setConfig(clientID, clientSecret, redirectURL);
        NestToken token = storingManager.readToken();
        if (token != null) {
            authorized(token);
        } else {
            nest.launchAuthFlow(context, tokenCode);
        }
    }

    public void authFlowCompleted(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK || requestCode != tokenCode) {
            return;
        }
        NestToken token = NestAPI.getAccessTokenFromIntent(intent);
        storingManager.saveToken(token);
        authorized(token);
    }

    private void authorized(NestToken token) {
        nest.authWithToken(token, new NestListener.AuthListener() {
            @Override
            public void onAuthSuccess(){
                nestAuthorizationInterface.authorizationComplete();
            }

            @Override
            public void onAuthFailure(NestException e) {

            }

            @Override
            public void onAuthRevoked() {

            }
        });
    }
}
