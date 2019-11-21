import { NativeModules } from "react-native";
import ProgressBar from './ProgressBar';


module.exports = NativeModules.MediaPlayer;
module.exports.ProgressBar = ProgressBar;
