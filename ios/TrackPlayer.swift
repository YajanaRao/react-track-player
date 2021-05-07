import AVFoundation
import Foundation
import MediaPlayer


enum TrackPlayerError: Error {
    case invalidTrack(String)
}

@objc(TrackPlayer)
class TrackPlayer: RCTEventEmitter {

   var player:AVAudioPlayer?


  func setupMediaPlayerNotificationView(title: String){
    print("setupMediaPlayerNotificationView: ", title);
    
    let commandCenter = MPRemoteCommandCenter.shared();

    // add handler for play command
    commandCenter.playCommand.addTarget { [unowned self] event in
      self.play();
      return .success;
    }

    commandCenter.pauseCommand.addTarget { [unowned self] event in
      self.pause();
      return .success; 
    }
 
    var nowPlayingInfo = [String : Any] ();
    nowPlayingInfo[MPMediaItemPropertyTitle] = title;
    nowPlayingInfo[MPNowPlayingInfoPropertyElapsedPlaybackTime] = 100
    nowPlayingInfo[MPMediaItemPropertyPlaybackDuration] = 100
    nowPlayingInfo[MPNowPlayingInfoPropertyPlaybackRate] = 1
 
    // Set the metadata
    print("now playing info: ", nowPlayingInfo);
    
    MPNowPlayingInfoCenter.default().nowPlayingInfo = nowPlayingInfo
  }

  @objc func handleInterruption(notification: Notification) {
        guard let userInfo = notification.userInfo,
            let typeValue = userInfo[AVAudioSessionInterruptionTypeKey] as? UInt,
            let type = AVAudioSession.InterruptionType(rawValue: typeValue) else {
                return
        }
        if type == .began {
            print("audio began");
            
            // Interruption began, take appropriate actions (save state, update user interface)
            sendEvent(withName: "media", body:  "paused");
           
        }
        else if type == .ended {
            print("audio ended")
            guard let optionsValue =
                userInfo[AVAudioSessionInterruptionOptionKey] as? UInt else {
                    return
            }
            let options = AVAudioSession.InterruptionOptions(rawValue: optionsValue)
            if options.contains(.shouldResume) {
                // Interruption Ended - playback should resume
                sendEvent(withName: "media", body:  "play");
               
            } else {
                // Interruption Ended - playback should NOT resume
                sendEvent(withName: "media", body:  "stoped");
            }
        }
    }


  @objc(setup)
  public func setup() {
    print("setup");
    let notificationCenter = NotificationCenter.default
        notificationCenter.removeObserver(self)
        notificationCenter.addObserver(self,
                                       selector: #selector(handleInterruption),
                                       name: AVAudioSession.interruptionNotification,
                                       object: nil)

    let sessionCategory: AVAudioSession.Category = .playback
    let sessionCategoryOptions: AVAudioSession.CategoryOptions = []
    let sessionCategoryMode: AVAudioSession.Mode = .default


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
            sendEvent(withName: "media", body:  "loading");
            let fileURL = URL(string:urlProp)
            let soundData = try Data(contentsOf:fileURL!)
            let title: String! = track["title"] as? String;
            setupMediaPlayerNotificationView(title: title);
            player = try AVAudioPlayer(data: soundData);
            player?.prepareToPlay();
            player?.pause()
            sendEvent(withName: "media", body:  "paused"); 
            resolve(NSNull())
        } else {
            throw TrackPlayerError.invalidTrack("Track Url is not valid");
            sendEvent(withName: "media", body:  "paused"); 
        }

        
    } catch {
        print("the error is : ", error)
        reject("Error", "Faied to load url", error)
        sendEvent(withName: "media", body:  "paused"); 
    }
  }
  
  @objc(pause)
  public func pause() {
    print("paused");
    player?.pause()
    sendEvent(withName: "media", body:  "paused");
  }
  

  @objc(play)
  public func play() {
    print("play");
    try? AVAudioSession.sharedInstance().setActive(true)
    player?.play()
    sendEvent(withName: "media", body:  "playing");
  }

  @objc(destroy)
  public func destroy() {
      print("Destroying player")
  }
    
  @objc(terminate)
  public func terminate() {
      print("terminate player")
  }
  
  @objc(getPosition:rejecter:)
  public func getPosition(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
      print("player progress: ", player?.currentTime as Any)
      resolve(player?.currentTime)
  }

   @objc(getDuration:rejecter:)
   public func getDuration(resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
       resolve(player?.duration)
   }
    
  override func supportedEvents() -> [String]! {
      return [
        "media"
      ]
    }
  
}
