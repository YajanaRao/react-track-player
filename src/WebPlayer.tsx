
const MediaPlayer = {
    player: new Audio(),
    load(url: string) {
        return new Promise((resolve, reject) => {
            try {
                this.player = new Audio(url);
                resolve();
            } catch (e) {
                reject();
            }
        })
    },
    play() {
        this.player.play().then(() => console.log("playing"))
    },
    pause() {
        this.player.pause();
    }
}

export {MediaPlayer};