import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import Header from '../../../components/layout/Header';

const FinancialHome = ({ route }) => {
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
                    <View className="flex-row justify-between items-center">
                        <TouchableOpacity>
                            <Text>예적금</Text>
                        </TouchableOpacity>
                    </View>
                    <View className="flex-row justify-between items-center">
                        <Text className="text-2xl font-bold">User님이 가입한 상품</Text>
                    </View>
                    <Text className="text-3xl font-bold">{selectedCategory}</Text>
                </ScrollView>
            </View>
        </>
    );
};

export default FinancialHome;


