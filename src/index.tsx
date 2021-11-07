import { DeviceEventEmitter, NativeEventEmitter, NativeModules, Platform } from "react-native";


const { TrackPlayer } = Platform.OS === "web" ? require("./WebPlayer") : NativeModules;

const emitter = Platform.OS !== 'android' ? new NativeEventEmitter(TrackPlayer) : DeviceEventEmitter;

function addEventListener(event: string, listener: any) {
    if (Platform.OS === "web") {
        // @ts-ignore
        return document.addEventListener(event, ({ detail }) => listener(detail));
    }
    return emitter.addListener(event, listener);
}

export { TrackPlayer, addEventListener };