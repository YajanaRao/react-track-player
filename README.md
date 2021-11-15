# react-track-player 🎧

[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/YajanaRao/Serenity/pulls)

Cross Platform audio streaming Module for React native

## Features

- [x] Background play
- [x] Notification
- [x] Support for online streaming and offline files

## Install

### Using npm

`npm install react-track-player --save`

### Using Yarn

`yarn add react-track-player`

## Example

```javascript
import { TrackPlayer } from "react-track-player";

load = () => {
  TrackPlayer.load({
    title: "Awesome song",
    artist: "Mr. Awesome",
    album: "Awesome songs only",
    cover: "https://source.unsplash.com/random",
    path: "https://dl.dropboxusercontent.com/s/8avcnxmjtdujytz/Sher%20Aaya%20Sher.mp3?dl=0",
  }).then(() => {
    console.log("audio loaded");
  });
};

play = () => {
  TrackPlayer.play();
};

pause = () => {
  TrackPlayer.pause();
};
```

### Event handler

```javascript
import { addEventListener } from "react-track-player";

subscription = addListener("media", function (event) {
  // handle event
  console.log("from event listener", event);
  if (event == "skip_to_next") {
    skipToNext();
  } else if (event == "skip_to_previous") {
    skipToPrevious();
  } else if (event == "completed") {
    skipToNext();
  } else {
    updateStatus();
  }
});

subscription.remove();
```

### API

#### State

```js
TrackPlayer.getState()
```

This method can be used to get the state of the player

#### Get Position

```js
TrackPlayer.getPosition();
```

Get track player progress position.
#### Get Duration

```js
TrackPlayer.getDuration();
```

Get track duration.

### Hooks

#### usePlaybackState

#### useProgress
### Development

Demo app is in `/example` directory
