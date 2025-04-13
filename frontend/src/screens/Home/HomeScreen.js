import { View, ScrollView, Text, useWindowDimensions } from 'react-native';
import Header from '../../components/layout/Header';
import ChallengeSection from '../../components/common/ChallengeSection';
import RatingSection from '../../components/common/RatingSection';
import QuizAndNews from '../../components/common/QuizAndNews';
import CommunitySection from '../../components/common/CommunitySection';

export default function HomeScreen() {
  const { width } = useWindowDimensions();
  const horizontalPadding = width > 380 ? 16 : 12;

  return (
    <View className="flex-1 bg-[#EFEFEF]">
      <Header />
      <ScrollView
        className="flex-1"
        contentContainerStyle={{
          paddingHorizontal: horizontalPadding,
          paddingTop: 16,
          paddingBottom: 24
        }}
      >
        {/* 지금 뜨고 있는 챌린지 */}
        <View className="mb-8">
          <Text className="text-[20px] font-bold text-black mb-2">지금 뜨고 있는 챌린지</Text>
          <ChallengeSection />
        </View>

        {/* 다른 사람들의 평점 */}
        <View className="mb-8">
          <Text className="text-[20px] font-bold text-black mb-2">다른 사람들의 평점은 어떨까?</Text>
          <RatingSection />
        </View>

        {/* 맞춤 퀴즈 & 추천 기사 */}
        <View className="mb-8">
          <QuizAndNews />
        </View>

        {/* 커뮤니티 */}
        <View className="mb-4">
          <Text className="text-[20px] font-bold text-Fineed-green mb-2">커뮤니티</Text>
          <CommunitySection />
        </View>
      </ScrollView>
    </View>
  );
}
