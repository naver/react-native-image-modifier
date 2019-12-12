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

package com.naver.rn;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.naver.utils.EXIFUtils;
import com.naver.utils.ImageModifierUtil;
import com.naver.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class RNImageModifierModule extends ReactContextBaseJavaModule {

  private final Context reactContext;

  private static final String SUCCESS_KEY = "success";
  private static final String ERROR_MESSAGE_KEY = "errorMsg";
  private static final String IMAGE_URI_KEY = "imageURI";
  private static final String BASE64_STRING_KEY = "base64String";
  private static final String EXIF_KEY = "exif";

  private static final String ERROR_MESSAGE_EMPTY_URI_KEY = "URI Path KEY('path') must not be null.";
  private static final String ERROR_MESSAGE_EMPTY_URI_VALUE = "URI Path Value must not be null.";
  private static final String ERROR_MESSAGE_FILE_SAVE_FAILED = "File save failed.";

  private static final String PATH_KEY = "path";
  private static final String GRAYSCALE_KEY = "grayscale";
  private static final String BASE64_KEY = "base64";
  private static final String RESIZE_RATIO_KEY = "resizeRatio";
  private static final String IMAGE_QUALITY_KEY = "imageQuality";
  private static final String EXTRACT_EXIF_KEY = "extractEXIF";

  private static final String ANDROID_URI_FILE_SCHEME = "file://";

  private static Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.valueOf("JPEG");

  public RNImageModifierModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNImageModifier";
  }

  @ReactMethod
  public void imageModifier(ReadableMap data, final Callback responseCb) {
    final String errorMessage = this.requiredDataValidate(data);
    if (StringUtils.isStringValid(errorMessage)) {
      responseCb.invoke(this.getReturnMessage(false, errorMessage));
      return;
    }

    Uri imageURI = Uri.parse(data.getString(PATH_KEY));

    try {
      Bitmap sourceImage = ImageModifierUtil.getSourceImageByPath(this.reactContext, imageURI);

      Bitmap resizeImage = null;
      if (data.hasKey(RESIZE_RATIO_KEY) == true) {
        final float resizeRatio = Float.parseFloat(data.getString(RESIZE_RATIO_KEY));
        if (resizeRatio > 0.0 && resizeRatio < 1.0) {
          resizeImage = ImageModifierUtil.getImageByResize(sourceImage, resizeRatio, false);
        }
      }

      Bitmap grayscaleImage = null;
      if (data.hasKey(GRAYSCALE_KEY) && Boolean.parseBoolean(data.getString(GRAYSCALE_KEY))) {
        grayscaleImage = ImageModifierUtil.imageToGrayscale(resizeImage != null ? resizeImage : sourceImage);
      }

      float imageQuality = 1.0f;
      if (data.hasKey(IMAGE_QUALITY_KEY) == true) {
        try {
          imageQuality = Float.parseFloat(data.getString(IMAGE_QUALITY_KEY));
        } catch (NumberFormatException ignore) {}
      }

      Bitmap targetImage = this.getTargetBitmap(grayscaleImage, resizeImage, sourceImage);
      sourceImage.recycle();

      try {
        WritableMap response = this.getReturnMessage(true);
        if (data.hasKey(BASE64_KEY) && Boolean.parseBoolean(data.getString(BASE64_KEY))) {
          response.putString(BASE64_STRING_KEY, ImageModifierUtil.getBase64FromBitmap(targetImage, COMPRESS_FORMAT));
        } else {
          response.putString(IMAGE_URI_KEY, this.saveToLocalStorage(targetImage, imageQuality));
        }

        targetImage.recycle();

        if (data.hasKey(EXTRACT_EXIF_KEY)) {
          try(InputStream input = this.reactContext.getContentResolver().openInputStream(imageURI)) {
            final String exifJsonString = EXIFUtils.getEXIFJsonString(input);
            response.putString(EXIF_KEY, exifJsonString);
          } catch (Exception ex) {
            responseCb.invoke(this.getReturnMessage(false, ex.toString()));
          }
        }

        responseCb.invoke(response);
      } catch (Exception ex) {
        responseCb.invoke(this.getReturnMessage(false, ex.toString()));
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      responseCb.invoke(this.getReturnMessage(false, ex.toString()));
    }
  }

  private Bitmap getTargetBitmap(Bitmap grayscaleImage, Bitmap resizeImage, Bitmap sourceImage) {
    Bitmap targetImage;
    if (grayscaleImage != null) {
      targetImage = Bitmap.createBitmap(grayscaleImage);
      grayscaleImage.recycle();

      if (resizeImage != null) {
        resizeImage.recycle();
      }
    } else if (resizeImage != null) {
      targetImage = Bitmap.createBitmap(resizeImage);
      resizeImage.recycle();
    } else {
      targetImage = Bitmap.createBitmap(sourceImage);
    }
    return targetImage;
  }

  private String requiredDataValidate(ReadableMap data) {
    if (data.hasKey(PATH_KEY) == false) {
      return ERROR_MESSAGE_EMPTY_URI_KEY;
    } else if (StringUtils.isStringValid(data.getString(PATH_KEY)) == false) {
      return ERROR_MESSAGE_EMPTY_URI_VALUE;
    }
    return null;
  }

  private String saveToLocalStorage(Bitmap targetImage, final float imageQuality) throws Exception {
    try {
      final String fileName = Long.toString(new Date().getTime()).concat(".").concat(COMPRESS_FORMAT.name());
      File saveTargetFile = new File(this.reactContext.getCacheDir(), fileName);

      ImageModifierUtil.saveImageFile(targetImage, saveTargetFile, COMPRESS_FORMAT, imageQuality);
      if (saveTargetFile.exists() && saveTargetFile.canRead()) {
        return ANDROID_URI_FILE_SCHEME.concat(saveTargetFile.getAbsolutePath());
      }
    } catch (Exception ex) {
      throw ex;
    }

    throw new Exception(ERROR_MESSAGE_FILE_SAVE_FAILED);
  }

  private WritableMap getReturnMessage(final boolean isSuccess, final String errorMessage) {
    WritableMap response = this.getReturnMessage(isSuccess);
    if (StringUtils.isStringValid(errorMessage)) {
      response.putString(ERROR_MESSAGE_KEY, errorMessage);
    }

    return response;
  }

  private WritableMap getReturnMessage(final boolean isSuccess) {
    WritableMap response = Arguments.createMap();
    response.putBoolean(SUCCESS_KEY, isSuccess);

    return response;
  }
}