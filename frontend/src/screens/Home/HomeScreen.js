import { View, ScrollView, Text } from 'react-native';
import Header from '../../components/layout/Header';
import ChallengeSection from '../../components/common/ChallengeSection';
import RatingSection from '../../components/common/RatingSection';
import QuizAndNews from '../../components/common/QuizAndNews';
import CommunitySection from '../../components/common/CommunitySection';

export default function HomeScreen() {
  return (
    <View className="flex-1 bg-[#EFEFEF]">
      <Header />
      <ScrollView className="flex-1 px-4 pt-4 space-y-6">
        {/* 지금 뜨고 있는 챌린지 */}
        <Text className="text-[20px] font-bold text-[#0A4C2C]">지금 뜨고 있는 챌린지</Text>
        <ChallengeSection />

        {/* 다른 사람들의 평점 */}
        <Text className="text-[20px] font-bold text-black mt-6">다른 사람들의 평점은 어떨까?</Text>
        <RatingSection />

        {/* 맞춤 퀴즈 & 추천 기사 */}
        <View className="flex-row justify-between mt-6">
          <Text className="text-[20px] font-bold text-black">맞춤 퀴즈</Text>
          <Text className="text-[20px] font-bold text-black">추천 기사</Text>
        </View>
        <QuizAndNews />

        {/* 커뮤니티 */}
        <Text className="text-[20px] font-bold text-[#0A4C2C] mt-6">커뮤니티</Text>
        <CommunitySection />
      </ScrollView>
    </View>
  );
}
