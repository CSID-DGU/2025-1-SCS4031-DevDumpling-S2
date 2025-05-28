import React, { useEffect } from 'react';
import { View, Text, SafeAreaView, TouchableOpacity, Image } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Header from '../components/layout/Header';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://52.78.59.11:8080';

const MyDataComplete = () => {
    const navigation = useNavigation();

    // 유저 타입 가져오기
    const getUserType = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken');
            if (!token) return;

            // 유저 타입 설정 API 호출
            const response = await axios.get(`${API_BASE_URL}/users/user-type`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            console.log('유저타입 API 응답:', response.data);

            // 최신 유저 정보 가져오기
            const userData = JSON.parse(await AsyncStorage.getItem('userData') || '{}');

            // 유저 타입 업데이트
            userData.userType = response.data.userType;
            await AsyncStorage.setItem('userData', JSON.stringify(userData));

            console.log('업데이트된 유저 정보:', userData);
        } catch (error) {
            console.error('유저 타입 설정 중 오류 발생:', error);
            if (error.response) {
                console.error('에러 상태:', error.response.status);
                console.error('에러 데이터:', error.response.data);
            }
        }
    };

    // 컴포넌트 마운트 시 유저 타입 설정
    useEffect(() => {
        getUserType();
    }, []);

    const handleGoToMain = async () => {
        try {
            // 최신 유저 정보로 업데이트 후 홈으로 이동
            await getUserType();
        } catch (error) {
            console.error('유저 정보 업데이트 실패:', error);
        }

        navigation.reset({
            index: 0,
            routes: [{ name: 'Home' }]
        });
    };

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-background">
                <View className="flex-1 justify-center items-center px-6">
                    <View className="w-16 h-16 rounded-full bg-Fineed-green mb-6 items-center justify-center">
                        <Text className="text-white text-3xl">✓</Text>
                    </View>

                    <Text className="text-2xl font-bold text-center mb-2">
                        마이데이터 연동 완료!
                    </Text>

                    <Text className="text-base text-center mb-8 text-gray-600">
                        FINEED와 함께{"\n"}새로운 금융 습관을 시작해보세요 🙂
                    </Text>

                    <TouchableOpacity
                        className="py-3 px-10 rounded-full bg-Fineed-green w-full"
                        onPress={handleGoToMain}
                    >
                        <Text className="text-white text-center font-bold">
                            메인으로
                        </Text>
                    </TouchableOpacity>
                </View>
            </SafeAreaView>
        </>
    );
};

export default MyDataComplete;