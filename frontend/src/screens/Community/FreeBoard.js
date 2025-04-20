import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, Touchable } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';


export default function FreeBoardScreen({ navigation }) {
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

                {/* 카테고리 탭 */}
                <View
                    className="flex-row items-center justify-between bg-[#014029] rounded-2xl mb-4"
                    style={{
                        paddingVertical: 12,
                        marginHorizontal: horizontalPadding,
                    }}>
                    {/* 뒤로 가기 버튼 */}
                    <TouchableOpacity onPress={() => navigation.goBack()} className="ml-4">
                        <Icon name="arrow-back-outline" size={24} color="#EFEFEF" />
                    </TouchableOpacity>
                    {/* 중앙 타이틀 */}
                    <Text className="text-white text-sm font-semibold">자유게시판</Text>
                    {/* 오른쪽 아이콘들 */}
                    <View className="flex-row mr-4">
                        <TouchableOpacity
                            onPress={() => navigation.navigate('CommunitySearch')}
                            style={{ marginRight: 15 }}>
                            <Icon name="search-outline" size={22} color="#EFEFEF" />
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => navigation.navigate('FreeBoardWrite')}>
                            <Icon name="create-outline" size={22} color="#EFEFEF" />
                        </TouchableOpacity>
                    </View>
                </View>

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
                            <View key={index}>
                                <TouchableOpacity className="px-4 py-4">
                                    <Text
                                        className="font-semibold text-black text-sm"
                                        numberOfLines={1}
                                    > {post.title} </Text>
                                    <Text
                                        className="text-xs text-xs text-gray-600 mt-1"
                                        numberOfLines={2}
                                    > {post.content} </Text>
                                    <Text className="text-[10px] text-gray-400 mt-1"> {post.author} </Text>
                                </TouchableOpacity>

                                {/* 항목 사이에만 구분선 */}
                                {index < posts.length - 1 && (
                                    <View className="border-t border-gray-200 mx-4" />
                                )}
                            </View>
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