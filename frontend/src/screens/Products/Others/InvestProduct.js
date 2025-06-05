import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const InvestProduct = ({ navigation }) => {
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
                
                try {
                    // GET 요청 시도
                    console.log('[InvestProduct] GET 요청 시도');
                    const response = await axios.get(`${API_BASE_URL}/api/stocks`);
                    console.log('[InvestProduct] GET 요청 성공:', response.data.length, '개 데이터');
                    setStocks(response.data);
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
                            setStocks(retryResponse.data);
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
    }, []);
    
    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">투자 상품</Text>
                    </View>
                </View>

                <ScrollView className="flex-1 p-4">
                    {loading ? (
                        <View className="flex items-center justify-center py-10">
                            <ActivityIndicator size="large" color="#014029" />
                            <Text className="mt-2 text-[#014029]">주식 정보를 불러오는 중...</Text>
                        </View>
                    ) : error ? (
                        <View className="flex items-center justify-center py-10">
                            <Text className="text-red-500">{error}</Text>
                        </View>
                    ) : stocks.length > 0 ? (
                        stocks.map((stock, index) => (
                            <View key={index} className="mb-4 bg-white p-4 rounded-xl shadow">
                                <Text className="text-lg font-bold text-[#014029]">{stock.stockName || '주식명'}</Text>
                                <Text className="text-sm text-gray-600 mb-2">{stock.market || '시장'}</Text>
                                <View className="bg-gray-100 p-3 rounded-md mt-2">
                                    <View className="flex-row justify-between mb-1">
                                        <Text className="text-sm">현재가:</Text>
                                        <Text className="text-sm font-bold">{stock.price ? `${stock.price.toLocaleString()}원` : '-'}</Text>
                                    </View>
                                    <View className="flex-row justify-between mb-1">
                                        <Text className="text-sm">등락율:</Text>
                                        <Text className={`text-sm font-bold ${stock.changeRate > 0 ? 'text-red-500' : stock.changeRate < 0 ? 'text-blue-500' : 'text-gray-500'}`}>
                                            {stock.changeRate ? `${stock.changeRate > 0 ? '+' : ''}${stock.changeRate}%` : '-'}
                                        </Text>
                                    </View>
                                    <View className="flex-row justify-between">
                                        <Text className="text-sm">거래량:</Text>
                                        <Text className="text-sm font-bold">{stock.volume ? stock.volume.toLocaleString() : '-'}</Text>
                                    </View>
                                </View>
                            </View>
                        ))
                    ) : (
                        <Text className="text-center text-gray-500">현재 제공되는 주식 상품이 없습니다</Text>
                    )}
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

export default InvestProduct;
