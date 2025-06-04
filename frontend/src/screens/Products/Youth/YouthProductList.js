import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import Header from '../../../components/layout/Header';

// JSON 파일 직접 불러오기
const assetProducts = require('../../../data/products_1.json');
const loanProducts = require('../../../data/products_2.json');

const YouthProduct = ({ navigation, route }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const [selectedCategory, setSelectedCategory] = useState('자산형성');
    const categories = ['자산형성', '대출'];

    // 추천 상품 및 추천 완료 상태 관리
    const [recommendedAsset, setRecommendedAsset] = useState([]);
    const [recommendedLoan, setRecommendedLoan] = useState([]);
    const [recommendDone, setRecommendDone] = useState(false);

    useEffect(() => {
        if (route?.params?.recommendDone) {
            // 추천 상품 코드로 실제 상품 정보 매칭
            const assetCodes = route.params.recommendedAssetCodes || [];
            const loanCodes = route.params.recommendedLoanCodes || [];
            
            console.log('받은 추천 상품 코드:', { assetCodes, loanCodes });
            
            // 자산형성 상품 매칭
            const matchedAssets = assetCodes
                .map(code => {
                    const found = assetProducts.find(p => String(p.productId) === String(code));
                    if (!found) {
                        console.log('자산형성 상품 매칭 실패:', code);
                    }
                    return found;
                })
                .filter(product => product && product.category);
            
            // 대출 상품 매칭
            const matchedLoans = loanCodes
                .map(code => {
                    const found = loanProducts.find(p => String(p.productId) === String(code));
                    if (!found) {
                        console.log('대출 상품 매칭 실패:', code);
                    }
                    return found;
                })
                .filter(product => product && product.category);
            
            console.log('매칭된 상품:', { 
                assetCount: matchedAssets.length, 
                loanCount: matchedLoans.length 
            });
            
            setRecommendedAsset(matchedAssets);
            setRecommendedLoan(matchedLoans);
            setRecommendDone(true);
        }
    }, [route?.params]);

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
            if (!product || !product.applicationPeriod) return false;
            const startDate = new Date(product.applicationPeriod.start);
            const endDate = new Date(product.applicationPeriod.end);
            return startDate <= now && now <= endDate;
        }).filter(Boolean);
        const inactiveProducts = products.filter(product => {
            if (!product) return false;
            if (!product.applicationPeriod) return true;
            const startDate = new Date(product.applicationPeriod.start);
            const endDate = new Date(product.applicationPeriod.end);
            return now < startDate || now > endDate;
        }).filter(Boolean);
        return { activeProducts, inactiveProducts };
    };

    const currentProducts = getCurrentProducts();
    const { activeProducts, inactiveProducts } = categorizeAssetProducts(currentProducts);

    // 상품 리스트 렌더링 시 undefined 방지용 filter 적용
    const safeActiveProducts = (activeProducts || []).filter(Boolean);
    const safeInactiveProducts = (inactiveProducts || []).filter(Boolean);
    const safeCurrentProducts = (currentProducts || []).filter(Boolean);

    // 추천 상품 섹션 렌더링 함수
    const renderRecommendedSection = (products) => {
        if (!Array.isArray(products) || products.length === 0) {
            return null;
        }

        const validProducts = products.filter(product => 
            product && 
            typeof product === 'object' && 
            product.productId && 
            product.category
        );

        if (validProducts.length === 0) {
            return null;
        }

        return (
            <View className="mb-2 gap-4">
                <Text className="text-2xl font-bold">🎯 추천 상품</Text>
                {validProducts.map((product) => (
                    <TouchableOpacity
                        key={product.productId}
                        onPress={() => navigateToProduct(product)}
                        className="bg-[#FFF7E6] p-4 rounded-2xl shadow-md mb-3 border border-[#FFD580]">
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
        );
    };

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
                    <View className="items-left pb-5">
                        <TouchableOpacity onPress={navigateToAddYouthInfo}>
                            {recommendDone ? (
                                <>
                                    <Text className="text-2xl font-bold">추가 정보 입력 완료!</Text>
                                    <Text className="text-2xl font-bold">딱 맞는 상품을 추천해드릴게요</Text>
                                    <Text className="text-lg font-bold">다시 입력하기 →</Text>
                                </>
                            ) : (
                                <>
                                    <Text className="text-2xl font-bold">추가 정보를 입력하면</Text>
                                    <Text className="text-2xl font-bold">나에게 더 맞는 상품을 볼 수 있어요 →</Text>
                                </>
                            )}
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

                        {/* 추천 상품 섹션 */}
                        {selectedCategory === '자산형성'
                            ? renderRecommendedSection(recommendedAsset)
                            : renderRecommendedSection(recommendedLoan)}

                        <Text className="text-xl font-bold">
                            ✅ 현재 {selectedCategory === '자산형성' ? '가입' : '신청'} 가능한 청년 {selectedCategory} 상품
                        </Text>

                        {selectedCategory === '자산형성' ? (
                            <>
                                {/* 현재 가입 기간 중인 상품 */}
                                {safeActiveProducts.length > 0 && (
                                    <View className="mb-2 gap-4">
                                        {safeActiveProducts.map((product) => (
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
                                {safeInactiveProducts.length > 0 && (
                                    <View className="gap-4">
                                        <View className="border-t border-gray-200 w-full mb-5" />
                                        <Text className="text-xl font-bold">❌ 지금은 신청 기간이 아니에요</Text>
                                        {safeInactiveProducts.map((product) => (
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
                            safeCurrentProducts.map((product) => (
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


