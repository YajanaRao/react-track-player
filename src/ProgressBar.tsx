import * as React from 'react';
import { NativeModules, StyleSheet, requireNativeComponent, StyleProp, ViewProps } from "react-native";


const SeekBar = requireNativeComponent("SeekBar");
const { TrackPlayer } = NativeModules;


const ProgressBar = ({ style, thumbTintColor, trackTintColor }: { style: StyleProp<ViewProps>, thumbTintColor: string, trackTintColor: string }) => {
  React.useEffect(() => {
    TrackPlayer.init();
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
