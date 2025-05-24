import React from 'react';
import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';

const DepositProduct = ({ navigation }) => {
    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">예·적금 상품</Text>
                    </View>
                </View>

                <ScrollView className="flex-1">
                    {/* 여기에 예적금 상품 목록이 들어갈 예정입니다 */}
                    <Text className="text-center text-gray-500">준비 중입니다</Text>
                </ScrollView>

                <TouchableOpacity 
                    className="mb-8 items-center"
                    onPress={() => navigation.navigate('ProductsHome')}>
                    <Text className="text-sm text-[#6D6D6D] underline">상품 목록으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
};

export default DepositProduct;
