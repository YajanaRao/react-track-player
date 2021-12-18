import { NativeModules, Platform } from "react-native";
import { addEventListener } from './events'; 
import { useProgress, usePlaybackState } from "./hooks";
const { TrackPlayer } = Platform.OS === "web" ? require("./WebPlayer") : NativeModules;

export { TrackPlayer, addEventListener, useProgress, usePlaybackState };