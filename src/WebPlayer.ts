import ProgressBar from './ProgressBar';

const  MediaPlayer =  {
    player: HTMLAudioElement,
    load(url: string) {
        return new Promise( (resolve, reject) => {
            try {
                this.player = new Audio(url);
                resolve();
            } catch (e) {
                reject();
            }
        })
    },
    play(){
        this.player.play();
    },
    pause(){
        this.player.pause();
    }
}

export { MediaPlayer };