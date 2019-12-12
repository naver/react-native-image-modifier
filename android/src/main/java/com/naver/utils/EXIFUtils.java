/**
 * react-native-image-modifier
 * Copyright (c) 2019-present NAVER Corp.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.naver.utils;

import android.media.ExifInterface;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EXIFUtils {

    private static final Map<Class, List<String>> EXIF_VALUE_KEYS = new HashMap<>();
    static {
        // Double
        List<String> doubleType = new ArrayList<>();
        doubleType.add(ExifInterface.TAG_APERTURE_VALUE);
        doubleType.add(ExifInterface.TAG_BRIGHTNESS_VALUE);
        doubleType.add(ExifInterface.TAG_EXPOSURE_BIAS_VALUE);
        doubleType.add(ExifInterface.TAG_EXPOSURE_TIME);
        doubleType.add(ExifInterface.TAG_F_NUMBER);
        doubleType.add(ExifInterface.TAG_FOCAL_LENGTH);
        doubleType.add(ExifInterface.TAG_SHUTTER_SPEED_VALUE);

        EXIF_VALUE_KEYS.put(Double.class, doubleType);

        // Integer
        List<String> intType = new ArrayList<>();
        intType.add(ExifInterface.TAG_COLOR_SPACE);
        intType.add(ExifInterface.TAG_EXPOSURE_MODE);
        intType.add(ExifInterface.TAG_EXPOSURE_PROGRAM);
        intType.add(ExifInterface.TAG_FLASH);
        intType.add(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);
        intType.add(ExifInterface.TAG_ISO_SPEED_RATINGS);
        intType.add(ExifInterface.TAG_METERING_MODE);
        intType.add(ExifInterface.TAG_PIXEL_X_DIMENSION);
        intType.add(ExifInterface.TAG_PIXEL_Y_DIMENSION);
        intType.add(ExifInterface.TAG_SCENE_CAPTURE_TYPE);
        intType.add(ExifInterface.TAG_SENSING_METHOD);
        intType.add(ExifInterface.TAG_SUBJECT_AREA);
        intType.add(ExifInterface.TAG_WHITE_BALANCE);
        intType.add(ExifInterface.TAG_SCENE_TYPE);

        EXIF_VALUE_KEYS.put(Integer.class, intType);

        // String
        List<String> stringType = new ArrayList<>();
//        stringType.add(ExifInterface.TAG_COMPONENTS_CONFIGURATION);
        stringType.add(ExifInterface.TAG_DATETIME_DIGITIZED);
        stringType.add(ExifInterface.TAG_DATETIME_ORIGINAL);
        stringType.add(ExifInterface.TAG_EXIF_VERSION);
        stringType.add(ExifInterface.TAG_FLASHPIX_VERSION);
        stringType.add(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED);
        stringType.add(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL);

        EXIF_VALUE_KEYS.put(String.class, stringType);
    };

    private static final Map<Class, List<String>> TIFF_VALUE_KEYS = new HashMap<>();
    static {
        // Double
        List<String> doubleType = new ArrayList<>();
        doubleType.add(ExifInterface.TAG_X_RESOLUTION);
        doubleType.add(ExifInterface.TAG_Y_RESOLUTION);

        TIFF_VALUE_KEYS.put(Double.class, doubleType);

        // Integer
        List<String> intType = new ArrayList<>();
        intType.add(ExifInterface.TAG_RESOLUTION_UNIT);

        TIFF_VALUE_KEYS.put(Integer.class, intType);

        // String
        List<String> stringType = new ArrayList<>();
        stringType.add(ExifInterface.TAG_DATETIME);
        stringType.add(ExifInterface.TAG_MAKE);
        stringType.add(ExifInterface.TAG_MODEL);
        stringType.add(ExifInterface.TAG_SOFTWARE);

        TIFF_VALUE_KEYS.put(String.class, stringType);
    }

    private static final Map<Class, List<String>> GPS_VALUE_KEYS = new HashMap<>();
    static {
        // Double
        List<String> doubleType = new ArrayList<>();
        doubleType.add(ExifInterface.TAG_GPS_DEST_BEARING);
        doubleType.add(ExifInterface.TAG_GPS_DEST_DISTANCE);
        doubleType.add(ExifInterface.TAG_GPS_DEST_LATITUDE);
        doubleType.add(ExifInterface.TAG_GPS_DEST_LONGITUDE);
        doubleType.add(ExifInterface.TAG_GPS_DOP);
        doubleType.add(ExifInterface.TAG_GPS_IMG_DIRECTION);
        doubleType.add(ExifInterface.TAG_GPS_LATITUDE);
        doubleType.add(ExifInterface.TAG_GPS_LONGITUDE);
        doubleType.add(ExifInterface.TAG_GPS_SPEED);
        doubleType.add(ExifInterface.TAG_GPS_TRACK);
        doubleType.add(ExifInterface.TAG_GPS_ALTITUDE);

        GPS_VALUE_KEYS.put(Double.class, doubleType);

        // Integer
        List<String> intType = new ArrayList<>();
        intType.add(ExifInterface.TAG_GPS_ALTITUDE_REF);
        intType.add(ExifInterface.TAG_GPS_DIFFERENTIAL);

        GPS_VALUE_KEYS.put(Integer.class, intType);

        // String
        List<String> stringType = new ArrayList<>();
        stringType.add(ExifInterface.TAG_GPS_AREA_INFORMATION);
        stringType.add(ExifInterface.TAG_GPS_DATESTAMP);
        stringType.add(ExifInterface.TAG_GPS_DEST_BEARING_REF);
        stringType.add(ExifInterface.TAG_GPS_DEST_DISTANCE_REF);
        stringType.add(ExifInterface.TAG_GPS_DEST_LATITUDE_REF);
        stringType.add(ExifInterface.TAG_GPS_DEST_LONGITUDE_REF);
        stringType.add(ExifInterface.TAG_GPS_IMG_DIRECTION_REF);
        stringType.add(ExifInterface.TAG_GPS_LATITUDE_REF);
        stringType.add(ExifInterface.TAG_GPS_LONGITUDE_REF);
        stringType.add(ExifInterface.TAG_GPS_MAP_DATUM);
        stringType.add(ExifInterface.TAG_GPS_MEASURE_MODE);
        stringType.add(ExifInterface.TAG_GPS_PROCESSING_METHOD);
        stringType.add(ExifInterface.TAG_GPS_SATELLITES);
        stringType.add(ExifInterface.TAG_GPS_SPEED_REF);
        stringType.add(ExifInterface.TAG_GPS_STATUS);
        stringType.add(ExifInterface.TAG_GPS_TIMESTAMP);
        stringType.add(ExifInterface.TAG_GPS_TRACK_REF);
        stringType.add(ExifInterface.TAG_GPS_VERSION_ID);

        GPS_VALUE_KEYS.put(String.class, stringType);
    }

    private static final Gson GSON_OBJ = new Gson();

    private static final String EXIF_KEY = "EXIF";
    private static final String GPS_KEY = "GPS";
    private static final String TIFF_KEY = "TIFF";

    public static String getEXIFJsonString(final InputStream inputStream) throws IOException {
        ExifInterface originalExif = new ExifInterface(inputStream);

        Map<String, Object> resultMpa = new HashMap<>();
        resultMpa.put(EXIF_KEY, getMetaInfoMap(originalExif, EXIF_VALUE_KEYS));
        resultMpa.put(GPS_KEY, getMetaInfoMap(originalExif, GPS_VALUE_KEYS));
        resultMpa.put(TIFF_KEY, getMetaInfoMap(originalExif, TIFF_VALUE_KEYS));

        return GSON_OBJ.toJson(resultMpa);
    }

    private static Map<String, Object> getMetaInfoMap(ExifInterface originalExif, Map<Class, List<String>> inputMap) {
        Map<String, Object> outputMap = new HashMap<>();
        for (Map.Entry<Class, List<String>> keyClass : inputMap.entrySet()) {
            if (keyClass.getKey() == String.class) {
                for (String key : inputMap.get(keyClass.getKey())) {
                    try {
                        outputMap.put(key, originalExif.getAttribute(key));
                    } catch (Exception ignore) {}
                }
            } else if (keyClass.getKey() == Integer.class) {
                for (String key : inputMap.get(keyClass.getKey())) {
                    try {
                        outputMap.put(key, originalExif.getAttributeInt(key, 0));
                    } catch (Exception ignore) {}
                }
            } else if (keyClass.getKey() == Double.class) {
                for (String key : inputMap.get(keyClass.getKey())) {
                    try {
                        outputMap.put(key, originalExif.getAttributeDouble(key, 0.0));
                    } catch (Exception ignore) {}
                }
            }
        }
        return outputMap;
    }
}
