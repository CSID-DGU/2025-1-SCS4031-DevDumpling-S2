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
            console.log('userToken:', userToken);
            console.log('userData:', userData);
            if (userToken && userData) {
                setIsLoggedIn(true);
                const user = JSON.parse(userData);
                setUserName(user.nickname || '');
            } else {
                setIsLoggedIn(false);
                setUserName('');
            }
        } catch (error) {
            console.error('로그인 상태 확인 오류:', error);
            setIsLoggedIn(false);
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
                <View className="flex-row items-center">


                    <TouchableOpacity onPress={() => navigation.navigate('Mypage')}>
                        <Text
                            style={{ fontFamily: 'Pretendard-Regular' }}
                            className="text-[14px] leading-[22px] text-black"
                        >
                            마이페이지
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
