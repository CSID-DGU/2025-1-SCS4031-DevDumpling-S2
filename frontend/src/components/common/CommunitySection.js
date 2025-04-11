import { View, Text, TouchableOpacity } from 'react-native';

export default function CommunitySection() {
    const posts = [
        ['HOT게시판', '일일 50만원으로 살아남기 챌린지 꿀팁ㅌㅌㅌ'],
        ['투자게시판', '2차전지 어제 논 왔었음?!?'],
        ['챌린지게시판', '무지출 챌린지 혹시 인증 도 모아야?'],
        ['퀴즈게시판', '신용점수 퀴즈 틀렸는데 증격 발표;;;'],
        ['자유게시판', '아무도 안물어봤지만 내 취미 재테크 방법'],
    ];

    return (
        <View className="bg-white rounded-[15px] p-4 shadow-md mt-2 mb-6">
            {posts.map(([title, sub], idx) => (
                <TouchableOpacity key={idx} className={idx < posts.length - 1 ? "py-3 border-b border-[#F0F0F0]" : "py-3"}>
                    <Text className="text-[14px] font-bold text-black">{title}</Text>
                    <Text className="text-[12px] text-[#6D6D6D] mt-1">{sub}</Text>
                </TouchableOpacity>
            ))}
        </View>
    );
}
