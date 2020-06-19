import { NativeModules, Platform } from "react-native";
import ProgressBar from './ProgressBar';


const { MediaPlayer } = Platform.OS === "web" ? require("./WebPlayer") :  NativeModules;

export { MediaPlayer };
export { ProgressBar };