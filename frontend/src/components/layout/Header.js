import { View, Text, TouchableOpacity, Alert } from 'react-native';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import React, { useState, useCallback, useEffect } from 'react';

const Header = () => {
    const navigation = useNavigation();
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userName, setUserName] = useState('');

    // 로그인 상태 확인 함수
    const checkLoginStatus = async () => {
        try {
            const userToken = await AsyncStorage.getItem('userToken');
            const userData = await AsyncStorage.getItem('userData');

            if (userToken) {
                setIsLoggedIn(true);

                // 사용자 이름 설정 (있는 경우)
                if (userData) {
                    const user = JSON.parse(userData);
                    setUserName(user.nickname || '');
                }
            } else {
                setIsLoggedIn(false);
                setUserName('');
            }
        } catch (error) {
            console.error('로그인 상태 확인 오류:', error);
            setIsLoggedIn(false);
        }
    };

    // 로그아웃 함수
    const handleLogout = async () => {
        try {
            Alert.alert(
                '로그아웃',
                '정말 로그아웃 하시겠습니까?',
                [
                    {
                        text: '취소',
                        style: 'cancel'
                    },
                    {
                        text: '로그아웃',
                        onPress: async () => {
                            // AsyncStorage에서 토큰과 사용자 정보 삭제
                            await AsyncStorage.removeItem('userToken');
                            await AsyncStorage.removeItem('userData');
                            setIsLoggedIn(false);
                            setUserName('');

                            // 선택적: 로그아웃 성공 메시지 표시
                            Alert.alert('로그아웃 되었습니다.');
                        }
                    }
                ]
            );
        } catch (error) {
            console.error('로그아웃 오류:', error);
            Alert.alert('로그아웃 중 오류가 발생했습니다.');
        }
    };

    // 화면이 포커스될 때마다 로그인 상태 확인
    useFocusEffect(
        useCallback(() => {
            checkLoginStatus();
        }, [])
    );

    // 컴포넌트 마운트 시 최초 로그인 상태 확인
    useEffect(() => {
        checkLoginStatus();
    }, []);

    return (
        <View className="flex-row justify-between items-center px-[27px] pt-[57px] pb-[12px]">
            <Text
                style={{ fontFamily: 'Pretendard-ExtraBold' }}
                className="text-[28px] leading-[36px] text-[#014029]"
            >
                FINEED
            </Text>

            {isLoggedIn ? (
                // 로그인된 경우 사용자 이름과 로그아웃 버튼 표시
                <View className="flex-row items-center">
                    {userName ? (
                        <Text
                            style={{ fontFamily: 'Pretendard-Regular' }}
                            className="text-[14px] leading-[22px] text-black mr-3"
                        >
                            {userName}님
                        </Text>
                    ) : null}

                    <TouchableOpacity onPress={() => navigation.navigate('Mypage')}>
                        <Text
                            style={{ fontFamily: 'Pretendard-Regular' }}
                            className="text-[14px] leading-[22px] text-black"
                        >
                            마이페이지
                        </Text>
                    </TouchableOpacity>

                    <TouchableOpacity onPress={handleLogout}>
                        <Text
                            style={{ fontFamily: 'Pretendard-Regular' }}
                            className="text-[14px] leading-[22px] text-black"
                        >
                            로그아웃
                        </Text>
                    </TouchableOpacity>
                </View>
            ) : (
                // 로그인되지 않은 경우 로그인 버튼 표시
                <TouchableOpacity onPress={() => navigation.navigate('Login')}>
                    <Text
                        style={{ fontFamily: 'Pretendard-Regular' }}
                        className="text-[14px] leading-[22px] text-black"
                    >
                        로그인
                    </Text>
                </TouchableOpacity>
            )}
        </View>
    );
};

export default Header;
