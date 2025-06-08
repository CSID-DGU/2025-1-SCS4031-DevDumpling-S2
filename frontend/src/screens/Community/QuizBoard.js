import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import React from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function QuizBoardScreen({ navigation }) {
    const posts = [
        {
            title: '경제 상식 퀴즈: 주식 용어 맞히기',
            content: 'PER, PBR, EPS의 뜻은 무엇일까요?',
            author: '퀴즈마스터',
        },
        {
            title: '가계부 퀴즈: 월평균 생활비는?',
            content: '대한민국 20대 평균 생활비를 맞혀보세요!',
            author: '생활퀴즈러',
        },
        {
            title: '투자 상식 OX 퀴즈',
            content: '분산투자는 위험을 줄여줄까?',
            author: 'OX퀴즈러',
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
                                onPress={() => navigation.navigate('CommunityPosts', {
                                    boardType: 'QUIZ',
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