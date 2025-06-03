import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';

const Scraps = () => {
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
                    <Text className="text-2xl text-[#014029] font-bold mb-8">User님이 스크랩한 기사</Text>
                    <TouchableOpacity className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md">
                        <Text className="text-xs text-gray-500 mb-1">전자신문</Text>
                        <Text className="text-base font-bold mb-2" numberOfLines={2}>환경부, 추경 1753억 편성…“산불·싱크홀 등 재난 대응에 집중 투자”</Text>
                        <Text className="text-xs text-gray-500">2025-04-18</Text>
                    </TouchableOpacity>
                </ScrollView>
            </View>
        </>
    );
};

export default Scraps;
