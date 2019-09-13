import {requireNativeComponent} from 'react-native';
import { NativeModules, StyleSheet } from "react-native";
import React from 'react';

const SeekBar = requireNativeComponent('SeekBar');
const RNAudio = NativeModules.RNAudio;


class ProgressBar extends React.Component {
	componentDidMount() {
		try{
    		RNAudio.init();		
    		 this._interval = setInterval(() => {
    			RNAudio.update();
  			}, 1000);	
		}catch(error){
			console.log("ProgressBar: "+error);
		}
  	}

  	componentWillUnmount() {
  		try{
  			clearInterval(this._interval);
  		}catch(error){
  			console.log(error);
  		}
	  
	}

	render(){
		return (
			<SeekBar style={this.props.style ? this.props.style : styles.bar} />
			)
	}
}

const styles = StyleSheet.create({
  bar: {
    height: 5,
    width: '100%',
    margin: 10,
    flexDirection: 'row',
    alignItems: 'flex-start',
  }
});

export default ProgressBar;
