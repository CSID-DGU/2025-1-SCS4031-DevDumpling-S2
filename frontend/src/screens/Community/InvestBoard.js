import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function InvestBoardScreen({ navigation }) {
    const posts = [
        {
            title: "주식 투자 초보를 위한 팁",
            content: "주식 투자를 시작하는 분들을 위한 기본적인 조언들입니다...",
            author: "투자마스터",
        },
        {
            title: "부동산 투자 전략",
            content: "부동산 시장 분석과 투자 전략에 대해 알아봅시다...",
            author: "부동산전문가",
        },
        // ... 추가 게시글들
    ];

    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <BoardHeader
                    navigation={navigation}
                    title="투자게시판"
                    onSearchPress={() => navigation.navigate('CommunitySearch')}
                    onWritePress={() => navigation.navigate('InvestBoardWrite')}
                />

                <ScrollView
                    className="flex-1"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                    }}>
                    <View className="bg-[#F9F9F9] rounded-2xl shadow-md">
                        {posts.map((post, index) => (
                            <BoardPostItem
                                key={index}
                                post={post}
                                isLastItem={index === posts.length - 1}
                                onPress={() => {/* TODO: 게시글 상세 페이지로 이동 */}}
                            />
                        ))}
                    </View>
                </ScrollView>

                <TouchableOpacity className="mb-8 items-center"
                    onPress={() => navigation.navigate('Home')}>
                    <Text className="text-sm text-[#6D6D6D] underline">메인으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
} 