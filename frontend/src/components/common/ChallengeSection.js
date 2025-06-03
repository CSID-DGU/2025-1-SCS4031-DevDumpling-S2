import { View, Text, Image, useWindowDimensions, TouchableOpacity } from 'react-native';

export default function ChallengeSection({ userData, challenges = [], categories = [], challengeLoading = false, navigation }) {
    const { width } = useWindowDimensions();
    const cardGap = 16;
    const availableWidth = width - 40;
    const mainCardWidth = availableWidth * 0.65;
    const secondCardWidth = availableWidth - mainCardWidth - cardGap;

    console.log('ChallengeSection userData:', userData);

    const getCategoryImage = (categoryId) => {
        const category = categories.find(cat => cat.id === categoryId);
        return category ? category.imageUrl : null;
    };

    return (
        <View className="my-2">
            <View className="flex-row justify-between">
                {/* 메인 챌린지 카드 */}
                <View
                    style={{ width: mainCardWidth }}
                    className="h-auto bg-white rounded-[20px] p-5 shadow-md"
                >
                    <View className="flex-row justify-between mb-2">
                        <Text className="text-[12px] text-[#6D6D6D]">1위 · 저축 챌린지</Text>
                        <Text className="text-[12px] text-[#6D6D6D]">자세히 보기 {'>'}</Text>
                    </View>
                    <View className="flex-row items-center flex-wrap">
                        <Image
                            source={require('../../../assets/images/plane-ticket.png')}
                            style={{ width: 80, height: 80 }}
                            resizeMode="contain"
                        />
                        <View className="ml-2 flex-1">
                            <Text className="text-[20px] font-bold text-black mb-1">떠나자!</Text>
                            <Text className="text-[20px] font-bold text-black">여행 자금 모으기</Text>
                        </View>
                    </View>
                    <View className="flex-row items-center justify-center mt-4">
                        <View className="flex-row items-center">
                            <Text className="text-[12px] text-[#6D6D6D]">좋아요 130</Text>
                        </View>
                        <Text className="text-[12px] text-[#6D6D6D] mx-2">·</Text>
                        <View className="flex-row items-center">
                            <Text className="text-[12px] text-[#6D6D6D]">89명 참여 중</Text>
                        </View>
                    </View>
                    <View className="flex-row mt-3 justify-center">
                        <View className="w-2 h-2 rounded-full bg-[#0A4C2C] mx-1"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-1"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-1"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-1"></View>
                        <View className="w-2 h-2 rounded-full bg-[#D9D9D9] mx-1"></View>
                    </View>
                </View>

                {/* 참여 안내/참여중인 챌린지 카드 */}
                <View
                    style={{ width: secondCardWidth }}
                    className="h-auto bg-white rounded-[20px] p-4 shadow-md flex justify-center items-center"
                >
                    <Text className="text-[14px] text-[#6D6D6D] text-center mb-2">참여중인 챌린지</Text>
                    {userData ? (
                        challengeLoading ? (
                            <Text className="text-center text-gray-500">로딩 중...</Text>
                        ) : challenges.length === 0 ? (
                            <Text className="text-[14px] text-black text-center leading-6">
                                아직 참여중인{'\n'}챌린지가{'\n'} 없어요
                            </Text>
                        ) : (
                            <TouchableOpacity
                                onPress={() => navigation && navigation.navigate('ChallengeDetail', { challengeId: challenges[0].id })}
                                className="items-center"
                            >
                                {getCategoryImage(challenges[0].categoryId) ? (
                                    <Image
                                        source={{ uri: getCategoryImage(challenges[0].categoryId) }}
                                        style={{ width: 48, height: 48, borderRadius: 24, marginBottom: 8 }}
                                        resizeMode="cover"
                                    />
                                ) : (
                                    <View style={{ width: 48, height: 48, borderRadius: 24, backgroundColor: '#eee', marginBottom: 8 }} />
                                )}
                                <Text className="text-[14px] text-black text-center font-bold">{challenges[0].title}</Text>
                            </TouchableOpacity>
                        )
                    ) : (
                        <>
                            <Text className="text-[14px] text-black text-center leading-6">
                                로그인하고{'\n'}챌린지에{'\n'}함께해보세요
                            </Text>
                            <Text className="text-[14px] text-black text-center mt-2">😊</Text>
                        </>
                    )}
                </View>
            </View>
        </View>
    );
}
