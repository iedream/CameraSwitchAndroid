package com.example.iedream.cameraswitchandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.nestlabs.sdk.Camera;

public class CameraDetailActivity extends Activity {
    CameraModel camera;
    Switch onOffSwitch;
    TextView stateLabel;
    TextView locationLabel;
    RadioGroup radioGroup;
    CameraUpdateActivity cameraUpdateActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameradetail);

        camera = (CameraModel)getIntent().getSerializableExtra("camera");

        onOffSwitch = (Switch)findViewById(R.id.cameraSwitch);
        stateLabel = (TextView)findViewById(R.id.cameraState);
        locationLabel = (TextView)findViewById(R.id.location);
        radioGroup = (RadioGroup)findViewById(R.id.ProximityGroup);

        onOffSwitch.setChecked(camera.isOn);
        stateLabel.setText(camera.name);
        locationLabel.setText(camera.location);
        setRadioButton(camera.proximity);

        onOffSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.isOn = !camera.isOn;
                cameraUpdateActivity.updateCameraState(camera);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (R.id.instantRadioButton == i) {
                    camera.proximity = Proximity.Instant;
                } else if (R.id.closeRadioButton == i) {
                    camera.proximity = Proximity.Close;
                } else if (R.id.mediumRadioButton == i) {
                    camera.proximity = Proximity.Medium;
                } else if (R.id.farRadioButton == i) {
                    camera.proximity = Proximity.Far;
                } else if (R.id.distantRadioButton == i) {
                    camera.proximity = Proximity.Distant;
                }
                cameraUpdateActivity.updateProximitySetting(camera);
            }
        });
    }

    private void setRadioButton(Proximity proximity) {
        switch (proximity) {
            case Instant:
                radioGroup.check(R.id.instantRadioButton);
                break;
            case Close:
                radioGroup.check(R.id.closeRadioButton);
                break;
            case Medium:
                radioGroup.check(R.id.mediumRadioButton);
                break;
            case Far:
                radioGroup.check(R.id.farRadioButton);
                break;
            case Distant:
                radioGroup.check(R.id.distantRadioButton);
                break;
        }
    }

}
