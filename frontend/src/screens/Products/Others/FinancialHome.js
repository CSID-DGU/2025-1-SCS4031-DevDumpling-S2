import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, TextInput } from 'react-native';
import Header from '../../../components/layout/Header';
import { Ionicons } from '@expo/vector-icons';

const categories = ['예·적금', '카드', '대출', '보험', 'ETF', '투자'];

const FinancialHome = ({ route, navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const { category } = route.params;
    const [selectedCategory, setSelectedCategory] = useState(category);

    // 카테고리를 route에서 받은 값으로 설정
    useEffect(() => {
        setSelectedCategory(category);
    }, [category]);

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-2 px-4 pb-12">
                <ScrollView
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding + 5,
                        paddingTop: 16,
                        paddingBottom: 24,
                        justifyContent: 'center'
                    }}>
                    <View className="flex-row mb-5 gap-2">
                        {categories.map((cat) => (
                            <TouchableOpacity
                                key={cat}
                                onPress={() => navigation.replace('FinancialHome', { category: cat })}
                                className={`px-4 py-2 rounded-full shadow-md ${
                                    selectedCategory === cat
                                    ? 'bg-[#014029]'
                                    : 'bg-[#F9F9F9]'
                            }`}>
                                <Text
                                    className={`text-sm font-semibold ${
                                        selectedCategory === cat ? 'text-white' : 'text-black'}`}>
                                    {cat}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </View>

                    {/* 추천 상품 */}
                    <View className="flex-col justify-center">
                        <TouchableOpacity className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md">
                            <Text className="text-sm text-[#6D6D6D] mb-4">
                                User님께 이 {selectedCategory} 상품을 추천드려요!
                            </Text>
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-2" />
                                <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                            </View>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default FinancialHome;


