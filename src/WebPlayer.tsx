import { DeviceEventEmitter } from "react-native";
import { log } from '@nadha/utils';

const MediaPlayer = {
    player: new Audio(),
    load(url: string) {
        return new Promise((resolve, reject) => {
            try {
                this.player = new Audio(url);
                resolve();
            } catch (e) {
                reject();
                log.error("Web Player", e);
            }
        })
    },
    play() {
        try {
            this.player.play().catch(error => log.error("MediaPlayer", error));
            this.player.addEventListener('ended', () => {
                log.debug("media player", "ended");
                DeviceEventEmitter.emit("skip_to_next")
            });
        } catch (error) {
            log.error("Web Player", error);
        }
    },
    pause() {
        try {
            this.player.pause();
        } catch (error) {
            log.error("Web Player", error);
        }
    }
}

export { MediaPlayer };