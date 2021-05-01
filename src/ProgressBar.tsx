import * as React from 'react';
import { NativeModules, StyleSheet, requireNativeComponent, StyleProp, ViewProps, Platform, View } from "react-native";

let SeekBar = null;
if (Platform.OS === "android") {
  SeekBar = requireNativeComponent("SeekBar");
} else if (Platform.OS === "ios") {
  SeekBar = requireNativeComponent('RNTSeekBar');
} else {
  SeekBar = ({ style, trackTintColor, thumbTintColor }) => (
    <React.Fragment>
      <View style={{ backgroundColor: thumbTintColor, height: 16, width: 15, bottom: -10, opacity: 1, borderRadius: 8, alignSelf: 'flex-start' }} />
      <View style={[style, { backgroundColor: trackTintColor, height: 5, borderRadius: 2, opacity: 0.2 }]} />
    </React.Fragment>
  )
}
const { TrackPlayer } = NativeModules;


const ProgressBar = ({ style, thumbTintColor = "black", trackTintColor = "white" }: { style: StyleProp<ViewProps>, thumbTintColor: string, trackTintColor: string }) => {
  React.useEffect(() => {
    TrackPlayer.setup();
    return () => {
      TrackPlayer.terminate();
    }
  }, [])
  // @ts-ignore
  return <SeekBar style={style || styles.bar} thumbTintColor={thumbTintColor} trackTintColor={trackTintColor} />;
}


const styles = StyleSheet.create({
  bar: {
    height: "100%",
    width: "100%",
    margin: 10
  }
});

export default ProgressBar;
