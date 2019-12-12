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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageModifierUtil {

    private static final String URI_DATA = "data";
    private static final String URI_CONTENT = "content";

    private static final List<String> URI_FILE_CONTENT = Collections.unmodifiableList(Arrays.asList("file", "content"));

    public static Bitmap getSourceImageByPath(final Context context, final Uri imageURI) throws Exception {
        if (imageURI == null) {
            throw new Exception("imageURI must not be null.");
        }

        final String imageURIScheme = imageURI.getScheme();

        Bitmap sourceImage = null;
        if (StringUtils.isStringValid(imageURIScheme) == false || URI_FILE_CONTENT.contains(imageURIScheme.toLowerCase())) {
            sourceImage = loadBitmapImage(context, imageURI);
        } else if (imageURIScheme.equalsIgnoreCase(URI_DATA)) {
            sourceImage = loadBitmapImageByBase64(imageURI);
        }

        if (sourceImage == null) {
            throw new Exception("image can't be loaded by URI.");
        }

        return sourceImage;
    }

    private static Bitmap loadBitmapImage(final Context context, final Uri imageURI) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds = true;
        if (imageURI.getScheme() == null || imageURI.getScheme().equalsIgnoreCase(URI_CONTENT) == false) {
            try {
                return BitmapFactory.decodeFile(imageURI.getEncodedPath(), options);
            } catch (Exception ex) {
                throw ex;
            }
        } else {
            try(InputStream input = context.getContentResolver().openInputStream(imageURI)) {
                return BitmapFactory.decodeStream(input, null, options);
            } catch (Exception ex) {
                throw new Exception("An error occurred while working on Bitmap processing by URI.");
            }
        }
    }

    private static List<String> LIST_OF_IMAGE_TYPE = Collections.unmodifiableList(Arrays.asList("image/jpg", "image/jpeg", "image/png"));

    private static Bitmap loadBitmapImageByBase64(final Uri imageURI) throws Exception {
        final String imagePath = imageURI.getSchemeSpecificPart();
        final int splitLocation = imagePath.indexOf(',');
        if (splitLocation != -1) {
            final String imageType = imagePath.substring(0, splitLocation).replace('\\', '/').toLowerCase();
            if (LIST_OF_IMAGE_TYPE.contains(imageType)) {
                final String base64EncodedImageString = imagePath.substring(splitLocation + 1);
                final byte[] decodedStringArray = Base64.decode(base64EncodedImageString, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedStringArray, 0, decodedStringArray.length);
            }
        }

        throw new Exception("An error occurred while working on Bitmap base64 processing by URI.");
    }

    public static Bitmap getImageByResize(final Bitmap image, final float resizeRatio, final boolean reuseInputImage) throws Exception {
        if (image == null) {
            throw new Exception("image must not be null.");
        }
        try {
            final int width = (int)(image.getWidth() * resizeRatio);
            final int height = (int)(image.getHeight() * resizeRatio);
            Bitmap resultImage = Bitmap.createScaledBitmap(image, width, height, true);
            if (reuseInputImage == false) {
                image.recycle();
            }
            return resultImage;
        } catch (OutOfMemoryError ex) {
            throw ex;
        }
    }

    public static void saveImageFile(final Bitmap image, final File savePath,
                                     final Bitmap.CompressFormat compressFormat, final float imageQuality) throws Exception {
        if (image == null) {
            throw new Exception("image must not be null.");
        }

        if (savePath.createNewFile() == false) {
            throw new IOException("image file already exists.");
        }

        byte[] imageDataArray;
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            image.compress(compressFormat, (int)(imageQuality * 100), outputStream);

            imageDataArray = outputStream.toByteArray();
            outputStream.flush();
        } catch (Exception ex) {
            throw new Exception("An error occurred during image compression.");
        }

        if (imageDataArray == null || imageDataArray.length == 0) {
            throw new Exception("The image data array problem has occurred.");
        }

        FileOutputStream fos = new FileOutputStream(savePath);
        fos.write(imageDataArray);
        fos.flush();
        fos.close();

        image.recycle();
    }

    private static final float[] GRAYSCALE_MATRIX = new float[]{0.3f, 0.59f, 0.11f, 0, 0, 0.3f, 0.59f, 0.11f, 0, 0, 0.3f, 0.59f, 0.11f, 0, 0, 0, 0, 0, 1, 0,};

    public static Bitmap imageToGrayscale(Bitmap originSourceImage) {
        Bitmap convertedImage = Bitmap.createBitmap(originSourceImage.getWidth(), originSourceImage.getHeight(), originSourceImage.getConfig());

        Canvas canvas = new Canvas(convertedImage);
        Paint paint = new Paint();

        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(GRAYSCALE_MATRIX);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(originSourceImage, 0, 0, paint);

        originSourceImage.recycle();

        return convertedImage;
    }

    public static String getBase64FromBitmap(Bitmap bitmap, final Bitmap.CompressFormat compressFormat) throws Exception {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            bitmap.compress(compressFormat, 100, byteArrayOutputStream);
            final String base64Encoded = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            bitmap.recycle();
            return base64Encoded;
        } catch (Exception ex) {
            throw new Exception("There was a problem while image to base64 converting.");
        }
    }

    public static InputStream getInputStreamFromBitmap(Bitmap bitmap, final Bitmap.CompressFormat compressFormat) throws Exception {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            bitmap.compress(compressFormat, 100, byteArrayOutputStream);
            byte[] bitmapArray = byteArrayOutputStream.toByteArray();
            return new ByteArrayInputStream(bitmapArray);
        } catch (Exception ex) {
            throw new Exception("There was a problem while image to InputStream converting.");
        }
    }
}
