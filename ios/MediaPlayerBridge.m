
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(MediaPlayer, NSObject)

RCT_EXTERN_METHOD(load:(NSString *)url
            resolver:(RCTPromiseResolveBlock)resolve
            rejecter:(RCTPromiseRejectBlock)reject);


RCT_EXTERN_METHOD(play);
        
RCT_EXTERN_METHOD(pause);

RCT_EXTERN_METHOD(init);

@end