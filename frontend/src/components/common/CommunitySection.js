import { View, Text, TouchableOpacity, useWindowDimensions } from 'react-native';

export default function CommunitySection() {
    const { width } = useWindowDimensions();

    const posts = [
        ['HOT게시판', '한달 50만원으로 살아남기 챌린지 꿀팁ㄷㄷ'],
        ['투자게시판', '2차전지 아직 늦지 않았을까?'],
        ['챌린지게시판', '무지출 챌린지 하면 진짜 돈 모임?'],
        ['퀴즈게시판', '신용점수 퀴즈 틀렸는데 점수 미쳤음'],
        ['자유게시판', '아무도 안물어봤지만 내 최애 재테크 방법'],
    ];

    return (
        <View className="bg-white rounded-[15px] p-5 shadow-md mt-3 mb-8">
            {posts.map(([title, sub], idx) => (
                <TouchableOpacity
                    key={idx}
                    className={idx < posts.length - 1 ? "py-3 border-b border-[#F0F0F0]" : "py-3"}
                    style={{ paddingHorizontal: width > 380 ? 5 : 2 }}
                >
                    <Text className="text-[14px] font-bold text-black">{title}</Text>
                    <Text className="text-[12px] text-[#6D6D6D] mt-2">{sub}</Text>
                </TouchableOpacity>
            ))}
        </View>
    );
}
