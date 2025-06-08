import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import React from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function FreeBoardScreen({ navigation }) {
    const posts = [
        {
            id: 1,
            title: "요즘 재테크 어떻게들 하세요?",
            content: "요즘 은행 금리도 낮아지고 주식도 불안정한데 다들 어떻게 재테크하시나요?",
            author: "신예성",
            likes: 24,
            comments: 15,
            createdAt: "2025-06-07T10:30:00"
        },
        {
            id: 2,
            title: "신용카드 추천 부탁드립니다",
            content: "처음으로 신용카드를 만들려고 하는데 초보자에게 좋은 카드 추천해주세요!",
            author: "신예성",
            likes: 18,
            comments: 32,
            createdAt: "2025-06-07T14:20:00"
        },
        {
            id: 3,
            title: "월급 관리 꿀팁 공유",
            content: "월급날만 되면 통장이 텅텅 비는 분들을 위한 관리 팁 공유합니다~",
            author: "김희진",
            likes: 56,
            comments: 28,
            createdAt: "2025-06-06T09:15:00"
        },
        {
            id: 4,
            title: "주식 입문자 조언 구합니다",
            content: "주식 처음 시작하는데 어떤 종목부터 공부해야 할까요? 추천 좀 부탁드립니다.",
            author: "현광수",
            likes: 32,
            comments: 41,
            createdAt: "2025-06-05T16:45:00"
        },
        {
            id: 5,
            title: "적금 vs 펀드 어떤게 좋을까요?",
            content: "목돈 마련을 위해 적금과 펀드 중 고민중인데 경험자분들 조언 부탁드려요",
            author: "김희진",
            likes: 29,
            comments: 37,
            createdAt: "2025-06-04T11:30:00"
        },
        {
            id: 6,
            title: "생활비 절약 꿀팁",
            content: "매달 생활비 절약하는 나만의 방법들 공유합니다! 식비 절약이 핵심이에요~",
            author: "오연진",
            likes: 48,
            comments: 23,
            createdAt: "2025-06-03T13:20:00"
        }
    ];

    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <BoardHeader
                    navigation={navigation}
                    title="자유게시판"
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
                                key={post.id}
                                post={post}
                                isLastItem={index === posts.length - 1}
                                onPress={() => navigation.navigate('CommunityPosts', {
                                    boardType: 'FREE',
                                    postId: post.id
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