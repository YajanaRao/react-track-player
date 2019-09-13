import { NativeModules } from "react-native";
import {requireNativeComponent} from 'react-native';

module.exports = NativeModules.RNAudio;
module.exports.ProgressBar = requireNativeComponent('SeekBar');
