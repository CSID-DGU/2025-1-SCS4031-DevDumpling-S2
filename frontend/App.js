import { Text, View, SafeAreaView } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import HomeScreen from './src/screens/Home/HomeScreen';
import CommunityHome from './src/screens/Community/CommunityHome';
import FreeBoard from './src/screens/Community/FreeBoard';
import Login from './src/screens/Login';
import Mypage from './src/screens/Mypage';
import "./global.css";
import { useFonts } from 'expo-font';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LoadingProvider } from './src/contexts/LoadingContext';
import Loading from './src/components/common/Loading';

const Stack = createNativeStackNavigator();

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
    <LoadingProvider>
      <NavigationContainer>
        <StatusBar style="light" />
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen
            name="Home"
            component={HomeScreen}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Community"
            component={CommunityHome}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Login"
            component={Login}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="FreeBoard"
            component={FreeBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Mypage"
            component={Mypage}
            options={{ headerShown: false }}
          />
        </Stack.Navigator>
        <Loading visible />
      </NavigationContainer>
    </LoadingProvider>
  );
}