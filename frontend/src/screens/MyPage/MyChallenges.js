import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, Image } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { format } from 'date-fns';
import { getCategoryName } from '../Challenge/ChallengeApi';

const API_BASE_URL = 'http://52.78.59.11:8080';

const MyChallenges = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const navigation = useNavigation();
    const [userData, setUserData] = useState(null);
    const [categories, setCategories] = useState([]);
    const [challenges, setChallenges] = useState([]);
    const [personalRanks, setPersonalRanks] = useState({});
    const [loading, setLoading] = useState(true);
    const [categoryImages, setCategoryImages] = useState({});

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const storedUserData = await AsyncStorage.getItem('userData');
                if (storedUserData) {
                    setUserData(JSON.parse(storedUserData));
                }
            } catch (error) {
                console.error('사용자 데이터 불러오기 실패:', error);
            }
        };
        fetchUserData();
    }, []);

    // URL을 안전하게 처리
    const safeUri = (uri) => {
        if (!uri) return '';
        return uri.startsWith('http') ? uri : `https://${uri}`;
    };

    useEffect(() => {
        const fetchAll = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                if (!token) return;

                // 1. 카테고리 이미지 정보
                const catRes = await axios.get(`${API_BASE_URL}/api/challenges/categories`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('카테고리 데이터:', catRes.data);
                setCategories(catRes.data);

                // 2. 참여 중인 챌린지 목록
                const chalRes = await axios.get(`${API_BASE_URL}/api/challenges/participating`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('참여중인 챌린지 응답:', chalRes.data);

                const challengeList = Array.isArray(chalRes.data.content)
                    ? chalRes.data.content
                    : [];
                
                // 챌린지 ID 확인 및 로깅
                console.log('처리할 챌린지 목록:', challengeList.map(c => ({
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

                // 3. 각 챌린지별 내 랭킹
                const rankPromises = processedChallenges.map(async (challenge) => {
                    try {
                        const rankRes = await axios.get(`${API_BASE_URL}/api/challenges/${challenge.id}/personalrank`, {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        return { challengeId: challenge.id, rank: rankRes.data.rank };
                    } catch {
                        return { challengeId: challenge.id, rank: null };
                    }
                });
                const ranks = await Promise.all(rankPromises);
                const rankMap = {};
                ranks.forEach(({ challengeId, rank }) => {
                    rankMap[challengeId] = rank;
                });
                setPersonalRanks(rankMap);

            } catch (error) {
                console.error('챌린지 데이터 불러오기 실패:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchAll();
    }, []);

    // 카테고리 이미지 매핑
    useEffect(() => {
        if (challenges.length > 0 && categories.length > 0) {
            const imageMap = {};
            challenges.forEach(challenge => {
                if (!challenge.id) {
                    console.error('챌린지 ID가 없습니다:', challenge);
                    return;
                }

                console.log('처리중인 챌린지:', {
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
                        console.log('카테고리 코드로 매칭 성공:', {
                            challengeId: challenge.id,
                            challengeCategory: challenge.category,
                            matchedCategory: matched.category,
                            imageUrl: matched.imageUrl
                        });
                        imageMap[challenge.id] = matched.imageUrl;
                    } else {
                        // 2. 카테고리 이름으로 매칭
                        const categoryName = getCategoryName(challenge.category);
                        console.log('카테고리 이름 변환:', {
                            challengeId: challenge.id,
                            original: challenge.category,
                            converted: categoryName
                        });

                        const nameMatched = categories.find(
                            cat => cat.name === categoryName || cat.name === challenge.category
                        );

                        if (nameMatched?.imageUrl) {
                            console.log('카테고리 이름으로 매칭 성공:', {
                                challengeId: challenge.id,
                                challengeCategory: challenge.category,
                                matchedName: nameMatched.name,
                                imageUrl: nameMatched.imageUrl
                            });
                            imageMap[challenge.id] = nameMatched.imageUrl;
                        } else {
                            console.log('매칭 실패:', {
                                challengeId: challenge.id,
                                challengeCategory: challenge.category,
                                categoryName: categoryName,
                                availableCategories: categories.map(c => ({ name: c.name, category: c.category }))
                            });
                        }
                    }
                }
            });
            console.log('최종 카테고리 이미지 매핑:', imageMap);
            setCategoryImages(imageMap);
        }
    }, [challenges, categories]);

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
                    <Text className="text-2xl text-[#014029] font-bold mb-8">
                        {userData ? `${userData.nickname}` : '닉네임 정보 없음'}님이 참여중인 챌린지
                    </Text>

                    {loading ? (
                        <Text className="text-center text-gray-500">로딩 중...</Text>
                    ) : challenges.length === 0 ? (
                        <Text className="text-center text-gray-500">참여 중인 챌린지가 없습니다.</Text>
                    ) : (
                        challenges.map((challenge, idx) => (
                            <TouchableOpacity 
                                key={challenge.id} 
                                className="flex-row px-5 py-4 rounded-2xl items-center bg-white shadow-md mb-8"
                                onPress={() => navigation.navigate('ChallengeDetailScreen', { challengeId: challenge.id })}
                            >
                                {categoryImages[challenge.id] ? (
                                    <Image
                                        source={{ uri: safeUri(categoryImages[challenge.id]) }}
                                        className="w-10 h-10 mr-4"
                                        resizeMode="contain"
                                        onError={(e) => {
                                            console.error('이미지 로드 실패:', {
                                                challengeId: challenge.id,
                                                category: challenge.category,
                                                imageUrl: categoryImages[challenge.id]
                                            });
                                        }}
                                    />
                                ) : (
                                    <View className="w-10 h-10 bg-gray-400 rounded-full mr-4" />
                                )}
                                <View className="flex-col justify-center">
                                    <Text className="text-lg text-black font-bold mb-1">
                                        {challenge.title}
                                    </Text>
                                    <Text className="text-xs text-gray-500">
                                        {personalRanks[challenge.id] ? `현재 ${personalRanks[challenge.id]}위` : '랭킹 정보 없음'}
                                    </Text>
                                </View>
                            </TouchableOpacity>
                        ))
                    )}
                </ScrollView>
            </View>
        </>
    );
};

export default MyChallenges;
