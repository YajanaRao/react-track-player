@objc(MediaPlayer)
public class MediaPlayer: NSObject {

  @objc(load:resolver:rejecter:)
  public func load(to url: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    // Date is ready to use!
    resolve(NSNull());
  }

}