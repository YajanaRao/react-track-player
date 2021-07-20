#import "TrackPlayerBridge.h"
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

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

RCT_EXTERN_METHOD(getPosition:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject);

RCT_EXTERN_METHOD(getDuration:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject);


@end

// ProgressBar

#import <UIKit/UIKit.h>

#import <React/RCTViewManager.h>

@interface RNTSeekBarManager : RCTViewManager
@end

@implementation RNTSeekBarManager

RCT_EXPORT_VIEW_PROPERTY(value, float);

RCT_EXPORT_VIEW_PROPERTY(maximumValue, float);

RCT_EXPORT_MODULE(RNTSeekBar)

- (UIView *)view
{
  UISlider *seekBar = [[UISlider alloc] init];
//  seekBar.value = value;
    seekBar.minimumValue = 0;
    seekBar.maximumValue = 100;
  return seekBar;
}

@end
