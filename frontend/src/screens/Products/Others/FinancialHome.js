import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, TextInput } from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

const categories = ['예·적금', '카드', '대출', '보험', 'ETF', '투자'];

const FinancialHome = ({ route, navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const { category } = route.params;
    const [selectedCategory, setSelectedCategory] = useState(category);
    const [searchText, setSearchText] = useState('');

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

                    {/* 카테고리 탭 */}
                    <ScrollView
                        horizontal
                        showsHorizontalScrollIndicator={false}
                        contentContainerStyle={{ gap: 8 }}
                        className="mb-5">
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
                    </ScrollView>

                    {/* 추천 상품 */}
                    <View className="flex-col justify-center mb-8">
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md"
                            onPress={() => navigation.navigate('DepositProduct')}>
                            <Text className="text-sm text-[#6D6D6D] mb-5">
                                User님께 이 {selectedCategory} 상품을 추천드려요!
                            </Text>
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                    </View>

                    {/* 가입 중인 상품 리스트 */}
                    <Text className="text-xl font-bold text-black mb-5">User님이 가입한 {selectedCategory} 상품</Text>
                    <View className="flex-col justify-center gap-4 mb-8">
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md"
                            onPress={() => navigation.navigate('UserTypeNone')}>
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md"
                            onPress={() => navigation.navigate('UserTypeResult')}>
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                    </View>
                    
                    <View className="border-t border-gray-200 w-full mb-8" />

                    {/* 검색 창 */}
                    <View
                        className="flex-row items-center bg-white rounded-full px-5 py-0.5 shadow-md mb-5"
                        style={{
                            paddingVertical: 12,
                            paddingHorizontal: horizontalPadding,
                        }}>
                        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-2">
                            <Icon name="arrow-back" size={22} color="#014029" />
                        </TouchableOpacity>
                        <TextInput
                            className="flex-1 text-sm text-black"
                            placeholder="검색할 상품을 입력하세요"
                            value={searchText}
                            onChangeText={setSearchText}
                        />
                        <Icon name="search" size={20} color="#014029" />
                    </View>

                    {/* 상품 리스트 */}
                    <View className="flex-col justify-center gap-4 mb-8">
                        <TouchableOpacity className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md">
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                        <TouchableOpacity className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md">
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                        <TouchableOpacity className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md">
                            <View className="flex-row items-center">
                                <View className="w-16 h-16 bg-gray-400 rounded-full mr-5" />
                                <View className="flex-col">
                                    <Text className="text-xs text-[#6D6D6D]">하나은행</Text>
                                    <Text className="text-xl font-bold text-[#014029] mb-1">3·6·9 정기예금</Text>
                                </View>
                            </View>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default FinancialHome;


