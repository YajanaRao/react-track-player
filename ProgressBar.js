import {requireNativeComponent} from 'react-native';
import { NativeModules, StyleSheet } from "react-native";
import React from 'react';

const SeekBar = requireNativeComponent('SeekBar');
const RNAudio = NativeModules.RNAudio;


class ProgressBar extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      status: "init"
    };
  }

   static getDerivedStateFromProps(props, state) {
      if (props.status != state.status) {
        return {
          status: props.status
        };
      }
      return null;
    }
	componentDidMount() {
	    RNAudio.init();

  	}

  	componentDidUpdate(prevProps) {
      // Typical usage (don't forget to compare props):
      if (this.props.status !== prevProps.status) {
        if(this.state.status == "playing"){
          try{

               this._interval = setInterval(() => {
                  RNAudio.update();
              }, 1000);
          }catch(error){
              console.log("ProgressBar: "+error);
          }
      }else{
        try{
            clearInterval(this._interval);
        }catch(error){
        	console.log(error);
        }
      }
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
       height: '100%',
       width: '100%',
       margin: 10
     }
});

export default ProgressBar;
