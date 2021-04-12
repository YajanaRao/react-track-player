import AVFoundation
import Foundation
import MediaPlayer

enum TrackPlayerError: Error {
    case invalidTrack(String)
}

@objc(TrackPlayer)
class TrackPlayer: RCTEventEmitter {

  var player:AVAudioPlayer!


  @objc(setup)
  public func setup() {
    var sessionCategory: AVAudioSession.Category = .playback
    var sessionCategoryOptions: AVAudioSession.CategoryOptions = []
    var sessionCategoryMode: AVAudioSession.Mode = .default

    // Progressively opt into AVAudioSession policies for background audio
    // and AirPlay 2.
   if #available(iOS 13.0, *) {
       try? AVAudioSession.sharedInstance().setCategory(sessionCategory, mode: sessionCategoryMode, policy: .longFormAudio, options: sessionCategoryOptions)
   } else if #available(iOS 11.0, *) {
       try? AVAudioSession.sharedInstance().setCategory(sessionCategory, mode: sessionCategoryMode, policy: .longForm, options: sessionCategoryOptions)
   }
  }

  @objc(load:resolver:rejecter:)
  public func load(track: [String: Any], resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    do {
        print("track: ", track)
        if let urlProp: String = track["path"] as? String {
            let fileURL = URL(string:urlProp)
            let soundData = try Data(contentsOf:fileURL!)
            player = try AVAudioPlayer(data: soundData)
            player.pause()
            sendEvent(withName: "media", body:  "paused");
            resolve(NSNull())
        } else {
            throw TrackPlayerError.invalidTrack("Track Url is not valid");
        }

        
    } catch {
        print("the error is : ", error)
        reject("Failed", "Something went wrong", error)
    }
  }
  
  @objc(pause)
  public func pause() {
    player.pause()
    sendEvent(withName: "media", body:  "paused");
  }
  

  @objc(play)
  public func play() {
    try? AVAudioSession.sharedInstance().setActive(true)
    player.play()
    sendEvent(withName: "media", body:  "playing");
  }

  @objc(destroy)
  public func destroy() {
      print("Destroying player")
  }
    

  override func supportedEvents() -> [String]! {
      return [
        "media"
      ]
    }
  
}
