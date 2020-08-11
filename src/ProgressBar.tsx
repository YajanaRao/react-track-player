import React, { useEffect } from "react";
import { NativeModules, StyleSheet } from "react-native";
import { requireNativeComponent, StyleProp, ViewProps } from "react-native";
const SeekBar = requireNativeComponent("SeekBar");
const TrackPlayer = NativeModules.TrackPlayer;

const ProgressBar = ({ style }: { style: StyleProp<ViewProps> }) => {
  useEffect(() => {
    TrackPlayer.init();
    return () => {
      TrackPlayer.terminate();
    }
  }, [])
  // @ts-ignore
  return <SeekBar style={style ? style : styles.bar} />;
}


const styles = StyleSheet.create({
  bar: {
    height: "100%",
    width: "100%",
    margin: 10
  }
});

export default ProgressBar;
