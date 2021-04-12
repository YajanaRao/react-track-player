#import "TrackPlayerBridge.h"
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(TrackPlayer, NSObject)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

RCT_EXTERN_METHOD(load:(NSDictionary *)track
            resolver:(RCTPromiseResolveBlock)resolve
            rejecter:(RCTPromiseRejectBlock)reject);


RCT_EXTERN_METHOD(play);
        
RCT_EXTERN_METHOD(pause);

RCT_EXTERN_METHOD(setup);

RCT_EXTERN_METHOD(destroy);

@end

