import React from 'react';
import { View, Text, TouchableOpacity, useWindowDimensions } from 'react-native';

const RatingSection = () => {
    const { width } = useWindowDimensions();

    return (
        <View className="bg-white rounded-[15px] p-5 shadow-md mt-3 mb-2">
            <View className="flex-row justify-between mb-3">
                <View style={{ maxWidth: width * 0.5 }}>
                    <Text className="text-[12px] text-[#6D6D6D]">ETF · 미래에셋자산운용</Text>
                    <Text className="text-[18px] font-bold text-black mt-1">Tiger 미국&P500</Text>
                </View>
                <TouchableOpacity>
                    <Text className="text-[12px] text-[#6D6D6D]">금융 상품 평점 보러 가기 {'>'}</Text>
                </TouchableOpacity>
            </View>
            <Text className="text-[12px] text-[#6D6D6D]">로그인하면 이 상품의 평점을 볼 수 있어요</Text>
        </View>
    );
};

export default RatingSection;
