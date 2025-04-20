import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function ChallengeBoardScreen({ navigation }) {
    const posts = [
        {
            title: "매일 1만원 저축 챌린지",
            content: "매일 1만원씩 저축하는 챌린지에 참여하세요!",
            author: "저축마스터",
        },
        {
            title: "한 달 동안 카드값 30% 줄이기",
            content: "카드값을 줄이는 방법을 공유하고 함께 실천해봐요!",
            author: "절약전문가",
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
                    title="챌린지게시판"
                    onSearchPress={() => navigation.navigate('CommunitySearch')}
                    onWritePress={() => navigation.navigate('ChallengeBoardWrite')}
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