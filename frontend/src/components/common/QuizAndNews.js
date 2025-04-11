import { View, Text, TouchableOpacity, Image } from 'react-native';

export default function QuizAndNews() {
    return (
        <View className="flex-row space-x-4 mt-2">
            {/* 퀴즈 */}
            <TouchableOpacity className="w-[48%] bg-white rounded-[15px] shadow-md p-4 items-center justify-center">
                <Image
                    source={require('../../../assets/images/test.png')}
                    className="w-[50px] h-[50px] mb-2"
                    resizeMode="contain"
                />
                <Text className="text-[16px] font-bold text-black text-center">금융 퀴즈</Text>
                <Text className="text-[16px] font-bold text-black text-center">풀러 가기</Text>
            </TouchableOpacity>

            {/* 추천 기사 */}
            <View className="w-[48%] bg-white rounded-[15px] shadow-md p-4">
                <View className="flex-row items-center mb-2">
                    <View className="w-[24px] h-[24px] bg-[#E8F1EB] rounded-full items-center justify-center mr-2">
                    </View>
                </View>
                <Text className="text-[14px] font-bold text-black leading-5 mb-2">최고 연 7.20% 기본 연 1.20%</Text>
                <Text className="text-[12px] text-[#6D6D6D]">로그인하면 맞춤 금융 상품을 살펴볼 수 있어요!</Text>
            </View>
        </View>
    );
}
