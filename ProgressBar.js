import { requireNativeComponent } from "react-native";
import { StyleSheet } from "react-native";
import React from "react";
const SeekBar = requireNativeComponent("SeekBar");

class ProgressBar extends React.Component {
  render() {
    return <SeekBar style={this.props.style ? this.props.style : styles.bar} />;
  }
}

const styles = StyleSheet.create({
  bar: {
    height: "100%",
    width: "100%",
    margin: 10
  }
});

export default ProgressBar;
