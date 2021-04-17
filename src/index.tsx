import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from "react-native";
import ProgressBar from './ProgressBar';


const { TrackPlayer } = Platform.OS === "web" ? require("./WebPlayer") : NativeModules;

const emitter = Platform.OS !== 'android' ? new NativeEventEmitter(TrackPlayer) : DeviceEventEmitter;

function addEventListener(event: string, listener: any) {
    return emitter.addListener(event, listener);
}

export { ProgressBar, TrackPlayer, addEventListener };