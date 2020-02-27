require 'json'

package = JSON.parse(File.read(File.join('..', 'package.json')))

Pod::Spec.new do |s|
  s.name         = "RNImageModifier"
  s.version      = package['version']
  s.summary      = "RNImageModifier"
  s.description  = package['description']
  s.homepage     = package['homepage']
  s.license      = package['license']
  s.author       = package['author']
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/naver/react-native-image-modifier.git", :tag => "master" }
  s.source_files  = "RNImageModifier/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
end

  
