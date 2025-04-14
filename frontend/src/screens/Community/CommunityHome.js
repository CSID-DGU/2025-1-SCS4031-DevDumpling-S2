import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';


export default function CommunityHomeScreen({ navigation }) {
    const boards = [
        {
            title: "HOT게시판",
            description: "🔥 지금 가장 인기 있는 글 모음!",
            route: "HotBoard",
        },
        {
            title: "투자게시판",
            description: "💰 투자 정보 & 전략 공유 공간",
            route: "InvestBoard",
        },
        {
            title: "챌린지게시판",
            description: "💪 챌린지 이야기를 나누며 함께 목표를 이루어요!",
            route: "ChallengeBoard",
        },
        {
            title: "퀴즈게시판",
            description: "🧠 금융 퀴즈 & 지식 공유 공간",
            route: "QuizBoard",
        },
        {
            title: "자유게시판",
            description: "💬 자유롭게 소통하는 공간",
            route: "FreeBoard",
        },
    ];
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">

                {/* 카테고리 탭 */}
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">커뮤니티</Text>
                    </View>
                </View>

                {/* 게시판 카드 리스트 */}
                <ScrollView
                    className="flex-1"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                    }}>
                    {boards.map((board, index) => (
                        <TouchableOpacity
                            key={index}
                            className={`bg-[#F9F9F9] p-4 rounded-2xl shadow-md ${index !== boards.length - 1 ? "mb-5" : ""}`}
                            onPress={() => board.route && navigation.navigate(board.route)}
                        >
                            <Text className="text-base font-bold mb-1">{board.title}</Text>
                            <Text className="text-sm text-[#6D6D6D]">{board.description}</Text>
                        </TouchableOpacity>
                    ))}
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