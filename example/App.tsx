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
  DeviceEventEmitter
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

const App = () => {
  const [audioState, setAudioState] = useState("init")
  let subscription = null
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
    TrackPlayer.load(
      "https://dl.dropboxusercontent.com/s/8avcnxmjtdujytz/Sher%20Aaya%20Sher.mp3?dl=0"
    ).then(() => {
      console.log("audio loaded");
      setAudioState("loaded")
    });
  }

  const playAudio = () => {
    TrackPlayer.play()
  }

  const pauseAudio = () => {
    TrackPlayer.pause()
  }

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          <View style={styles.engine}>
            <Text style={styles.footer}>{audioState}</Text>
          </View>

          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Load Audio</Text>
              <Text style={styles.sectionDescription}>
                Click <Text style={styles.highlight}>Load</Text> to load the audio
              </Text>
              <Button title="Load" onPress={loadAudio} />
            </View>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Play Audio</Text>
              <Button title="Play" onPress={playAudio} />
            </View>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Pause Audio</Text>
              <Button title="Pause" onPress={pauseAudio} />
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
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
