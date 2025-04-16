import React, { useState, useEffect } from 'react';
import { View, Text, Image, TouchableOpacity, SafeAreaView, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';

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
        <SafeAreaView className="flex-1 bg-Fineed-background">
            <View className="px-5 py-8">
                <Text className="text-3xl font-bold text-Fineed-green mb-8">마이페이지</Text>

                {userData ? (
                    <View className="bg-white rounded-xl p-6 shadow-sm">
                        <View className="flex-row items-center mb-6">
                            {userData.profileImage ? (
                                <Image
                                    source={{ uri: userData.profileImage }}
                                    className="w-20 h-20 rounded-full mr-4"
                                />
                            ) : (
                                <View className="w-20 h-20 rounded-full bg-gray-200 mr-4 items-center justify-center">
                                    <Text className="text-2xl text-gray-400">프로필</Text>
                                </View>
                            )}

                            <View>
                                <Text className="text-xl font-bold">{userData.nickname || '닉네임 정보 없음'}</Text>
                                <Text className="text-gray-500">{userData.email || '이메일 정보 없음'}</Text>
                            </View>
                        </View>

                        <View className="border-t border-gray-200 pt-4">
                            <TouchableOpacity
                                className="py-3"
                                onPress={() => console.log('계정 설정')}
                            >
                                <Text className="text-base">계정 설정</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="py-3"
                                onPress={() => console.log('알림 설정')}
                            >
                                <Text className="text-base">알림 설정</Text>
                            </TouchableOpacity>

                            <TouchableOpacity
                                className="py-3"
                                onPress={() => console.log('문의하기')}
                            >
                                <Text className="text-base">문의하기</Text>
                            </TouchableOpacity>
                        </View>

                        <TouchableOpacity
                            className="mt-6 bg-Fineed-green py-3 rounded-full items-center"
                            onPress={handleLogout}
                        >
                            <Text className="text-white font-bold">로그아웃</Text>
                        </TouchableOpacity>
                    </View>
                ) : (
                    <View className="items-center justify-center py-10">
                        <Text className="text-lg text-gray-500">로그인이 필요합니다</Text>
                        <TouchableOpacity
                            className="mt-4 bg-Fineed-green py-3 px-6 rounded-full"
                            onPress={() => navigation.navigate('Login')}
                        >
                            <Text className="text-white font-bold">로그인하기</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>
        </SafeAreaView>
    );
}