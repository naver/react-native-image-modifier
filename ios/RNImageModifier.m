// react-native-image-modifier
// Copyright (c) 2019-present NAVER Corp.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy 
// of this software and associated documentation files (the "Software"), to deal 
// in the Software without restriction, including without limitation the rights 
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
// copies of the Software, and to permit persons to whom the Software is 
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all 
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
// SOFTWARE.

#import "RNImageModifier.h"

@implementation RNImageModifier

static NSString *const ERROR_MESSAGE_EMPTY_URI_KEY = @"URI Path KEY('path') must not be null.";
static NSString *const ERROR_MESSAGE_EMPTY_URI_VALUE = @"URI Path Value must not be null.";

static NSString *const SUCCESS_KEY = @"success";
static NSString *const ERROR_MESSAGE_KEY = @"errorMsg";
static NSString *const IMAGE_URI_KEY = @"imageURI";
static NSString *const BASE64_KEY = @"base64";
static NSString *const EXIF_KEY = @"exif";

static NSString *const PATH_KEY = @"path";
static NSString *const GRAYSCALE_KEY = @"grayscale";
static NSString *const RESIZE_RATIO_KEY = @"resizeRatio";
static NSString *const IMAGE_QUALITY_KEY = @"imageQuality";
static NSString *const BASE64_STRING_KEY = @"base64String";
static NSString *const EXTRACT_EXIF_KEY = @"extractEXIF";

static NSString *const SAVE_IMAGE_FILE_NAME_BY_OVERWRITE = @"modifiedImage.jpg";

static int const BITS_PER_COMPONENT = 8;
static int const BYTES_PER_ROW = 4;

static CGFloat const BASE64_IMAGE_QUALITY = 1.0f;

RCT_EXPORT_MODULE()

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(imageModifier:(NSDictionary *) params
                  callback:(RCTResponseSenderBlock)callback) {
    
    if ([params objectForKey:PATH_KEY] == nil) {
        callback(@[ERROR_MESSAGE_EMPTY_URI_KEY, @""]);
        return;
    }
    
    NSString *const uri = params[PATH_KEY];
    if ([uri length] == 0) {
        callback(@[ERROR_MESSAGE_EMPTY_URI_VALUE, @""]);
        return;
    }
    
    CGFloat resizeRatio = 1.0f;
    if ([params objectForKey:RESIZE_RATIO_KEY]) {
        NSString *resizeRatioString = params[RESIZE_RATIO_KEY];
        if ([resizeRatioString length] > 0) {
            resizeRatio = [resizeRatioString floatValue];
        }
    }
    
    CGFloat imageQuality = 1.0f;
    if ([params objectForKey:IMAGE_QUALITY_KEY]) {
        NSString *imageQualityString = params[IMAGE_QUALITY_KEY];
        if ([imageQualityString length] > 0) {
            imageQuality = [imageQualityString floatValue];
        }
    }
    
    BOOL imageToGrayscale = false;
    if ([params objectForKey:GRAYSCALE_KEY]) {
        NSString *imageToGrayscaleString = params[GRAYSCALE_KEY];
        imageToGrayscale = [imageToGrayscaleString boolValue];
    }
    
    NSString *const encodedURI = [uri stringByRemovingPercentEncoding];
    
    UIImage *const originImage = [self getNSImageByURI: encodedURI];
    UIImage *const resizedImage = [self modifyImage:originImage resizeRatio:resizeRatio];
    
    UIImage *grayscaleImage = nil;
    if (imageToGrayscale) {
        grayscaleImage = [self convertToGray:resizedImage];
    }
    
    NSMutableDictionary *response = [[NSMutableDictionary alloc] init];
    if ([params objectForKey:BASE64_KEY] && [params[BASE64_KEY] boolValue]) {
        [response setObject:@YES forKey:SUCCESS_KEY];
        [response setObject:[self getBase64FromImage:(grayscaleImage != nil ? grayscaleImage : resizedImage)] forKey:BASE64_STRING_KEY];
    } else {
        [response setObject:@YES forKey:SUCCESS_KEY];
        [response setObject:[self saveImageToLocal:(grayscaleImage != nil ? grayscaleImage : resizedImage) fileName:SAVE_IMAGE_FILE_NAME_BY_OVERWRITE imageQuality:imageQuality] forKey:IMAGE_URI_KEY];
    }
    
    if ([params objectForKey:EXTRACT_EXIF_KEY]) {
        [response setObject:[self getJsonString:[self getMetaDataFromImage:encodedURI]] forKey:EXIF_KEY];
    }
    
    callback(@[[NSNull null], response]);
}

- (UIImage *) getNSImageByURI:(NSString *) uri {
    NSURL *const url = [NSURL fileURLWithPath: uri];
    NSData *const data = [NSData dataWithContentsOfURL: url];
    return [UIImage imageWithData: data];
}

- (NSString *) saveImageToLocal:(UIImage *) image fileName:(NSString *) name imageQuality:(CGFloat) quality {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths firstObject];

    NSData *data = UIImageJPEGRepresentation(image, quality);
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *fullPath = [documentsDirectory stringByAppendingPathComponent:name];

    [fileManager createFileAtPath:fullPath contents:data attributes:nil];
    return fullPath;
}

- (UIImage *) modifyImage:(UIImage *) originImage resizeRatio:(CGFloat) sizeRatio {
    const int resizeWidth = originImage.size.width * sizeRatio;
    const int resizeHeight = originImage.size.height * sizeRatio;

    uint32_t *rgbImage = (uint32_t *) malloc(resizeWidth * resizeHeight * sizeof(uint32_t));
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(rgbImage, resizeWidth, resizeHeight, BITS_PER_COMPONENT, (resizeWidth * BYTES_PER_ROW), colorSpace, kCGBitmapByteOrder32Little | kCGImageAlphaNoneSkipLast);
    
    CGContextSetInterpolationQuality(context, kCGInterpolationMedium);
    CGContextSetShouldSmoothFonts (context, NO);
    CGContextSetAllowsFontSmoothing (context, NO);
    CGContextSetShouldAntialias(context, NO);
    
    CGContextDrawImage(context, CGRectMake(0, 0, resizeWidth, resizeHeight), [originImage CGImage]);
    
    CGImageRef image = CGBitmapContextCreateImage(context);
    
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    UIImage *resultUIImage = [UIImage imageWithCGImage:image];
    
    CGImageRelease(image);

    return resultUIImage;
}

- (NSString *) getBase64FromImage:(UIImage *) originImage {
    NSData *imageData = UIImageJPEGRepresentation(originImage, BASE64_IMAGE_QUALITY);
    return [imageData base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
}

- (UIImage *) convertToGray:(UIImage *) originImage {
    int const imageWith = originImage.size.width;
    int const imageHeight = originImage.size.height;
    
    CGColorSpaceRef colorSpaceGray = CGColorSpaceCreateDeviceGray();
    CGContextRef context = CGBitmapContextCreate(nil, imageWith, imageHeight, BITS_PER_COMPONENT, (imageWith * BYTES_PER_ROW), colorSpaceGray, kCGImageAlphaNoneSkipLast);

    CGContextDrawImage(context, CGRectMake(0, 0, imageWith, imageHeight), [originImage CGImage]);

    CGImageRef newImageReference = CGBitmapContextCreateImage(context);
    UIImage *const newImage = [UIImage imageWithCGImage:newImageReference];

    CGColorSpaceRelease(colorSpaceGray);
    CGContextRelease(context);
    CFRelease(newImageReference);

    return newImage;
}

- (NSDictionary *) getMetaDataFromImage:(NSString *) uri {
    NSURL *const url = [NSURL fileURLWithPath: uri];
    NSData *const data = [NSData dataWithContentsOfURL: url];
    
    CGImageSourceRef sourceRef = CGImageSourceCreateWithData((CFDataRef)data, NULL);

    CFDictionaryRef imagePropertiesDictionary = CGImageSourceCopyPropertiesAtIndex(sourceRef,0, NULL);

    CFDictionaryRef exifEXIF = (CFDictionaryRef)CFDictionaryGetValue(imagePropertiesDictionary, kCGImagePropertyExifDictionary);
    CFDictionaryRef exifGPS = (CFDictionaryRef)CFDictionaryGetValue(imagePropertiesDictionary, kCGImagePropertyGPSDictionary);
    CFDictionaryRef exifTIFF = (CFDictionaryRef)CFDictionaryGetValue(imagePropertiesDictionary, kCGImagePropertyTIFFDictionary);
    
    NSMutableDictionary *metaDataDic = [[NSMutableDictionary alloc] init];
    
    if(exifEXIF != nil) {
        NSDictionary *tmpEXIF = (__bridge NSDictionary*)exifEXIF;
        [metaDataDic setObject:tmpEXIF forKey:@"EXIF"];
    }
    if(exifGPS != nil) {
        NSDictionary *tmpGPS = (__bridge NSDictionary*)exifGPS;
        [metaDataDic setObject:tmpGPS forKey:@"GPS"];
    }
    if(exifTIFF != nil) {
        NSDictionary *tmpTIFF = (__bridge NSDictionary*)exifTIFF;
        [metaDataDic setObject:tmpTIFF forKey:@"TIFF"];
    }
    
    return metaDataDic;
}

- (NSString *) getJsonString:(NSDictionary *) dicData {
    NSData* jsonData = [NSJSONSerialization dataWithJSONObject:dicData options:NSJSONWritingPrettyPrinted error:nil];
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

@end
  
