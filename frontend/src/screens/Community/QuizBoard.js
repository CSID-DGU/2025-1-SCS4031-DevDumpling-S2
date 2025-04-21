import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function QuizBoardScreen({ navigation }) {
    const posts = [
        {
            title: "오늘 퀴즈",
            content: "재밌당",
            author: "퀴즈마스터",
        },
        {
            title: "퀴즈 한 달 기록 달성",
            content: "꾸준히 한 게 보이니까 뿌듯허네",
            author: "퀴즈조아",
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
                    title="퀴즈게시판"
                    onSearchPress={() => navigation.navigate('CommunitySearch')}
                    onWritePress={() => navigation.navigate('CommunityWrite')}
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