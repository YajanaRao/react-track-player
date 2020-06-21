@objc(MediaPlayerManager)
class MediaPlayerManager: NSObject {

  @objc(load:resolver:rejecter:)
  func load(url: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    // Date is ready to use!
    resolve(NSNull());
  }

}