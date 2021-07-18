import * as React from 'react';
import { NativeModules, StyleSheet, requireNativeComponent, StyleProp, ViewProps, Platform, View, ViewStyle } from "react-native";

let SeekBar: any = null;
if (Platform.OS === "android") {
  SeekBar = requireNativeComponent("SeekBar");
} else if (Platform.OS === "ios") {
  SeekBar = requireNativeComponent("RNTSeekBar")
} else {
  SeekBar = ({ style, trackTintColor, thumbTintColor }: { style: ViewStyle, trackTintColor: string, thumbTintColor: string }) => (
    <React.Fragment>
      <View style={[styles.thumbBar, { backgroundColor: thumbTintColor }]} />
      <View style={[style, { backgroundColor: trackTintColor, height: 5, borderRadius: 2, opacity: 0.2 }]} />
    </React.Fragment>
  )
}


const { TrackPlayer } = NativeModules;


const ProgressBar = ({ style, thumbTintColor = "black", trackTintColor = "white" }: { style: StyleProp<ViewProps>, thumbTintColor: string, trackTintColor: string }) => {
  let timer: any = null;

  const [value, setValue] = React.useState(0);
  async function updateProgress() {
    const position = await TrackPlayer.getPosition();
    const progress = position / trackDuration * 100;
    setValue(progress);
  }

  let trackDuration: number = 0;

  React.useEffect(() => {
    TrackPlayer.setup();
    if (Platform.OS === "ios") {
      TrackPlayer.getDuration().then((duration: number) => {
        trackDuration = duration;
      })
      timer = setInterval(() => {
        updateProgress();
      }, 1000);
    }
    return () => {
      TrackPlayer.terminate();
      clearInterval(timer);
    }
  }, [])
  // @ts-ignore
  if (Platform.OS === "ios") {
    return <SeekBar value={value} maximumValue={100} style={style || styles.bar} thumbTintColor={thumbTintColor} trackTintColor={trackTintColor} />;
  }
  return <SeekBar style={style || styles.bar} thumbTintColor={thumbTintColor} trackTintColor={trackTintColor} />;
}


const styles = StyleSheet.create({
  bar: {
    height: "100%",
    width: "100%",
    margin: 10
  },
  thumbBar: { height: 16, width: 15, bottom: -10, opacity: 1, borderRadius: 8, alignSelf: 'flex-start' }
});

export default ProgressBar;
