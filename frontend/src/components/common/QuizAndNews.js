import { View, Text, TouchableOpacity, Image, useWindowDimensions } from 'react-native';

export default function QuizAndNews() {
    const { width } = useWindowDimensions();
    const cardWidth = (width - 40) / 2 - 8;

    return (
        <View className="flex-row justify-between mt-3">
            {/* 퀴즈 섹션 */}
            <View style={{ width: cardWidth }}>
                <Text className="text-[20px] font-bold text-Fineed-green mb-3">맞춤 퀴즈</Text>
                <TouchableOpacity
                    style={{ height: cardWidth * 1.15 }}
                    className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                >
                    <Image
                        source={require('../../../assets/images/test.png')}
                        style={{ width: width > 380 ? 80 : 70, height: width > 380 ? 80 : 70 }}
                        resizeMode="contain"
                    />
                    <Text className="text-[18px] font-bold text-black text-center mt-4">금융 퀴즈</Text>
                    <Text className="text-[18px] font-bold text-black text-center">풀러 가기</Text>
                </TouchableOpacity>
            </View>

            {/* 추천 기사 섹션 */}
            <View style={{ width: cardWidth }}>
                <Text className="text-[20px] font-bold text-Fineed-green mb-3">추천 기사</Text>
                <View
                    style={{ height: cardWidth * 1.15 }}
                    className="bg-white rounded-[20px] shadow-md p-5"
                >
                    <View className="mb-2">
                        <Text className="text-[10px] text-[#6D6D6D]">적금 · 케이뱅크</Text>
                    </View>
                    <Text className="text-[20px] font-bold text-black ">궁금한 적금</Text>
                    <Text className="text-[10px] text-[#6D6D6D] mt-2">최고 연 7.20% 기본 연 1.20%</Text>

                    <Text className="text-[10px] text-[#6D6D6D] text-center mt-10">
                        로그인하면 맞춤 추천 금융 상품을 받아볼 수 있어요!
                    </Text>

                    <View className="flex-row justify-center mt-4">
                        <Text className="text-[12px] text-[#6D6D6D]">금융 기사 더 보러 가기 {'>'}</Text>
                    </View>
                </View>
            </View>
        </View>
    );
}
