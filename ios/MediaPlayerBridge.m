
#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(MediaPlayer, NSObject)

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

RCT_EXTERN_METHOD(load:(NSString *)url
            resolver:(RCTPromiseResolveBlock)resolve
            rejecter:(RCTPromiseRejectBlock)reject);


RCT_EXTERN_METHOD(play);
        
RCT_EXTERN_METHOD(pause);

@end

