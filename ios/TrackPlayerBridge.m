#import "TrackPlayerBridge.h"
#import <React/RCTBridgeModule.h>

// RNTMapManager.m
#import <UIKit/UIKit.h>

#import <React/RCTViewManager.h>

@interface RNTSeekBarManager : RCTViewManager
@end

@implementation RNTSeekBarManager

RCT_EXPORT_MODULE(RNTSeekBar)

- (UIView *)view
{
  UISlider *seekBar = [[UISlider alloc] init];
  seekBar.value = 0.9;
  return seekBar;
}

@end

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

RCT_EXTERN_METHOD(terminate);

@end

