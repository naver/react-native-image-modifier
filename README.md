# react-native-image-modifier

A React-native module it can modify an image by simply.

### npm registry
 - https://www.npmjs.com/package/react-native-image-modifier

### tested Environment
 - ios 9.0 or higher
 - android 8.0 or higher

### supported features.
 - resize
 - quality
 - grayscale
 - base64 encoding

### latest version
 - 0.1.3

## install

* React Native >= 0.60
```
yarn add react-native-image-modifier
cd ios && pod install
```

* React Native <= 0.59
```
yarn add react-native-image-modifier
react-native link react-native-image-modifier
```

* add to yarn package.json
```
"dependencies": {
    "react-native-image-modifier": "^0.1.3"
}
```

## usage
```javascript
import ImageModifier from 'react-native-image-modifier'
...
const param = {
        path: uri,
        grayscale: false, // or true
        base64: false, // or true
        resizeRatio: 0.8, // 1.0 is origin value
        imageQuality: 0.7 // 1.0 is max quality value
      }

const { success, errorMsg, imageURI, base64String } = await ImageModifier.modify(param)
```

## request param

#### path - required value
 - type : string
 - description : The absolute path of the local file. (URI)

#### grayscale - optional value (default value is false)
 - type : boolean
 - description : If you want to make to grayscale, set true.

#### base64 - optional value (default value is false)
 - type : boolean
 - description : If you want to get image data by base64 encoding, set true.

#### resizeRatio - optional value (default value is 1.0)
 - type : float
 - description : Image resize ratio, between 0.1 to 1.0.

#### imageQuality - optional value (default value is 1.0)
 - type : float
 - description : Image quality, between 0.1 to 1.0.

## response value

#### success
 - type : boolean
 - description : success(true) or failure(false).

#### errorMsg
 - type : string
 - description : the message of errors.

#### imageURI
 - type : string
 - description : The absolute path of the edited file. (URI)

#### base64String
 - type : string
 - description : base64 encoded text data.

## setting the module to the project.

### ios
 - In the Xcode, in the project navigator and right click `Libraries` -> Add Files to `your project name`
 - Go to `node_modules` -> `react-native-image-modifier` and add `RNImageModifier.xcodeproj`
 - In the Xcode, in the project navigator and select your project. Add `libRNImageModifier.a` to your project's `Build Phases` -> `Link Binary With Libraries`
 - Build & run your project

### android
  - Open `android/app/src/main/java/your project name/MainApplication.java`
  - Add `import com.naver.rn.RNImageModifierPackage;` to the imports line
  - Add `new RNImageModifierPackage()` to the list of the `getPackages()` method

  - Insert to the `android/settings.gradle`

  	```
  	include ':react-native-image-modifier'
  	project(':react-native-image-modifier').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-image-modifier/android')
  	```

 - Insert the line of dependencies block in `android/app/build.gradle`

  	```
   compile project(':react-native-image-modifier')
  	```

### license

```
Copyright (c) 2019-present NAVER Corp.

Permission is hereby granted, free of charge, to any person obtaining a copy 
of this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights 
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
copies of the Software, and to permit persons to whom the Software is 
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
SOFTWARE.
```
