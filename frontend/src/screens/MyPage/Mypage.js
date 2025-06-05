import React, { useState, useEffect } from 'react';
import { View, Text, Image, TouchableOpacity, SafeAreaView, ScrollView, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Header from '../../components/layout/Header';
import axios from 'axios';
import { getCategoryName } from '../Challenge/ChallengeApi';

export default function MypageScreen() {
    const navigation = useNavigation();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [categories, setCategories] = useState([]);
    const [challenges, setChallenges] = useState([]);
    const [challengeLoading, setChallengeLoading] = useState(true);
    const [categoryImages, setCategoryImages] = useState({});

    // URL을 안전하게 처리
    const safeUri = (uri) => {
        if (!uri) return '';
        return uri.startsWith('http') ? uri : `https://${uri}`;
    };

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

    useEffect(() => {
        const fetchChallenges = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                if (!token) return;

                // 카테고리 정보
                const catRes = await axios.get('http://52.78.59.11:8080/api/challenges/categories', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('Mypage - 카테고리 데이터:', catRes.data);
                setCategories(catRes.data);

                // 참여중인 챌린지 목록
                const chalRes = await axios.get('http://52.78.59.11:8080/api/challenges/participating', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('Mypage - 참여중인 챌린지 응답:', chalRes.data);

                const challengeList = Array.isArray(chalRes.data.content)
                    ? chalRes.data.content
                    : [];
                
                // 챌린지 ID 확인 및 로깅
                console.log('Mypage - 처리할 챌린지 목록:', challengeList.map(c => ({
                    id: c.id,
                    challengeId: c.challengeId,
                    title: c.title,
                    category: c.category
                })));

                // id 또는 challengeId가 있는지 확인하고 설정
                const processedChallenges = challengeList.map(challenge => ({
                    ...challenge,
                    id: challenge.id || challenge.challengeId
                }));

                setChallenges(processedChallenges);

            } catch (error) {
                console.error('챌린지 데이터 불러오기 실패:', error);
            } finally {
                setChallengeLoading(false);
            }
        };
        fetchChallenges();
    }, []);

    // 카테고리 이미지 매핑
    useEffect(() => {
        if (challenges.length > 0 && categories.length > 0) {
            const imageMap = {};
            challenges.forEach(challenge => {
                if (!challenge.id) {
                    console.error('Mypage - 챌린지 ID가 없습니다:', challenge);
                    return;
                }

                console.log('Mypage - 처리중인 챌린지:', {
                    id: challenge.id,
                    category: challenge.category,
                    title: challenge.title
                });

                if (challenge.category) {
                    // 1. 카테고리 코드로 직접 매칭
                    const matched = categories.find(
                        cat => cat.category === challenge.category
                    );
                    
                    if (matched?.imageUrl) {
                        console.log('Mypage - 카테고리 코드로 매칭 성공:', {
                            challengeId: challenge.id,
                            challengeCategory: challenge.category,
                            matchedCategory: matched.category,
                            imageUrl: matched.imageUrl
                        });
                        imageMap[challenge.id] = matched.imageUrl;
                    } else {
                        // 2. 카테고리 이름으로 매칭
                        const categoryName = getCategoryName(challenge.category);
                        console.log('Mypage - 카테고리 이름 변환:', {
                            challengeId: challenge.id,
                            original: challenge.category,
                            converted: categoryName
                        });

                        const nameMatched = categories.find(
                            cat => cat.name === categoryName || cat.name === challenge.category
                        );

                        if (nameMatched?.imageUrl) {
                            console.log('Mypage - 카테고리 이름으로 매칭 성공:', {
                                challengeId: challenge.id,
                                challengeCategory: challenge.category,
                                matchedName: nameMatched.name,
                                imageUrl: nameMatched.imageUrl
                            });
                            imageMap[challenge.id] = nameMatched.imageUrl;
                        } else {
                            console.log('Mypage - 매칭 실패:', {
                                challengeId: challenge.id,
                                challengeCategory: challenge.category,
                                categoryName: categoryName,
                                availableCategories: categories.map(c => ({ name: c.name, category: c.category }))
                            });
                        }
                    }
                }
            });
            console.log('Mypage - 최종 카테고리 이미지 매핑:', imageMap);
            setCategoryImages(imageMap);
        }
    }, [challenges, categories]);

    const handleLogout = async () => {
        try {
            // 모든 사용자 관련 데이터 삭제
            await AsyncStorage.multiRemove(['userData', 'userToken']);
            setUserData(null);
            console.log('로그아웃 완료: 모든 사용자 데이터 삭제됨');
            navigation.reset({
                index: 0,
                routes: [{ name: 'Login' }],
            });
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
        }
    };

    if (loading) {
        return (
            <SafeAreaView className="flex-1 bg-Fineed-background items-center justify-center">
                <ActivityIndicator size="large" color="#014029" />
            </SafeAreaView>
        );
    }

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-Fineed-background">
                <ScrollView className="flex-1 p-5">
                    {/* 프로필 섹션 */}
                    <View className="px-5 pt-3 pb-5">
                        <View className="flex-row items-center justify-between space-x-3">
                            <View className="w-[35%] flex items-center justify-center">
                                <Image
                                    source={{ uri: userData?.profileImage }}
                                    className="w-[100px] h-[100px] rounded-full bg-gray-200"
                                />
                            </View>
                            <View className="w-[60%] bg-white py-4 px-5 rounded-3xl shadow-sm">
                                <Text className="text-center text-lg font-bold mb-1 text-Fineed-green mt-2">
                                    {userData ? `${userData.nickname}님,` : '닉네임 정보 없음'}
                                </Text>
                                <Text className="text-center text-lg font-bold mb-2 text-Fineed-green">
                                    반가워요!
                                </Text>
                                <TouchableOpacity
                                    className="flex-row items-center justify-center mt-2"
                                    onPress={() => navigation.navigate('MyDataConsent')}>
                                        <Text
                                            className="text-center text-sm font-bold mb-2 text-Fineed-green"
                                            style={{ textDecorationLine: 'underline' }}>마이데이터 변경하기 {'>'}</Text>
                                </TouchableOpacity>
                            </View>
                        </View>
                    </View>

                    {/* 챌린지 섹션 */}
                    <View className="px-5 mb-4">
                        <TouchableOpacity
                            className="flex-row items-center mb-3"
                            onPress={() => navigation.navigate('MyChallenges')}
                        >
                            <Text className="text-2xl font-bold text-Fineed-green">참여중인 챌린지</Text>
                            <Text className="text-2xl font-bold text-Fineed-green ml-1">{'>'}</Text>
                        </TouchableOpacity>

                        {challengeLoading ? (
                            <Text className="text-center text-gray-500">로딩 중...</Text>
                        ) : challenges.length === 0 ? (
                            <View className="bg-white rounded-3xl shadow-sm overflow-hidden mb-3 p-8 items-center">
                                <Text className="text-lg text-gray-500">아직 참여중인 챌린지가 없어요</Text>
                            </View>
                        ) : (
                            <TouchableOpacity
                                className="bg-white rounded-3xl shadow-sm overflow-hidden mb-3"
                                onPress={() => {
                                    console.log('Mypage - 챌린지 데이터:', challenges[0]);
                                    const challengeId = challenges[0]?.challengeId || challenges[0]?.id;
                                    console.log('Mypage - 사용할 challengeId:', challengeId);
                                    if (challengeId) {
                                        navigation.navigate('ChallengeDetailScreen', { challengeId });
                                    } else {
                                        console.error('Mypage - challengeId가 없습니다:', challenges[0]);
                                    }
                                }}
                            >
                                <View className="flex-row p-5">
                                    <View className="w-[36%] flex items-center justify-center pr-3">
                                        {categoryImages[challenges[0].id] ? (
                                            <Image
                                                source={{ uri: safeUri(categoryImages[challenges[0].id]) }}
                                                className="w-14 h-14"
                                                resizeMode="contain"
                                                onError={(e) => {
                                                    console.error('Mypage - 이미지 로드 실패:', {
                                                        challengeId: challenges[0].id,
                                                        category: challenges[0].category,
                                                        imageUrl: categoryImages[challenges[0].id]
                                                    });
                                                }}
                                            />
                                        ) : (
                                            <View className="w-14 h-14 bg-gray-200 rounded-full" />
                                        )}
                                    </View>
                                    <View className="h-24 w-px bg-gray-200 mr-3" />
                                    <View className="w-[64%] flex justify-center pl-1">
                                        <Text className="text-center text-xl font-bold text-Fineed-green">{challenges[0].title}</Text>
                                    </View>
                                </View>
                            </TouchableOpacity>
                        )}
                    </View>

                    {/* 메뉴 버튼 */}
                    <View className="px-4 mb-8">
                        <View className="flex-row mb-4">
                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 mr-3 h-44"
                                onPress={() => navigation.navigate('Scraps')}
                            >
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">스크랩한</Text>
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">기사</Text>
                                <Text className="text-xl font-bold text-Fineed-green">보러 가기 {'>'}</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 h-44"
                                onPress={() => navigation.navigate('MyPosts')}
                            >
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">내가 작성한</Text>
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">게시글</Text>
                                <Text className="text-xl font-bold text-Fineed-green">보러 가기 {'>'}</Text>
                            </TouchableOpacity>
                        </View>

                        <View className="flex-row">
                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 mr-3 h-44"
                                onPress={() => navigation.navigate('MyType')}
                            >
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">내 금융 유형</Text>
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">확인하기 {'>'}</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 h-44"
                                onPress={() => navigation.navigate('QuizHistory')}
                            >
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">퀴즈 기록</Text>
                                <Text className="text-xl font-bold mb-0.5 text-Fineed-green">보러 가기 {'>'}</Text>
                            </TouchableOpacity>
                        </View>
                    </View>

                    {/* 로그아웃 버튼 */}
                    <View className="px-5 mb-10 mt-3">
                        <TouchableOpacity
                            className="bg-white rounded-3xl shadow-md p-3 items-center"
                            onPress={handleLogout}
                        >
                            <Text className="text-2xl font-bold text-Fineed-green">로그아웃</Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
            </SafeAreaView>
        </>
    );
}