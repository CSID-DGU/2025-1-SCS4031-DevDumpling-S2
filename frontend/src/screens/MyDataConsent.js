import React, { useEffect } from 'react';
import { View, Text } from 'react-native';
import DumplingLoading from '../components/common/DumplingLoading';
import { useNavigation } from '@react-navigation/native';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://52.78.59.11:8080';

const MyDataConsent = () => {
    const navigation = useNavigation();

    const handleLoadingComplete = async () => {
        // 실제 로딩이 끝나면 홈으로 이동
        navigation.reset({
            index: 0,
            routes: [{ name: 'Home' }]
        });
    };

    // 실제 데이터 처리는 여기서 수행 (백엔드에서 계좌 생성 등)
    useEffect(() => {
        const processMyData = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                if (!token) return;

                // 여기서 실제 마이데이터 처리 API 호출
                // 백엔드에서 계좌 생성 등을 처리
                // await axios.post(`${API_BASE_URL}/api/mydata/process`, {}, {
                //   headers: { Authorization: `Bearer ${token}` }
                // });

                // 로딩 애니메이션 보여주기 (실제 API 응답을 기다리지 않음)
            } catch (error) {
                console.error('마이데이터 처리 중 오류 발생:', error);
            }
        };

        processMyData();
    }, []);

    return (
        <DumplingLoading
            message={["마이데이터를", "가져오는 중이에요!"]}
            onLoadingComplete={handleLoadingComplete}
        />
    );
};

export default MyDataConsent;