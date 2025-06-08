import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import { useState, useEffect } from 'react';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { format } from 'date-fns';

const API_BASE_URL = 'http://52.78.59.11:8080';

// 백엔드 BoardType 열거형과 일치하도록 수정
const boardTypeMap = {
  FREE: '자유게시판',
  INVESTMENT: '투자게시판',
  CHALLENGE: '챌린지게시판',
  QUIZ: '퀴즈게시판',
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
            // 사용자 데이터와 토큰 가져오기
            const token = await AsyncStorage.getItem('userToken');
            const userDataStr = await AsyncStorage.getItem('userData');
            
            console.log('userToken:', token);
            console.log('userData:', userDataStr);
            
            if (!token) {
                console.error('토큰이 없습니다.');
                setLoading(false);
                return;
            }

            // 요청 구성
            const url = `${API_BASE_URL}/api/boards/my`;
            const config = {
                headers: { 
                    Authorization: `Bearer ${token}`,
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                timeout: 10000 // 10초 타임아웃 설정
            };
            
            console.log('내 게시글 목록 요청 URL:', url);
            console.log('요청 헤더:', JSON.stringify(config.headers));

            // API 요청 실행
            const response = await axios.get(url, config);

            console.log('내 게시글 목록 응답:', JSON.stringify(response.data));

            // 응답 데이터 처리
            if (!response.data || !response.data.content || response.data.content.length === 0) {
                console.log('게시글 데이터가 없습니다.');
                setMyPosts([]);
                return;
            }

            setMyPosts(response.data.content);
        } catch (error) {
            console.error('내 게시글 목록 조회 중 오류 발생:', error);
            
            // 에러 상세 정보 로깅
            if (error.response) {
                console.error('에러 상태:', error.response.status);
                console.error('에러 데이터:', JSON.stringify(error.response.data));
                console.error('에러 헤더:', JSON.stringify(error.response.headers));
            } else if (error.request) {
                console.error('요청은 전송되었으나 응답이 없음:', error.request);
            } else {
                console.error('요청 설정 중 오류 발생:', error.message);
            }
            
            // 오류 발생 시 빈 배열로 초기화
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
                            <View 
                                key={index}
                                className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md mb-4"
                            >
                                {/* <Text className="text-xs text-gray-500 mb-1">
                                    {boardTypeMap[post.board_type] || '일반'}
                                </Text> */}
                                <Text className="text-base font-bold mb-2" numberOfLines={2}>{post.title}</Text>
                                <Text className="text-sm text-black mb-2">{post.content}</Text>
                                <Text className="text-xs text-gray-500">{format(new Date(post.createdAt), 'yyyy-MM-dd HH:mm')}</Text>
                            </View>
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
