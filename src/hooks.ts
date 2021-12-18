import * as React from 'react';
import {  EmitterSubscription, NativeModules, Platform } from "react-native";
import { addEventListener } from './events';
const { TrackPlayer } = Platform.OS === "web" ? require("./WebPlayer") : NativeModules;

/** Get current playback state and subsequent updatates  */
export const usePlaybackState = () => {
    const [state, setState] = React.useState("")
    const isUnmountedRef = React.useRef(true)
  
    React.useEffect(() => {
      isUnmountedRef.current = false
      return () => {
        isUnmountedRef.current = true
      }
    }, [])
  
    React.useEffect(() => {
      async function setPlayerState() {
        const playerState = await TrackPlayer.getState()
  
        // If the component has been unmounted, exit
        if (isUnmountedRef.current) return
  
        setState(playerState)
      }
  
      // Set initial state
      setPlayerState()
  
      const sub: any = addEventListener('media', (state: string) => {
        setState(state)
      })
  
      return () =>{
        if(sub !== undefined){
            sub.remove();
        }
      } 
    }, [])
  
    return state
  }

export function useProgress(updateInterval?: number) {
    const [state, setState] = React.useState({ position: 0, duration: 0 })
    const playerState = usePlaybackState();
    const stateRef = React.useRef(state)
    const isUnmountedRef = React.useRef(true)
  
    React.useEffect(() => {
      isUnmountedRef.current = false
      return () => {
        isUnmountedRef.current = true
      }
    }, [])
  
    const getProgress = async () => {
      const [position, duration] = await Promise.all([
        TrackPlayer.getPosition(),
        TrackPlayer.getDuration(),
      ])
  
      // If the component has been unmounted, exit
      if (isUnmountedRef.current) return
  
      // If there is no change in properties, exit
      if (
        position === stateRef.current.position &&
        duration === stateRef.current.duration
      )
        return
  
      const state = { position, duration }
      stateRef.current = state
      setState(state)
    }
  
    React.useEffect(() => {
      if (playerState === "") {
        setState({ position: 0, duration: 0 })
        return
      }
  
      // Set initial state
      getProgress()
  
      // Create interval to update state periodically
      const poll = setInterval(getProgress, updateInterval || 1000)
      return () => clearInterval(poll)
    }, [playerState, updateInterval])
  
    return state
  }