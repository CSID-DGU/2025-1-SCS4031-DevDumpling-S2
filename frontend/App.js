import { Text, View, SafeAreaView } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import HomeScreen from './src/screens/Home/HomeScreen';
import CommunityHome from './src/screens/Community/CommunityHome';
import HotBoard from './src/screens/Community/HotBoard';
import InvestBoard from './src/screens/Community/InvestBoard';
import ChallengeBoard from './src/screens/Community/ChallengeBoard';
import QuizBoard from './src/screens/Community/QuizBoard';
import FreeBoard from './src/screens/Community/FreeBoard';
import CommunitySearch from './src/screens/Community/CommunitySearch';
import CommunityWrite from './src/screens/Community/CommunityWrite';
import Login from './src/screens/Login';
import Mypage from './src/screens/Mypage';
import "./global.css";
import { useFonts } from 'expo-font';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { LoadingProvider } from './src/contexts/LoadingContext';
import Loading from './src/components/common/Loading';
import NewsList from './src/screens/NewsList';
import Quiz from './src/screens/Quiz';
import MyDataConsent from './src/screens/MyDataConsent';


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
            name="CommunitySearch"
            component={CommunitySearch}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Login"
            component={Login}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="HotBoard"
            component={HotBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="InvestBoard"
            component={InvestBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="ChallengeBoard"
            component={ChallengeBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="QuizBoard"
            component={QuizBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="FreeBoard"
            component={FreeBoard}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="CommunityWrite"
            component={CommunityWrite}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Mypage"
            component={Mypage}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="NewsList"
            component={NewsList}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="Quiz"
            component={Quiz}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="MyDataConsent"
            component={MyDataConsent}
            options={{ headerShown: false }}
          />
        </Stack.Navigator>
        <Loading visible />
      </NavigationContainer>
    </LoadingProvider>
  );
}