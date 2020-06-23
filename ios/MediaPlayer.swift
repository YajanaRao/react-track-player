import AVFoundation

@objc(MediaPlayer)
public class MediaPlayer: NSObject {

    var avPlayer:AVAudioPlayer!
  @objc(load:resolver:rejecter:)
  public func load(to url: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    do {
        let fileURL = URL(string:url)
        let soundData = try Data(contentsOf:fileURL!)
        avPlayer = try AVAudioPlayer(data: soundData)
        resolve(NSNull())
    } catch {
        reject("Failed", "Something went wrong", error)
    }
  }
  
  @objc(pause)
  public func pause() {
    avPlayer.pause()
  }

  @objc(play)
  public func play() {
    avPlayer.play()
  }
  
}
