import React from 'react';
import { View, Text, TextInput, TouchableOpacity, Image, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';

export default function Login() {
    const navigation = useNavigation();

    // 카카오 로그인 처리 함수
    const handleKakaoLogin = () => {
        // 실제 카카오 로그인 구현 해야함
        navigation.navigate('Home');
        // 로그인 성공 하면 홈으로 이동
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
        </SafeAreaView>
    );
}