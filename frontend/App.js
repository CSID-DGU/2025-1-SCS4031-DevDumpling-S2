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
import CommunityPosts from './src/screens/Community/CommunityPosts';
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
import NewsDetail from './src/screens/NewsDetail';
import BankSelection from './src/screens/BankSelection';
import MyDataComplete from './src/screens/MyDataComplete';
import ProductsHome from './src/screens/Products/ProductsHome';
import YouthProductList from './src/screens/Products/Youth/YouthProductList';
import DepositProduct from './src/screens/Products/Others/DepositProduct';
import CardProduct from './src/screens/Products/Others/CardProduct';
import LoanProduct from './src/screens/Products/Others/LoanProduct';
import InsuranceProduct from './src/screens/Products/Others/InsuranceProduct';
import ETFProduct from './src/screens/Products/Others/ETFProduct';
import InvestProduct from './src/screens/Products/Others/InvestProduct';
import AddYouthInfo from './src/screens/Products/Youth/AddYouthInfo';

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
            name="CommunityPosts"
            component={CommunityPosts}
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
          <Stack.Screen
            name="NewsDetail"
            component={NewsDetail}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="BankSelection"
            component={BankSelection}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="MyDataComplete"
            component={MyDataComplete}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="ProductsHome"
            component={ProductsHome}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="YouthProductList"
            component={YouthProductList}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="DepositProduct"
            component={DepositProduct}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="CardProduct"
            component={CardProduct}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="LoanProduct"
            component={LoanProduct}
            options={{ headerShown: false }}
          />
          
          <Stack.Screen
            name="InsuranceProduct"
            component={InsuranceProduct}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="ETFProduct"
            component={ETFProduct}
            options={{ headerShown: false }}
          />          
          <Stack.Screen
            name="InvestProduct"
            component={InvestProduct}
            options={{ headerShown: false }}
          />
          <Stack.Screen
            name="AddYouthInfo"
            component={AddYouthInfo}
            options={{ headerShown: false }}
          />
        </Stack.Navigator>
        <Loading visible />
      </NavigationContainer>
    </LoadingProvider>
  );
}