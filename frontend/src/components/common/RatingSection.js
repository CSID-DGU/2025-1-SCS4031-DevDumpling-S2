import React from 'react';
import { View, Text, TouchableOpacity, useWindowDimensions } from 'react-native';
import { useNavigation } from '@react-navigation/native';

const RatingSection = () => {
    const { width } = useWindowDimensions();
    const navigation = useNavigation();

    const navigateToProductsHome = () => {
        navigation.navigate('ProductsHome');
    };

    return (
        <View className="bg-white rounded-[15px] p-5 shadow-md mt-3 mb-2">
            <View className="flex-row justify-between mb-3">
                <View style={{ maxWidth: width * 0.5 }}>
                    <Text className="text-[12px] text-[#6D6D6D]">청년우대상품</Text>
                    <Text className="text-[18px] font-bold text-black mt-1">청년 주택드림 청약통장</Text>
                </View>
                <TouchableOpacity onPress={navigateToProductsHome}>
                    <Text className="text-[12px] text-[#6D6D6D]">더 많은 상품 보러가기 {'>'}</Text>
                </TouchableOpacity>
            </View>
            <Text className="text-[12px] text-[#6D6D6D]">로그인하면 더 자세한 정보를 볼 수 있어요</Text>
        </View>
    );
};

export default RatingSection;
