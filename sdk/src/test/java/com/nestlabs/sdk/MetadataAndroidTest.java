/*
 * Copyright 2016, Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nestlabs.sdk;

import android.os.Build;
import android.os.Parcel;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import junit.framework.Assert;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class MetadataAndroidTest {

    public static final String TEST_METADATA_JSON = "/test-metadata.json";
    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testMetadataToParcel() {
        try {
            String json = IOUtils.toString(
                    this.getClass().getResourceAsStream(TEST_METADATA_JSON), "utf-8");
            Metadata metadata = mapper.readValue(json, Metadata.class);

            Parcel parcel = Parcel.obtain();
            metadata.writeToParcel(parcel, 0);

            parcel.setDataPosition(0);

            Metadata metadataFromParcel = Metadata.CREATOR.createFromParcel(parcel);
            assertEquals(metadata, metadataFromParcel);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
