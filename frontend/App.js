import React from 'react';
import { SafeAreaView } from 'react-native';
import HomeScreen from './homescreen';
import 'nativewind';

const App = () => {
  return (
    <SafeAreaView className="flex-1">
      <HomeScreen />
    </SafeAreaView>
  );
};

export default App;
