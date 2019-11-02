
Pod::Spec.new do |s|
  s.name         = "RNImageModifier"
  s.version      = "1.0.0"
  s.summary      = "RNImageModifier"
  s.description  = <<-DESC
                  RNImageModifier
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "kellin.me@navercorp.com" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/naver/react-native-image-modifier", :tag => "master" }
  s.source_files  = "RNImageModifier/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  