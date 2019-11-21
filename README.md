# react-native-media-player 🎧
Android  audio streaming Module for React native

## Features
- [x] Background play
- [x] Notification 
- [x] Support for online streaming and offline files

## Install

### Using npm
`npm install https://github.com/YajanaRao/react-native-media-player.git --save` 

### Using Yarn

`yarn add https://github.com/YajanaRao/react-native-media-player.git --save `

## Example

```javascript
import MediaPlayer from 'react-native-media-player';

load = () => {
  MediaPlayer.load(
    "https://dl.dropboxusercontent.com/s/8avcnxmjtdujytz/Sher%20Aaya%20Sher.mp3?dl=0"
  ).then(() => {
    console.log("audio loaded");
  });
}

play = () => {
  MediaPlayer.play();
}

pause = () => {
  MediaPlayer.pause();
}
```

### Event handler
```javascript
import {DeviceEventEmitter} from 'react-native';
subscription = DeviceEventEmitter.addListener('media', function(event) {
      // handle event
      console.log('from event listener', event);
      if (event == 'skip_to_next') {
        dispatch(skipToNext());
      } else if (event == 'skip_to_previous') {
        dispatch(skipToPrevious());
      } else if (event == 'completed') {
        dispatch(skipToNext());
      } else {
        dispatch({
          type: 'STATUS',
          status: event,
        });
      }
    });
    
 subscription.remove();
```
### Components

#### ProgressBar
A component base that updates itself every second with a new position.

`import { ProgressBar } from 'react-native-media-player';`

ProgressBar interacts with native audio module and updates the progress. All you need to do is render `<ProgressBar>` with your styles if you want.
