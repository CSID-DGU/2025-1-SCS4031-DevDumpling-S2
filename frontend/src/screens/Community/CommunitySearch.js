import { View, ScrollView, Text, TextInput, useWindowDimensions, TouchableOpacity, Touchable } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';


export default function CommunitySearchScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const recentKeywords = ["연금", "보험 증명서", "퇴직연금"];

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">

                {/* 카테고리 탭 */}
                <View
                    className="flex-row items-center bg-[#014029] rounded-2xl mb-4"
                    style={{
                        paddingVertical: 12,
                        paddingHorizontal: horizontalPadding,
                    }}>
                    {/* 중앙 타이틀 */}
                    <Text className="text-white text-sm font-semibold">커뮤니티 글 검색</Text>
                </View>

                {/* 게시글 작성 */}
                <View
                    className="flex-1 bg-[#F9F9F9] rounded-2xl shadow-md"
                    style={{
                        paddingHorizontal: horizontalPadding,
                    }}>
                    <View className="border-t border-gray-200 mx-4" />
                </View>
                
            </View>
        </>
    );
}