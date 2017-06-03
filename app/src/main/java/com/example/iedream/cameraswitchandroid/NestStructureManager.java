package com.example.iedream.cameraswitchandroid;

import android.support.annotation.NonNull;

import com.nestlabs.sdk.Camera;
import com.nestlabs.sdk.NestAPI;
import com.nestlabs.sdk.NestListener;
import com.nestlabs.sdk.Structure;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by iedream on 2017-06-02.
 */

public class NestStructureManager {
    NestAPI nest;
    Map<String, String> map = new HashMap<String, String>();

    public NestStructureManager(NestAPI nestAPI) {
        nest = nestAPI;
    }

    private void listenToStructure() {
        nest.addStructureListener(new NestListener.StructureListener() {
            @Override
            public void onUpdate(@NonNull ArrayList<Structure> structures) {
                for (Structure structure: structures) {
                    map.put(structure.getStructureId(), structure.getAway());
                }
            }
        });
    }

    public String getAwayForStructureId(String structureId) {
        String away = map.get(structureId);
        return away;
    }
}
