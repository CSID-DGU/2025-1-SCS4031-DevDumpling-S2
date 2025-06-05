import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const InvestProduct = ({ navigation, route }) => {
    const { product } = route.params || {};
    const productId = route.params?.productId;
    const [stocks, setStocks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // API에서 주식 정보 가져오기
    useEffect(() => {
        const fetchStocks = async () => {
            try {
                setLoading(true);
                // API 서버 기본 URL
                const API_BASE_URL = 'http://52.78.59.11:8080';
                
                // 이미 상품 정보가 전달되었는지 확인
                if (product) {
                    console.log('[InvestProduct] 전달받은 상품 정보 사용:', product.stock_name || product.stockName);
                    setStocks([product]); // 전달받은 상품만 배열에 추가
                    setLoading(false);
                    return;
                }
                
                // 상품 정보가 없는 경우 API에서 가져오기
                try {
                    // GET 요청 시도
                    console.log('[InvestProduct] GET 요청 시도');
                    const response = await axios.get(`${API_BASE_URL}/api/stocks`);
                    console.log('[InvestProduct] GET 요청 성공:', response.data.length, '개 데이터');
                    
                    // productId가 있으면 해당 상품만 필터링
                    if (productId) {
                        const filteredProduct = response.data.find(item => item.stock_symbol === productId || item.stockSymbol === productId);
                        if (filteredProduct) {
                            setStocks([filteredProduct]);
                        } else {
                            setStocks(response.data);
                        }
                    } else {
                        setStocks(response.data);
                    }
                    
                    setLoading(false);
                } catch (getErr) {
                    console.log(`[InvestProduct] GET 요청 실패: ${getErr.message}, 상태코드: ${getErr.response?.status}`);
                    
                    if (getErr.response?.status === 404) {
                        try {
                            // POST 요청으로 재시도
                            console.log('[InvestProduct] POST 요청으로 재시도');
                            const fetchResponse = await axios.post(`${API_BASE_URL}/api/stocks/fetch`);
                            console.log('[InvestProduct] 데이터 가져오기 성공:', fetchResponse.data);
                            
                            // 다시 GET 요청
                            console.log('[InvestProduct] 다시 GET 요청 시도');
                            const retryResponse = await axios.get(`${API_BASE_URL}/api/stocks`);
                            console.log('[InvestProduct] 다시 GET 요청 성공:', retryResponse.data.length, '개 데이터');
                            
                            // productId가 있으면 해당 상품만 필터링
                            if (productId) {
                                const filteredProduct = retryResponse.data.find(item => item.stock_symbol === productId || item.stockSymbol === productId);
                                if (filteredProduct) {
                                    setStocks([filteredProduct]);
                                } else {
                                    setStocks(retryResponse.data);
                                }
                            } else {
                                setStocks(retryResponse.data);
                            }
                        } catch (retryErr) {
                            console.error('[InvestProduct] 재시도 실패:', retryErr);
                            throw retryErr; // 상위 catch로 오류 전파
                        }
                    } else {
                        throw getErr; // 404가 아닌 다른 오류는 상위로 전파
                    }
                }
            } catch (err) {
                console.error('[InvestProduct] 주식 상품 데이터 가져오기 최종 오류:', err);
                setError('주식 정보를 불러오는데 실패했습니다.');
                setLoading(false);
            }
        };

        fetchStocks();
    }, [product, productId]);
    
    // 상품 데이터 가져오기 (첫 번째 상품 또는 없으면 빈 객체)
    const stockProduct = stocks.length > 0 ? stocks[0] : {};
    
    // 상품 데이터 필드 통합 (다른 형식의 데이터를 지원하기 위해)
    const stockName = stockProduct.stock_name || stockProduct.stockName || '-';
    const stockSymbol = stockProduct.stock_symbol || stockProduct.stockSymbol || '-';
    const stockPrice = stockProduct.stock_price || stockProduct.price || 0;
    const stockChange = stockProduct.change_rate || stockProduct.changeRate || 0;
    const stockVolume = stockProduct.volume || 0;
    const stockMarket = stockProduct.market || '-';
    
    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                {loading ? (
                    <View className="flex items-center justify-center py-10">
                        <ActivityIndicator size="large" color="#014029" />
                        <Text className="mt-2 text-Fineed-green">주식 정보를 불러오는 중...</Text>
                    </View>
                ) : error ? (
                    <View className="flex items-center justify-center py-10">
                        <Text className="text-red-500">{error}</Text>
                    </View>
                ) : (
                    <ScrollView
                        contentContainerStyle={{
                            paddingHorizontal: 16,
                            paddingTop: 16,
                            paddingBottom: 24
                        }}>
                        {/* 상품 이름 및 기본 정보 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-2xl font-bold text-Fineed-green mb-2">{stockName}</Text>
                            <Text className="text-sm text-gray-600 mb-4">{stockSymbol} | {stockMarket}</Text>
                            
                            <View className="flex-row justify-between items-center mb-2">
                                <Text className="text-lg font-bold">현재가</Text>
                                <Text className="text-xl font-bold">
                                    {stockPrice ? `${Number(stockPrice).toLocaleString()}원` : '-'}
                                </Text>
                            </View>
                            
                            <View className="flex-row justify-between items-center">
                                <Text className="text-lg font-bold">등락률</Text>
                                <Text className={`text-lg font-bold ${stockChange > 0 ? 'text-red-500' : stockChange < 0 ? 'text-blue-500' : 'text-gray-500'}`}>
                                    {stockChange != null ? `${stockChange > 0 ? '▲' : stockChange < 0 ? '▼' : ''} ${Math.abs(stockChange).toFixed(2)}%` : '-'}
                                </Text>
                            </View>
                        </View>
                        
                        {/* 상세 정보 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-xl font-bold mb-4">상세 정보</Text>
                            
                            <View className="flex-row gap-5">
                                <View className="flex-col gap-3 w-24">
                                    <Text className="text-sm font-bold">거래량</Text>
                                    <Text className="text-sm font-bold">시장구분</Text>
                                    <Text className="text-sm font-bold">종목코드</Text>
                                </View>
                                <View className="flex-col gap-3 flex-1">
                                    <Text className="text-sm">{stockVolume ? Number(stockVolume).toLocaleString() : '-'}</Text>
                                    <Text className="text-sm">{stockMarket}</Text>
                                    <Text className="text-sm">{stockSymbol}</Text>
                                </View>
                            </View>
                        </View>
                        
                        {/* 투자 정보 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-xl font-bold mb-4">투자 정보</Text>
                            
                            <View className="flex-row gap-5">
                                <View className="flex-col gap-3 w-24">
                                    <Text className="text-sm font-bold">일분기 최고</Text>
                                    <Text className="text-sm font-bold">일분기 최저</Text>
                                    <Text className="text-sm font-bold">시가총액</Text>
                                </View>
                                <View className="flex-col gap-3 flex-1">
                                    <Text className="text-sm">{stockProduct.high_price ? Number(stockProduct.high_price).toLocaleString() + '원' : '-'}</Text>
                                    <Text className="text-sm">{stockProduct.low_price ? Number(stockProduct.low_price).toLocaleString() + '원' : '-'}</Text>
                                    <Text className="text-sm">{stockProduct.market_cap ? Number(stockProduct.market_cap).toLocaleString() + '원' : '-'}</Text>
                                </View>
                            </View>
                        </View>
                    </ScrollView>
                )}
                
                <TouchableOpacity 
                    className="m-4 px-4 py-2 rounded-full bg-Fineed-green shadow-md items-center justify-center"
                    onPress={() => navigation.navigate('FinancialHome', { category: '투자' })}>
                    <Text className="text-white text-lg font-bold">목록으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
};

export default InvestProduct;
