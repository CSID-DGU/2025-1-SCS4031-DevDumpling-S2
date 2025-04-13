import { View, ScrollView, Text, Pressable } from 'react-native';
import Header from '../../components/layout/Header';

export default function CommunityHomeScreen({ navigation }) {
    const boards = [
        {
            title: "HOT게시판",
            description: "🔥 지금 가장 인기 있는 글 모음!",
        },
        {
            title: "투자게시판",
            description: "💰 투자 정보 & 전략 공유 공간",
        },
        {
            title: "챌린지게시판",
            description: "💪 챌린지 이야기를 나누며 함께 목표를 이루어요!",
        },
        {
            title: "퀴즈게시판",
            description: "🧠 금융 퀴즈 & 지식 공유 공간",
        },
        {
            title: "자유게시판",
            description: "💬 자유롭게 소통하는 공간",
            route: "FreeBoard",
        },
    ];

    return (
        <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
            {/* 상단 헤더 */}
            <Header />
            
            {/* 카테고리 탭 */}
            <View className="flex-row justify-center mb-4">
                <View className="bg-[#014029] px-4 py-2 rounded-full">
                    <Text className="text-white text-sm font-semibold">커뮤니티</Text>
                </View>
            </View>

            {/* 게시판 카드 리스트 */}
            <ScrollView className="space-y-3">
                {boards.map((board, index) => (
                    <Pressable
                        key={index}
                        className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md"
                        onPress={() => board.route && navigation.navigate(board.route)}
                    >
                        <Text className="text-base font-bold mb-1">{board.title}</Text>
                        <Text className="text-sm text-[#6D6D6D]">{board.description}</Text>
                        <Text className="text-xs text-[#6D6D6D] mt-2">알락꼬리꼬마도요</Text>
                    </Pressable>
                ))}                      
            </ScrollView>

            {/* 메인으로 돌아가기 링크 */}
            <Pressable className="mt-6 items-center">
                <Text className="text-sm text-[#6D6D6D] underline">메인으로 돌아가기</Text>
            </Pressable>
        </View>
    );
}