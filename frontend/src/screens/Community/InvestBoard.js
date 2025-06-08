import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import React from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function InvestBoardScreen({ navigation }) {
    const posts = [
        {
            title: '주식초보의 첫 투자 고민',
            content: '우량주 vs 성장주, 어떤 걸 먼저 공부해야 할까요?',
            author: '주린이',
        },
        {
            title: 'ETF란 무엇인가요?',
            content: 'ETF 기초 개념부터 종류까지 쉽게 알려주세요!',
            author: '투자궁금',
        },
        {
            title: '월급 200, 투자 포트폴리오 추천',
            content: '적금·펀드·주식 비중을 어떻게 가져가야 할까요?',
            author: '200벌이',
        },
        {
            title: '배당주 추천 부탁드립니다',
            content: '안정적인 배당 수익 노리는 중입니다.',
            author: '배당러',
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
                    title="투자게시판"
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
                                onPress={() => navigation.navigate('CommunityPosts', {
                                    boardType: 'INVESTMENT',
                                    postId: index + 1,
                                })}
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