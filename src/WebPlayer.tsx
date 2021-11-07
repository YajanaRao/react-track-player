
const TrackPlayer = {
    player: new Audio(),
    load(song: { title: string, cover: string, path: string }) {
        console.log("load")
        return new Promise((resolve, reject) => {
            try {
                this.player = new Audio(song.path);
                this.player.onplay = () => {
                    console.log("playing");
                    // DeviceEventEmitter.emit("media", "playing");
                    const customEvent = new CustomEvent('media', { detail: "playing" });
                    document.dispatchEvent(customEvent);
                };
                this.player.onpause = () => {
                    console.log("paused");
                    // DeviceEventEmitter.emit("media", "paused")
                    const customEvent = new CustomEvent('media', { detail: "paused" });
                    document.dispatchEvent(customEvent);
                };
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
    },
    destroy() {
        this.player = null;
    }
}

export { TrackPlayer };