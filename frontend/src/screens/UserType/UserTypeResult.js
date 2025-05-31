import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/FontAwesome';

const UserTypeResult = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10 mt-20">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    {/* 상품 이름 */}
                    <View className="flex-col items-center justify-center">
                        <Text className="text-2xl text-black font-bold mb-8">User님의 유형은 도전러예요!</Text>

                        <View className="flex-row items-center justify-center gap-3">
                            <View className="w-[160px] items-center justify-center mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                                <Text className="text-2xl font-bold mb-2">🐯</Text>
                                <Text className="text-lg font-bold mb-2">도전러</Text>
                                <Text className="text-sm">신용·투자에 적극적,</Text>
                                <Text className="text-sm">소비도 즐겨요</Text>
                            </View>

                            <View className="w-[160px] items-center justify-center mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                                <Text className="text-2xl font-bold mb-2">🦊</Text>
                                <Text className="text-lg font-bold mb-2">계획러</Text>
                                <Text className="text-sm">절약하면서도 투자에</Text>
                                <Text className="text-sm">관심 많은 합리파</Text>
                            </View>
                        </View>

                        <View className="flex-row items-center justify-center gap-3">
                            <View className="w-[160px] items-center justify-center mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                                <Text className="text-2xl font-bold mb-2">🐻</Text>
                                <Text className="text-lg font-bold mb-2">편안러</Text>
                                <Text className="text-sm">소비는 즐기지만</Text>
                                <Text className="text-sm">리스크는 싫어해요</Text>
                            </View>

                            <View className="w-[160px] items-center justify-center mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                                <Text className="text-2xl font-bold mb-2">🐢</Text>
                                <Text className="text-lg font-bold mb-2">안심러</Text>
                                <Text className="text-sm">절약과 안정을</Text>
                                <Text className="text-sm">추구하는 보수파</Text>
                            </View>
                        </View>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default UserTypeResult;
