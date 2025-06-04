import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import Header from '../../../components/layout/Header';

// JSON 파일 직접 불러오기
const assetProducts = require('../../../data/products_1.json');
const loanProducts = require('../../../data/products_2.json');

const YouthProduct = ({ navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const [selectedCategory, setSelectedCategory] = useState('자산형성');
    const categories = ['자산형성', '대출'];

    const navigateToAddYouthInfo = () => {
        navigation.navigate('AddYouthInfo');
    };

    const navigateToProduct = (product) => {
        if (selectedCategory === '대출') {
            navigation.navigate('YouthLoanProduct', { product });
        } else {
            navigation.navigate('YouthProduct', { product });
        }
    };

    // 현재 선택된 카테고리에 따른 상품 표시
    const getCurrentProducts = () => {
        return selectedCategory === '자산형성' ? assetProducts : loanProducts;
    };

    // 현재 날짜 기준으로 가입 기간 중인 상품과 아닌 상품 분류
    const categorizeAssetProducts = (products) => {
        const now = new Date();
        const activeProducts = products.filter(product => {
            if (!product.applicationPeriod) return false;
            const startDate = new Date(product.applicationPeriod.start);
            const endDate = new Date(product.applicationPeriod.end);
            return startDate <= now && now <= endDate;
        });
        const inactiveProducts = products.filter(product => {
            if (!product.applicationPeriod) return true;
            const startDate = new Date(product.applicationPeriod.start);
            const endDate = new Date(product.applicationPeriod.end);
            return now < startDate || now > endDate;
        });
        return { activeProducts, inactiveProducts };
    };

    const currentProducts = getCurrentProducts();
    const { activeProducts, inactiveProducts } = categorizeAssetProducts(currentProducts);

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-2 px-4">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                    }}>
                    {/* 추가 정보 안내란 */}
                    <View className="items-left pb-10">
                        <TouchableOpacity onPress={navigateToAddYouthInfo}>
                            <Text className="text-2xl font-bold">추가 정보를 입력하면</Text>
                            <Text className="text-2xl font-bold">나에게 더 맞는 상품을 볼 수 있어요 →</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 카테고리 탭 */}
                    <ScrollView 
                        horizontal 
                        showsHorizontalScrollIndicator={false}
                        contentContainerStyle={{ gap: 8 }}
                        className="mb-5">
                        {categories.map((cat) => (
                            <TouchableOpacity
                                key={cat}
                                onPress={() => setSelectedCategory(cat)}
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

                    {/* 상품 리스트 */}
                    <View className="flex-col justify-center gap-5 mb-5">
                        <Text className="text-xl font-bold">
                            ✅ 현재 {selectedCategory === '자산형성' ? '가입' : '신청'} 가능한 청년 {selectedCategory} 상품
                        </Text>

                        {selectedCategory === '자산형성' ? (
                            <>
                                {/* 현재 가입 기간 중인 상품 */}
                                {activeProducts.length > 0 && (
                                    <View className="mb-2 gap-4">
                                        {/* <Text className="text-base font-semibold mb-3">⏰ 지금 신청 가능한 상품</Text> */}
                                        {activeProducts.map((product) => (
                                            <TouchableOpacity
                                                key={product.productId}
                                                onPress={() => navigateToProduct(product)}
                                                className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md mb-3">
                                                <Text className="text-xs text-[#6D6D6D] mb-2">{product.category || ''}</Text>
                                                <Text className="text-2xl font-bold text-[#014029] mb-2">{product.productName}</Text>
                                                <Text className="text-sm">{product.summary || product.Summary || ''}</Text>
                                                {product.tags && Array.isArray(product.tags) && product.tags.length > 0 && (
                                                    <Text className="text-xs text-[#6D6D6D] mt-2">
                                                        #{product.tags.join(' #')}
                                                    </Text>
                                                )}
                                            </TouchableOpacity>
                                        ))}
                                    </View>
                                )}

                                {/* 현재 가입 기간이 아닌 상품 */}
                                {inactiveProducts.length > 0 && (
                                    <View className="gap-4">
                                        <View className="border-t border-gray-200 w-full mb-5" />
                                        <Text className="text-xl font-bold">❌ 지금은 신청 기간이 아니에요</Text>
                                        {inactiveProducts.map((product) => (
                                            <TouchableOpacity
                                                key={product.productId}
                                                onPress={() => navigateToProduct(product)}
                                                className="bg-[#D9D9D9] p-4 rounded-2xl shadow-md mb-3">
                                                <Text className="text-xs text-[#6D6D6D] mb-2">{product.category || ''}</Text>
                                                <Text className="text-2xl font-bold text-[#014029] mb-2">{product.productName}</Text>
                                                <Text className="text-sm">{product.summary || product.Summary || ''}</Text>
                                                {product.tags && Array.isArray(product.tags) && product.tags.length > 0 && (
                                                    <Text className="text-xs text-[#6D6D6D] mt-2">
                                                        #{product.tags.join(' #')}
                                                    </Text>
                                                )}
                                            </TouchableOpacity>
                                        ))}
                                    </View>
                                )}
                            </>
                        ) : (
                            // 대출 상품 표시
                            currentProducts.map((product) => (
                                <TouchableOpacity
                                    key={product.productId}
                                    onPress={() => navigateToProduct(product)}
                                    className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md mb-3">
                                    <Text className="text-xs text-[#6D6D6D] mb-2">{product.category || ''}</Text>
                                    <Text className="text-2xl font-bold text-[#014029] mb-2">{product.productName}</Text>
                                    <Text className="text-sm">{product.summary || product.Summary || ''}</Text>
                                    {product.tags && Array.isArray(product.tags) && product.tags.length > 0 && (
                                        <Text className="text-xs text-[#6D6D6D] mt-2">
                                            #{product.tags.join(' #')}
                                        </Text>
                                    )}
                                </TouchableOpacity>
                            ))
                        )}
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default YouthProduct;


