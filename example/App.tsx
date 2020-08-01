/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React, { useEffect, useState } from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
  DeviceEventEmitter,
  ActivityIndicator
} from 'react-native';

import {
  Header,
  LearnMoreLinks,
  Colors,
  DebugInstructions,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';
import TrackPlayer from "react-track-player";

declare const global: { HermesInternal: null | {} };

const PlayBar = () => {
  const [audioState, setAudioState] = useState("init");
  let subscription = null;

  useEffect(() => {
    subscription = DeviceEventEmitter.addListener("media", function (event) {
      // handle event
      console.log("from event listener", event);
      if (event == "skip_to_next") {
        console.log('skip');
      } else if (event == "skip_to_previous") {
        console.log('skip');
      } else if (event == "completed") {
        setAudioState("completed")
      } else {
        setAudioState(event)
      }
    });
    return () => {
      subscription.remove();
    }
  }, []);

  const loadAudio = () => {
    setAudioState("loading")
    TrackPlayer.load(
      "https://dl.dropboxusercontent.com/s/6nwin3y04ohkep7/the-weeknd-kendrick-lamar-pray-for-me.mp3?dl=0"
    ).then(() => {
      console.log("audio loaded");
      setAudioState("paused")
    });
  }

  switch (audioState) {
    case "playing":
      return <Button title="Pause" onPress={() => TrackPlayer.pause()} />
    case "paused":
      return <Button title="Play" onPress={() => TrackPlayer.play()} />
    case "loading":
      return <ActivityIndicator />
    default:
      return (
        <View>
          <Text>{audioState}</Text>
          <Button title="Load" onPress={loadAudio} />
        </View>
      )
  }
}

const App = () => {





  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />


          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Load Audio</Text>
              <Text style={styles.sectionDescription}>
                Click <Text style={styles.highlight}>Load</Text> to load the audio
              </Text>
              <PlayBar />
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  }
});

export default App;
