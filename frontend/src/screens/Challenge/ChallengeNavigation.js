import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ChallengeHomeScreen from './ChallengeHomeScreen';
import ChallengeDetailScreen from './ChallengeDetailScreen';
import CategoryDetailScreen from './CategoryDetailScreen';

const Stack = createNativeStackNavigator();

export default function ChallengeNavigation() {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
      }}
    >
      <Stack.Screen name="ChallengeHome" component={ChallengeHomeScreen} />
      <Stack.Screen name="ChallengeDetail" component={ChallengeDetailScreen} />
      <Stack.Screen name="CategoryDetail" component={CategoryDetailScreen} />
    </Stack.Navigator>
  );
}
