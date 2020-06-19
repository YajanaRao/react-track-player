import {requireNativeComponent, StyleProp, ViewProps} from "react-native";
import { NativeModules, StyleSheet } from "react-native";
import React, {useEffect} from "react";
const SeekBar = requireNativeComponent("SeekBar");
const MediaPlayer = NativeModules.MediaPlayer;

const ProgressBar = ({style}:{style: StyleProp<ViewProps>}) => {
  useEffect(() => {
    MediaPlayer.init();
    return () => {
      MediaPlayer.terminate();
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
