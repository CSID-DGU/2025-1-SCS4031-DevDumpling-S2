import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, ActivityIndicator } from 'react-native';
import React, { useEffect, useState } from 'react';
import Header from '../../components/layout/Header';
import BoardHeader from '../../components/common/BoardHeader';
import BoardPostItem from '../../components/common/BoardPostItem';
import { fetchBoardPosts } from './CommunityApi';

export default function FreeBoardScreen({ navigation }) {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    useEffect(() => {
        fetchBoardPosts('FREE') // ✅ 대문자로 수정
            .then(data => {
                console.log('📦 받아온 게시글 목록:', data);
                setPosts(data);
            })
            .catch(err => {
                console.error('❌ 게시글 목록 불러오기 실패:', err);
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
                        {loading ? (
                            <ActivityIndicator />
                        ) : (
                            posts.map((post, index) => (
                                <BoardPostItem
                                    key={post.id}
                                    post={post}
                                    isLastItem={index === posts.length - 1}
                                    onPress={() => navigation.navigate('CommunityPosts', {
                                        boardType: 'FREE',
                                        postId: post.id
                                    })}
                                />
                            ))
                        )}
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