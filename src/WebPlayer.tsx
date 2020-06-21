import { DeviceEventEmitter } from "react-native";

const MediaPlayer = {
    player: new Audio(),
    load(url: string) {
        return new Promise((resolve, reject) => {
            try {
                this.player = new Audio(url);
                this.player.onended = () => {
                    console.log("done playing");
                    DeviceEventEmitter.emit("skip_to_next");
                }
                resolve();
            } catch (e) {
                reject();
            }
        })
    },
    play() {
        this.player.play().then(() => console.log("playing"));
    },
    pause() {
        this.player.pause();
    }
}

export { MediaPlayer };