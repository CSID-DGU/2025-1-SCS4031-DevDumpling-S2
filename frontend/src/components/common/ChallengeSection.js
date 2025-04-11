import { View, Text, Image, ScrollView } from 'react-native';

export default function ChallengeSection() {
    return (
        <View>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="pt-2">
                {/* 메인 챌린지 카드 */}
                <View className="w-[290px] h-[180px] bg-white rounded-[20px] p-4 mr-4 shadow-md">
                    <View className="flex-row justify-between mb-1">
                        <Text className="text-[12px] text-[#6D6D6D]">1위 · 저축 챌린지</Text>
                        <Text className="text-[12px] text-[#6D6D6D]">자세히 보기 {'>'}</Text>
                    </View>
                    <View className="flex-row items-center">
                        <Image
                            source={require('../../../assets/images/plane-ticket.png')}
                            className="w-[80px] h-[80px]"
                            resizeMode="contain"
                        />
                        <View className="ml-4">
                            <Text className="text-[18px] font-bold text-black mb-1">떠나자!</Text>
                            <Text className="text-[18px] font-bold text-black">여행 자금 모으기</Text>
                        </View>
                    </View>
                    <View className="flex-row items-center justify-center mt-3">
                        <View className="flex-row items-center">
                            <Text className="text-[12px] text-[#6D6D6D]">좋아요 130</Text>
                        </View>
                        <Text className="text-[12px] text-[#6D6D6D] mx-1">·</Text>
                        <View className="flex-row items-center">
                            <Text className="text-[12px] text-[#6D6D6D]">89명 참여 중</Text>
                        </View>
                    </View>
                    <View className="flex-row mt-2 justify-center">
                        <View className="w-2 h-2 rounded-full bg-[#0A4C2C] mx-0.5"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-0.5"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-0.5"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-0.5"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-0.5"></View>
                    </View>
                </View>

                {/* 참여 안내 카드 */}
                <View className="w-[120px] h-[180px] bg-white rounded-[20px] p-4 shadow-md flex justify-center items-center">
                    <Text className="text-[14px] text-[#6D6D6D] text-center mb-4">참여중인 챌린지</Text>
                    <Text className="text-[14px] text-black text-center leading-5">
                        로그인하고{'\n'}챌린지에{'\n'}함께해보세요
                    </Text>
                    <Text className="text-[14px] text-black text-center mt-1">😊</Text>
                </View>
            </ScrollView>
        </View>
    );
}
