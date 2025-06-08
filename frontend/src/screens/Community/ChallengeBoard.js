import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import React from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';

export default function ChallengeBoardScreen({ navigation }) {
    const posts = [
        {
            title: '이번 달 절약 챌린지 참가자 모집!',
            content: '한 달 동안 커피값 절반 줄이기 도전할 분?',
            author: '절약왕',
        },
        {
            title: '30일 저녁 노외식 챌린지 후기',
            content: '혼자 해보니 힘들지만 효과는 확실했어요!',
            author: '노외식러',
        },
        {
            title: '자취생 식비 15만원 챌린지 팁 공유',
            content: '장보기 루틴과 레시피를 공유합니다.',
            author: '자취꿀팁러',
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
                    title="챌린지게시판"
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
                                    boardType: 'CHALLENGE',
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