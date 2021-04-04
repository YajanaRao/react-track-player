import { DeviceEventEmitter } from "react-native";

const TrackPlayer = {
    player: new Audio(),
    load(url: string) {
        return new Promise((resolve, reject) => {
            try {
                this.player = new Audio(url);
                resolve(null);
            } catch (e) {
                reject();
                console.log("Web Player", e);
            }
        })
    },
    play() {
        try {
            this.player.play().catch((error: any) => console.log(error));
            this.player.addEventListener('ended', () => {
                DeviceEventEmitter.emit("skip_to_next")
            });
        } catch (error) {
            console.log("Web Player", error);
        }
    },
    pause() {
        try {
            this.player.pause();
        } catch (error) {
            console.log("Web Player", error);
        }
    }
}

export { TrackPlayer };