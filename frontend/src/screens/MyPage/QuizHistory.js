import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';

const QuizHistory = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const navigation = useNavigation();

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    <Text className="text-2xl text-[#014029] font-bold mb-8">User님의 퀴즈 기록</Text>

                    <View
                        className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md p-5 mb-5">
                            <Text className="text-xs text-gray-500 mb-1">2024-03-21</Text>
                            <View className="flex-row items-center justify-between gap-3">
                                <Text className="text-5xl font-bold text-black mb-1 ml-2 mr-4">O</Text>

                                <View className="flex-1 flex-col justify-center">
                                    <Text
                                        className="text-m text-black mb-2"
                                        style={{ flexShrink: 1 }}>
                                            Q. 최근 금리가 인상되면 소비자에게 가장 먼저 영향을 미치는 것은 무엇일까요?</Text>
                                    <Text className="text-m text-[#014029] font-bold mb-1">A. 대출 이자</Text>
                                </View>
                            </View>
                    </View>

                    <View
                        className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md p-5 mb-5">
                            <Text className="text-xs text-gray-500 mb-1">2024-03-21</Text>
                            <View className="flex-row items-center justify-between gap-3">
                                <Text className="text-5xl font-bold text-black mb-1 ml-2 mr-4">X</Text>

                                <View className="flex-1 flex-col justify-center">
                                    <Text
                                        className="text-m text-black mb-2"
                                        style={{ flexShrink: 1 }}>
                                            Q. 최근 금리가 인상되면 소비자에게 가장 먼저 영향을 미치는 것은 무엇일까요?</Text>
                                            <Text
                                                className="text-m text-red-500 font-bold mb-1"
                                                style={{ textDecorationLine: 'line-through' }}  // 선 긋기
                                                >
                                                A. 통신비
                                                </Text>
                                    <Text className="text-m text-[#014029] font-bold mb-1">A. 대출 이자</Text>
                                </View>
                            </View>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default QuizHistory;
