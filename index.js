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

import { NativeModules, Platform } from 'react-native'

var { RNImageModifier } = NativeModules;

class ImageModifier {
    static modify(param) {
        ImageModifier.paramValidate(param)

        if (Platform.OS === "ios") {
            return new Promise((resolve) => {
              RNImageModifier.imageModifier(param, (err, response) => {
                if (err) {
                  return {
                    success: false,
                    errorMsg: err
                  }
                }
                resolve(response);
              });
            }).catch(function (err) {
                  return {
                      success: false,
                      errorMsg: "check your ios native module setting. ("+err+")"
                  }
            });
        } else if (Platform.OS === "android") {
            return new Promise((resolve) => {
                RNImageModifier.imageModifier(param, resolve);
            }).catch(function (err) {
                    return {
                        success: false,
                        errorMsg: "check your android native module setting. ("+err+")"
                    }
            });
        } else {
            return {
                success: false,
                errorMsg: "not yet supported.("+Platform.OS+")"
            }
        }
    }

    static paramValidate(param) {
        if (param.hasOwnProperty('grayscale')) {
            param.grayscale = param.grayscale.toString().toLowerCase();
        } else {
            param.grayscale = "false";
        }
        if (param.hasOwnProperty('base64')) {
            param.base64 = param.base64.toString().toLowerCase();
        } else {
            param.base64 = "false";
        }
        if (param.hasOwnProperty('resizeRatio')) {
            param.resizeRatio = ImageModifier.checkToInputValue(param.resizeRatio);
        } else {
            param.resizeRatio = "1.0";
        }
        if (param.hasOwnProperty('imageQuality')) {
            param.imageQuality = ImageModifier.checkToInputValue(param.imageQuality);
        } else {
            param.imageQuality = "1.0";
        }
    }

    static checkToInputValue(inputValue) {
        try {
            var checkDataToString = inputValue.toString();
            var checkData = parseFloat(checkDataToString);
            if (checkData <= 0 || checkData > 1.0) {
                return "1.0";
            } else {
                return checkDataToString;
            }
        } catch(error) {
            return "1.0";
        }
    }
}

export default ImageModifier;
