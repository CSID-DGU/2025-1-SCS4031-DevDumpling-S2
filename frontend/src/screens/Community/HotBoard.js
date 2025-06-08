import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function HotBoardScreen({ navigation }) {
    const posts = [
        {
            title: "아무도 안 물어봤지만 내 최애 재테크 방법",
            content: "난 솔직히 월세 받는 게 최고라고 생각함. 거기까지 가는 게 문제긴 한데...",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "첫 월급으로 뭐 사야 현명한 소비일까?",
            content: "드디어 첫 월급 받았는데, 뭘 사야 후회 안 할까? 부모님 선물? 나를 위한 무언가?",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "금융 공부 다들 어떻게 하셈?",
            content: "ㅈㄱㄴ",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "중고거래로 1년 동안 모은 돈 자랑",
            content: "비싼 거 판 건 아니고 필요 없는 자잘한 거 팔고 꾸준히 돈 모았더니 1년 동안 87만원 모음 ㅋㅋㅋ",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "카드값 줄이는 현실적인 방법 있을까",
            content: "월말 카드값 방어 너무 힘듦... ㅠㅠ 고정비 줄이는 방법 없을까... 체크카드가 답인가",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "점심값 아끼는 방법",
            content: "도시락 싸기(근데 귀찮음ㅋㅋ) 아님 회사에서 간식으로 배 채우기 ㅎㅎ",
            author: "알락꼬리꼬마도요",
        },
        {
            title: "20대 자취 vs 본가 생활",
            content: "회사 근처 자취하면 돈은 드는데 시간이랑 체력은 확실히 세이브됨. 본가는 돈 굳는데 출퇴근이 에바임...",
            author: "알락꼬리꼬마도요",
        },
    ];

    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <BoardHeader
                    navigation={navigation}
                    title="HOT게시판"
                    onSearchPress={() => navigation.navigate('CommunitySearch')}
                    onWritePress={() => navigation.navigate('CommunityWrite')}
                />

                {/* 게시글 리스트 */}
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
                                onPress={() => navigation.navigate('CommunityPosts', {
                                    boardType: 'HOT',
                                    postId: index + 1
                                })}
                            />
                        ))}
                    </View>
                </ScrollView>

                {/* 메인으로 돌아가기 링크 */}
                <TouchableOpacity className="mb-8 items-center"
                    onPress={() => navigation.navigate('Home')}>
                    <Text className="text-sm text-[#6D6D6D] underline">메인으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
}