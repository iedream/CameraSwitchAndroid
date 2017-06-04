package com.example.iedream.cameraswitchandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.preference.DialogPreference;
import android.support.annotation.IdRes;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.nestlabs.sdk.Camera;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class CameraDetailActivity extends Activity {
    CameraModel camera;
    Switch onOffSwitch;
    TextView stateLabel;
    TextView locationLabel;
    RadioGroup radioGroup;
    CameraUpdateActivity cameraUpdateActivity;
    Button addBeaconButton;
    ListView beaconTable;
    ArrayList <String> beaconList = new ArrayList<String>();
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameradetail);

        camera = (CameraModel)getIntent().getSerializableExtra("camera");

        onOffSwitch = (Switch)findViewById(R.id.cameraSwitch);
        stateLabel = (TextView)findViewById(R.id.cameraState);
        locationLabel = (TextView)findViewById(R.id.location);
        radioGroup = (RadioGroup)findViewById(R.id.ProximityGroup);
        addBeaconButton = (Button)findViewById(R.id.addBeaconButton);
        beaconTable = (ListView)findViewById(R.id.beaconTable);

        onOffSwitch.setChecked(camera.isOn);
        stateLabel.setText(camera.name);
        locationLabel.setText(camera.location);
        setRadioButton(camera.proximity);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Add iBeacon ID");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        ArrayAdapter<String> beaconsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, beaconList);
        beaconTable.setAdapter(beaconsAdapter);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                beaconList.add(input.getText().toString());
                camera.beacons = new HashSet<String>(beaconList);
                cameraUpdateActivity.updateBeaconsSetting(camera);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        onOffSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.isOn = !camera.isOn;
                cameraUpdateActivity.updateCameraState(camera);
            }
        });

        addBeaconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
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
