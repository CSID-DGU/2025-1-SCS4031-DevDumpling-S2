import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import { useState, useEffect } from 'react';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { format } from 'date-fns';

const API_BASE_URL = 'http://52.78.59.11:8080';

const boardTypeMap = {
  HOT: 'HOT게시판',
  INVEST: '투자게시판',
  CHALLENGE: '챌린지게시판',
  QUIZ: '퀴즈게시판',
  FREE: '자유게시판',
};

const MyPosts = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const navigation = useNavigation();
    console.log('navigation:', navigation);
    const [myPosts, setMyPosts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userData, setUserData] = useState(null);

    useEffect(() => {
        fetchMyPosts();
    }, []);

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const storedUserData = await AsyncStorage.getItem('userData');
                if (storedUserData) {
                    setUserData(JSON.parse(storedUserData));
                }
            } catch (error) {
                console.error('사용자 데이터 불러오기 실패:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, []);

    const fetchMyPosts = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken');
            if (!token) {
                console.error('토큰이 없습니다.');
                return;
            }

            console.log('요청 URL:', `${API_BASE_URL}/api/boards/my`);
            console.log('토큰:', token);

            const response = await axios.get(`${API_BASE_URL}/api/boards/my`, {
                headers: { 
                    Authorization: `Bearer ${token}`,
                    'Accept': 'application/json'
                }
            });

            console.log('내 게시글 목록:', response.data);

            if (!response.data || !response.data.content || response.data.content.length === 0) {
                setMyPosts([]);
                setLoading(false);
                return;
            }

            setMyPosts(response.data.content);
        } catch (error) {
            console.error('내 게시글 목록 조회 중 오류 발생:', error);
            if (error.response) {
                console.error('에러 상태:', error.response.status);
                console.error('에러 데이터:', error.response.data);
                console.error('에러 헤더:', error.response.headers);
            }
            setMyPosts([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    <Text className="text-2xl text-[#014029] font-bold mb-8">{userData ? `${userData.nickname}` : '닉네임 정보 없음'}님이 작성한 게시글</Text>
                    {loading ? (
                        <Text className="text-center text-gray-500">로딩 중...</Text>
                    ) : myPosts.length > 0 ? (
                        myPosts.map((post, index) => (
                            <TouchableOpacity 
                                key={index}
                                onPress={() => {
                                    const boardType = post.board_type || 'FREE'; // 기본값으로 FREE 설정
                                    navigation.navigate('CommunityPosts', { 
                                        boardType: boardType, 
                                        postId: post.id 
                                    });
                                }}
                                className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md mb-4"
                            >
                                {/* <Text className="text-xs text-gray-500 mb-1">
                                    {boardTypeMap[post.board_type] || '일반'}
                                </Text> */}
                                <Text className="text-base font-bold mb-2" numberOfLines={2}>{post.title}</Text>
                                <Text className="text-xs text-gray-500">{format(new Date(post.createdAt), 'yyyy-MM-dd HH:mm')}</Text>
                            </TouchableOpacity>
                        ))
                    ) : (
                        <Text className="text-center text-gray-500">작성한 게시글이 없습니다.</Text>
                    )}
                </ScrollView>
            </View>
        </>
    );
};

export default MyPosts;
