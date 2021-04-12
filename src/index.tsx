import { NativeModules, Platform } from "react-native";
import ProgressBar from './ProgressBar';


const { TrackPlayer } = Platform.OS === "web" ? require("./WebPlayer") : NativeModules;


export { ProgressBar, TrackPlayer };