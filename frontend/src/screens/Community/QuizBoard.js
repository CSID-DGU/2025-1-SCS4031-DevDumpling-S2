import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, ActivityIndicator } from 'react-native';
import React, { useEffect, useState } from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';
import { fetchBoardPosts } from './CommunityApi';

export default function QuizBoardScreen({ navigation }) {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    useEffect(() => {
        fetchBoardPosts('QUIZ')
            .then(data => {
                console.log('퀴즈 게시판 API 응답:', data);
                setPosts(data.content);
            })
            .catch(err => {
                setPosts([]);
            })
            .finally(() => setLoading(false));
    }, []);

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
                        {loading ? (
                            <ActivityIndicator />
                        ) : (
                            posts.map((post, index) => (
                                <BoardPostItem
                                    key={post.id}
                                    post={post}
                                    isLastItem={index === posts.length - 1}
                                    onPress={() => navigation.navigate('CommunityPosts', {
                                        boardType: 'QUIZ',
                                        postId: post.id
                                    })}
                                />
                            ))
                        )}
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