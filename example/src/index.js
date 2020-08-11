import React from 'react';
import {Text} from "react-native";
import ReactDOM from 'react-dom';
import * as serviceWorker from './serviceWorker';

const App = () => (
	<Text>Hello</Text>
);

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
