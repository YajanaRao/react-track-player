require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-media-player"
  s.version      = package["version"]
  s.summary      = package["summary"]
  s.description  = package["description"]
  s.homepage     = "https://github.com/YajanaRao/react-native-media-player"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Yajana N Rao" => "yajananrao@gmail.com" }
  s.platforms    = { :ios => "8.0" }
  # s.source       = {}
  s.source       = { :git => "https://github.com/YajanaRao/react-native-media-player.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  # ...
  # s.dependency "..."
end

