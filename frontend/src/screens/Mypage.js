import React, { useState, useEffect } from 'react';
import { View, Text, Image, TouchableOpacity, SafeAreaView, ScrollView, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Header from '../components/layout/Header';

export default function MyPageScreen() {
    const navigation = useNavigation();
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);

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

    const handleLogout = async () => {
        try {
            await AsyncStorage.removeItem('userToken');
            await AsyncStorage.removeItem('userData');
            navigation.reset({
                index: 0,
                routes: [{ name: 'Login' }],
            });
        } catch (error) {
            console.error('로그아웃 실패:', error);
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
                <ScrollView className="flex-1">
                    {/* 프로필 섹션 */}
                    <View className="px-5 pt-3 pb-5">
                        <View className="flex-row items-center space-x-3">
                            <View className="w-[35%] flex items-center justify-center">
                                <Image
                                    source={{ uri: userData?.profileImage }}
                                    className="w-[86px] h-[86px] rounded-full bg-gray-200"
                                />
                            </View>
                            <View className="w-[65%] bg-white py-4 px-5 rounded-3xl shadow-sm">
                                <Text className="text-center text-base font-bold mb-1 text-Fineed-green">
                                    {userData ? `${userData.nickname}님,` : '닉네임 정보 없음'}
                                </Text>
                                <Text className="text-center text-base font-bold mb-2 text-Fineed-green">
                                    반가워요!
                                </Text>
                                <View className="flex-row items-center justify-center mb-2">
                                    <Image
                                        source={require('../../assets/images/badge.png')}
                                        className="w-[22px] h-[22px] mr-1"
                                        resizeMode="contain"
                                    />
                                    <Text className="font-bold mr-1">LV.2</Text>
                                    <Text className="mx-1 text-gray-400">|</Text>
                                    <Text className="text-xs text-gray-500">
                                        챌린지 달성 12회
                                    </Text>
                                </View>
                                <View className="h-1 bg-gray-200 rounded-full overflow-hidden mb-1">
                                    <View className="h-full bg-Fineed-green w-[60%]" />
                                </View>
                                <View className="flex-row justify-between px-1">
                                    <Text className="text-xs text-gray-500">LV.2</Text>
                                    <Text className="text-xs text-gray-500">LV.3</Text>
                                </View>
                            </View>
                        </View>
                    </View>

                    {/* 챌린지 섹션 */}
                    <View className="px-5 mb-4">
                        <TouchableOpacity
                            className="flex-row items-center mb-3"
                            onPress={() => navigation.navigate('Challenges')}
                        >
                            <Text className="text-2xl font-bold text-Fineed-green">참여중인 챌린지</Text>
                            <Text className="text-2xl font-bold text-Fineed-green ml-1">{'>'}</Text>
                        </TouchableOpacity>

                        <TouchableOpacity
                            className="bg-white rounded-3xl shadow-sm overflow-hidden mb-3"
                            onPress={() => navigation.navigate('ChallengeDetail')}
                        >
                            <View className="flex-row p-5">
                                <View className="w-[36%] flex items-center justify-center pr-3">
                                    <Image
                                        source={require('../../assets/images/piggy-bank.png')}
                                        className="w-24 h-24"
                                        resizeMode="contain"
                                    />
                                </View>
                                <View className="h-24 w-px bg-gray-200 mr-3" />
                                <View className="w-[64%] flex justify-center  pl-1">
                                    <Text className="text-center text-2xl font-bold mb-1 text-Fineed-green">도전!</Text>
                                    <Text className="text-center text-2xl font-bold text-Fineed-green">일주일 저축 챌린지</Text>
                                </View>
                            </View>
                            <View className="flex-row justify-center mt-3 mb-3">
                                <View className="h-2 w-2 rounded-full bg-Fineed-green mx-1" />
                                <View className="h-2 w-2 rounded-full bg-gray-300 mx-1" />
                                <View className="h-2 w-2 rounded-full bg-gray-300 mx-1" />
                                <View className="h-2 w-2 rounded-full bg-gray-300 mx-1" />
                                <View className="h-2 w-2 rounded-full bg-gray-300 mx-1" />
                            </View>
                        </TouchableOpacity>
                    </View>

                    {/* 메뉴 버튼 */}
                    <View className="px-4 mb-8">
                        <View className="flex-row mb-4">
                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 mr-3"
                                onPress={() => navigation.navigate('Scraps')}
                            >
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">스크랩·</Text>
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">찜 목록</Text>
                                <Text className="text-2xl font-bold text-Fineed-green">보러 가기 {'>'}</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8"
                                onPress={() => navigation.navigate('MyPosts')}
                            >
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">내가 작성한</Text>
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">글·댓글</Text>
                                <Text className="text-2xl font-bold text-Fineed-green">보러 가기 {'>'}</Text>
                            </TouchableOpacity>
                        </View>

                        <View className="flex-row">
                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8 mr-3"
                                onPress={() => navigation.navigate('Badges')}
                            >
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">획득한 뱃지</Text>
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">확인하기 {'>'}</Text>
                                <Text className="text-sm text-gray-500 mt-2">총 5개</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="flex-1 bg-white rounded-3xl shadow-sm p-8"
                                onPress={() => navigation.navigate('QuizHistory')}
                            >
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">퀴즈 기록</Text>
                                <Text className="text-2xl font-bold mb-0.5 text-Fineed-green">보러 가기 {'>'}</Text>
                                <Text className="text-sm text-gray-500 mt-2">브론즈 티어 · 9회 완료</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                </ScrollView>
            </SafeAreaView>
        </>
    );
}