@objc(MediaPlayer)
public class MediaPlayer: NSObject {

  @objc(load:resolver:rejecter:)
  public func load(to url: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    // Date is ready to use!
    resolve(NSNull());
  }
  
  @objc(pause)
  public func pause() {
    print("pause")
  }

  @objc(play)
  public func play() {
    print("play")
  }

  @objc(init)
  public func init() {
    print("init")
  }
  
}