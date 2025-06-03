import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

const types = [
  { code: 'A', name: '도전러', emoji: '🐯', desc1: '신용·투자에 적극적,', desc2: '소비도 즐겨요' },
  { code: 'B', name: '계획러', emoji: '🦊', desc1: '절약하면서도 투자에', desc2: '관심 많은 합리파' },
  { code: 'C', name: '편안러', emoji: '🐻', desc1: '소비는 즐기지만', desc2: '리스크는 싫어해요' },
  { code: 'D', name: '안심러', emoji: '🐢', desc1: '절약과 안정을', desc2: '추구하는 보수파' },
];

const UserTypeResult = () => {
  const { width } = useWindowDimensions();
  const horizontalPadding = width > 380 ? 16 : 12;
  const navigation = useNavigation();

  const [selectedType, setSelectedType] = useState(null);

  useEffect(() => {
    const fetchUserType = async () => {
      try {
        const storedUserData = await AsyncStorage.getItem('userData');
        if (storedUserData) {
          const userData = JSON.parse(storedUserData);
          setSelectedType(userData.userType); // A/B/C/D
        }
      } catch (error) {
        console.error('유저 타입 불러오기 실패:', error);
        setSelectedType(null);
      }
    };
    fetchUserType();
  }, []);

  // 한글 이름 찾기
  const selectedTypeObj = types.find(type => type.code === selectedType);

  return (
    <>
      <Header />
      <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10 mt-20">
        <ScrollView
          contentContainerClassName="justify-center"
          contentContainerStyle={{
            paddingHorizontal: horizontalPadding,
            paddingTop: 16,
            paddingBottom: 24,
          }}
        >
          <View className="flex-col items-center justify-center">
            <Text className="text-2xl text-black font-bold mb-8">
              User님의 유형은 {selectedTypeObj ? selectedTypeObj.name : '...'}예요!
            </Text>

            <View className="flex-row flex-wrap items-center justify-center gap-3 mb-5">
              {types.map((type, index) => {
                const isSelected = type.code === selectedType;
                return (
                  <View
                    key={index}
                    className={`w-[160px] items-center justify-center mb-1 p-6 rounded-2xl shadow-md ${
                      isSelected ? 'bg-Fineed-green' : 'bg-[#F9F9F9]'
                    }`}
                  >
                    <Text className={`text-2xl font-bold mb-2 ${isSelected ? 'text-white' : 'text-black'}`}>
                      {type.emoji}
                    </Text>
                    <Text className={`text-lg font-bold mb-2 ${isSelected ? 'text-white' : 'text-black'}`}>
                      {type.name}
                    </Text>
                    <Text className={`text-sm ${isSelected ? 'text-white' : 'text-black'}`}>
                      {type.desc1}
                    </Text>
                    <Text className={`text-sm ${isSelected ? 'text-white' : 'text-black'}`}>
                      {type.desc2}
                    </Text>
                  </View>
                );
              })}
            </View>

            <TouchableOpacity
              className="bg-Fineed-green text-white px-20 py-4 rounded-2xl mt-5"
              onPress={() => navigation.navigate('Quiz')}
            >
              <Text className="text-lg font-bold text-center text-white">퀴즈 풀러 가기</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </View>
    </>
  );
};

export default UserTypeResult;
