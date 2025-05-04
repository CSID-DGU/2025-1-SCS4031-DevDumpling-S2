import React from 'react';
import { View, Text, SafeAreaView, TouchableOpacity, Image } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Header from '../components/layout/Header';

const MyDataComplete = () => {
    const navigation = useNavigation();

    const handleGoToMain = () => {
        navigation.reset({
            index: 0,
            routes: [{ name: 'Home' }]
        });
    };

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-white">
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