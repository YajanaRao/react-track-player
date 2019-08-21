# react-native-audio 
Android audio streaming Module for React native

## Features
- [x] Background play
- [x] Notification 
- [x] Support for online streaming and offline files

## Install

### Using npm
`npm install https://github.com/YajanaRao/react-native-audio.git --save` 

### Using Yarn

`yarn add https://github.com/YajanaRao/react-native-audio.git --save `

## Example

```javascript
import Audio from 'react-native-audio';


load = () => {
  Audio.load(
    "https://dl.dropboxusercontent.com/s/8avcnxmjtdujytz/Sher%20Aaya%20Sher.mp3?dl=0"
  );
}

play = () => {
  Audio.play();
}

pause = () => {
  Audio.pause();
}

```
