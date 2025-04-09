import { Text, View, SafeAreaView, } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import HomeScreen from './src/screens/Home/HomeScreen';
import "./global.css";

export default function App() {
  return (
    <SafeAreaView className="flex-1 bg-gray-50">
      <StatusBar style="light" />
      <HomeScreen />
    </SafeAreaView>
  );
}
