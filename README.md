# react-track-player 🎧

[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/YajanaRao/Serenity/pulls)

Cross Platform audio streaming Module for React native. Provides audio playback, external media controls, background mode and more!

## Features

- [x] Lightweight - Complete audio library without managing queue, the code is optimized to use least resources
- [x] Background play - Audio can be playing in the background and media can be controlled externally as well
- [x] Local or network, files or streams - Support for online streaming and offline files
- [x] Multi-platform - Supports Android, iOS and Web
- [x] Supports React Hooks 🎣 - Includes React Hooks for common use-cases so you don’t have to write them


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

## Install

Install the module using yarn or npm
### Using npm

`npm install react-track-player --save`

### Using Yarn

`yarn add react-track-player`

## Getting started

First of all, you need to set up the player. This usually takes less than a second:

```javascript
import TrackPlayer from 'react-track-player';

await TrackPlayer.setup({})
// The player is ready to be used
```

## Player Information
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

**Returns**: Promise<String>

#### Get Position

```js
TrackPlayer.getPosition();
```

Get track player progress position.

**Returns**: Promise<number>
#### Get Duration

```js
TrackPlayer.getDuration();
```
Gets the duration of the current track in seconds.

**Returns**: Promise<number>

#### Destroy

```js
TrackPlayer.destroy()
```

Destroys the player, cleaning up its resources. After executing this function, you won’t be able to use the player anymore, unless you call setup() again.
Get track duration.

### Hooks

#### usePlaybackState

usePlaybackState gives the state of the player 

#### useProgress

useProgress accepts an interval to set the rate (in miliseconds) to poll the track player’s progress. The default value is 1000 or every second.

```js
import React from 'react';
import { Text, View } from 'react-native';
import { TrackPlayer, useProgress } from 'react-track-player';

const MyComponent = () => {
  const { position, duration } = useProgress()

  return (
    <View>
    <Slider
          style={{ width: '100%', height: 40 }}
          minimumValue={0}
          maximumValue={duration}
          value={position}
          onSlidingComplete={value => {
            TrackPlayer.pause();
            TrackPlayer.seekTo(value)
            TrackPlayer.play();
          }}
        />
      <Text>Track progress: {position} seconds out of {duration} total</Text>
    </View>
  )
}
```

### Development

Demo app is in `/example` directory
