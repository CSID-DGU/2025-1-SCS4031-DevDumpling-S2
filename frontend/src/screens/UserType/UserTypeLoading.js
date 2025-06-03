import { View } from 'react-native';
import DumplingLoading from '../../components/common/DumplingLoading';
import { useNavigation } from '@react-navigation/native';
import { useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://52.78.59.11:8080';

const UserTypeLoading = () => {
    const navigation = useNavigation();

    useEffect(() => {
        const fetchUserType = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                if (!token) throw new Error('토큰 없음');

                // 백엔드에서 userType 받아오기
                const response = await axios.get(`${API_BASE_URL}/users/user-type`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                // userData 업데이트
                const userDataStr = await AsyncStorage.getItem('userData');
                const userData = userDataStr ? JSON.parse(userDataStr) : {};
                userData.userType = response.data.userType;
                await AsyncStorage.setItem('userData', JSON.stringify(userData));
            } catch (error) {
                console.error('유저 타입 분류 실패:', error);
            }
        };

        fetchUserType();
    }, []);

    return (
        <DumplingLoading
            message={["사용자 유형을", "분석 중이에요!"]}
            onLoadingComplete={() => {
                navigation.navigate('UserTypeResult');
            }}
        />
    );
};

export default UserTypeLoading;
