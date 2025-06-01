import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';

const MyType = () => {
  const { width } = useWindowDimensions();
  const horizontalPadding = width > 380 ? 16 : 12;
  const navigation = useNavigation();

  // 나중에 백엔드에서 받아올 사용자 유형
  const selectedType = '도전러';

  const types = [
    { name: '도전러', emoji: '🐯', desc1: '신용·투자에 적극적,', desc2: '소비도 즐겨요' },
    { name: '계획러', emoji: '🦊', desc1: '절약하면서도 투자에', desc2: '관심 많은 합리파' },
    { name: '편안러', emoji: '🐻', desc1: '소비는 즐기지만', desc2: '리스크는 싫어해요' },
    { name: '안심러', emoji: '🐢', desc1: '절약과 안정을', desc2: '추구하는 보수파' },
  ];

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
              User님의 유형은 {selectedType}예요!
            </Text>

            <View className="flex-row flex-wrap items-center justify-center gap-3 mb-5">
              {types.map((type, index) => {
                const isSelected = type.name === selectedType;
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
              onPress={() => navigation.navigate('UserTypeLoading')}
            >
              <Text className="text-lg font-bold text-center text-white">다시 분석하기</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </View>
    </>
  );
};

export default MyType;
