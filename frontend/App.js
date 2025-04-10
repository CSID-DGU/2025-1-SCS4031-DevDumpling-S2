import { Text, View, SafeAreaView } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import HomeScreen from './src/screens/Home/HomeScreen';
import "./global.css";
import { useFonts } from 'expo-font';

export default function App() {
  const [fontsLoaded] = useFonts({
    'Pretendard-ExtraBold': require('./assets/fonts/Pretendard-ExtraBold.otf'),
    'Pretendard-Bold': require('./assets/fonts/Pretendard-Bold.otf'),
    'Pretendard-Regular': require('./assets/fonts/Pretendard-Regular.otf'),
  });

  if (!fontsLoaded) {
    return null;
  }

  return (
    <SafeAreaView className="flex-1 bg-gray-50">
      <StatusBar style="light" />
      <HomeScreen />
    </SafeAreaView>
  );
}
