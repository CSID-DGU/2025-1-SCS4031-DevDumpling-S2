import React, { useState, useEffect, useRef } from 'react';
import { View, Text, TextInput, TouchableOpacity, Image, SafeAreaView, Modal, ActivityIndicator, Alert } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { WebView } from 'react-native-webview';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function Login() {
    const navigation = useNavigation();
    const [loading, setLoading] = useState(false);
    const [showWebView, setShowWebView] = useState(false);
    const [webViewUrl, setWebViewUrl] = useState('');
    const authCodeProcessed = React.useRef({});

    const API_URL = 'http://10.0.2.2:8080'; // 에뮬레이터
    //const API_URL = 'http://172.17.0.1:8080'; // 기본 도커 브릿지 네트워크의 호스트 IP (환경에 따라 다를 수 있음)
    //const API_URL = 'http://backend:8080'; // Docker Compose 사용 시 서비스 이름
    //const API_URL = 'http://10.83.23.208:8080'; // 본인 로컬 IP 주소

    const handleKakaoLogin = async () => {
        authCodeProcessed.current = {};
        setLoading(true);
        try {
            setWebViewUrl(`${API_URL}/api/auth/kakao/login`);
            setShowWebView(true);
        } catch (error) {
            console.error('카카오 로그인 시작 오류:', error);
            setLoading(false);
            Alert.alert('카카오 로그인을 시작할 수 없습니다: ' + (error.message || '알 수 없는 오류'));
        }
    };

    return (
        <SafeAreaView className="flex-1 bg-Fineed-background items-center justify-center px-5">
            <View className="items-center mb-16">
                <Text className="text-2xl text-black mb-1">금융도 함께할 필요,</Text>
                <Text className="text-7xl font-bold text-Fineed-green">FINEED</Text>
            </View>

            <View className="w-full">
                <View className="w-full px-5">
                    <TouchableOpacity
                        onPress={handleKakaoLogin}
                        className="w-full h-[60px] bg-[#FEE500] rounded-full flex-row items-center justify-center mt-3"
                        style={{ overflow: 'hidden' }}
                    >
                        <Image
                            source={require('../../assets/images/kakaotalk_sharing_btn_small.png')}
                            style={{
                                width: 26,
                                height: 26,
                                marginRight: 10,
                            }}
                            resizeMode="contain"
                        />
                        <Text className="text-[#3C1E1E] text-base font-bold">카카오 로그인</Text>
                    </TouchableOpacity>
                </View>

                <View className="flex-row justify-center mt-5">
                    <Text className="text-gray-500 text-sm">회원가입</Text>
                </View>
            </View>

            <Modal
                visible={showWebView}
                onRequestClose={() => {
                    setShowWebView(false);
                    setLoading(false);
                }}
                animationType="slide"
            >
                <SafeAreaView style={{ flex: 1 }}>
                    <View className="flex-row justify-between items-center p-2 bg-Fineed-green">
                        <TouchableOpacity
                            onPress={() => {
                                setShowWebView(false);
                                setLoading(false);
                            }}
                            className="p-2"
                        >
                            <Text className="text-white font-bold">닫기</Text>
                        </TouchableOpacity>
                        <Text className="text-white font-bold">카카오 로그인</Text>
                        <View style={{ width: 40 }} />
                    </View>

                    <WebView
                        source={{ uri: webViewUrl }}
                        onNavigationStateChange={(navState) => {
                            console.log("WebView URL:", navState.url);

                            if (navState.url.includes('api/auth/kakao/callback')) {
                                try {
                                    console.log("콜백 URL 감지:", navState.url);

                                    // 인증 코드 추출
                                    const codeMatch = navState.url.match(/code=([^&]+)/);
                                    const code = codeMatch ? codeMatch[1] : null;

                                    console.log("추출된 코드:", code);

                                    if (code) {
                                        // WebView 숨기기
                                        setShowWebView(false);
                                        setLoading(true);

                                        // 백엔드 API 호출
                                        axios.get(`${API_URL}/api/auth/kakao/callback?code=${code}`)
                                            .then(response => {
                                                console.log("백엔드 응답:", response.data);

                                                // 토큰 저장
                                                AsyncStorage.setItem('userToken', response.data.token);
                                                AsyncStorage.setItem('userData', JSON.stringify(response.data.user));

                                                // 홈으로 이동
                                                navigation.reset({
                                                    index: 0,
                                                    routes: [{ name: 'Home' }],
                                                });
                                            })
                                            .catch(error => {
                                                console.error("API 오류:", error);
                                                console.error("응답 데이터:", error.response?.data);

                                                setLoading(false);
                                                alert(`로그인 실패: ${error.message}\n${JSON.stringify(error.response?.data)}`);
                                            });
                                    } else {
                                        console.error("코드 추출 실패");
                                        setLoading(false);
                                        alert("인증 코드를 추출할 수 없습니다");
                                    }
                                } catch (error) {
                                    console.error("콜백 처리 오류:", error);
                                    setLoading(false);
                                    alert(`콜백 처리 오류: ${error.message}`);
                                }
                            }
                        }}
                    />
                </SafeAreaView>
            </Modal>

            {loading && (
                <View className="absolute inset-0 bg-black bg-opacity-30 items-center justify-center">
                    <ActivityIndicator size="large" color="#014029" />
                </View>
            )}
        </SafeAreaView>
    );
}