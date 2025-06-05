import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const CardProduct = ({ navigation }) => {
    const [cards, setCards] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // API에서 카드 상품 정보 가져오기
    useEffect(() => {
        const fetchCards = async () => {
            try {
                setLoading(true);
                // API 서버 기본 URL
                const API_BASE_URL = 'http://52.78.59.11:8080';
                
                console.log('[CardProduct] 신용카드/체크카드 API 호출');
                // 신용카드와 체크카드 데이터 모두 가져오기
                const [creditCardsRes, checkCardsRes] = await Promise.all([
                    axios.get(`${API_BASE_URL}/api/cards/credit`),
                    axios.get(`${API_BASE_URL}/api/cards/check`)
                ]);
                
                // 두 배열 합치기
                const allCards = [...creditCardsRes.data, ...checkCardsRes.data];
                console.log(`[CardProduct] 총 ${allCards.length}개 카드 상품 로드됨 (신용카드: ${creditCardsRes.data.length}, 체크카드: ${checkCardsRes.data.length})`);
                const response = { data: allCards };
                console.log('카드 상품 데이터:', response.data);
                setCards(response.data);
                setLoading(false);
            } catch (err) {
                console.error('카드 상품 데이터 가져오기 오류:', err);
                setError('카드 상품 정보를 불러오는데 실패했습니다.');
                setLoading(false);
            }
        };

        fetchCards();
    }, []);
    
    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">카드 상품</Text>
                    </View>
                </View>

                <ScrollView className="flex-1 p-4">
                    {loading ? (
                        <View className="flex items-center justify-center py-10">
                            <ActivityIndicator size="large" color="#014029" />
                            <Text className="mt-2 text-[#014029]">카드 상품 정보를 불러오는 중...</Text>
                        </View>
                    ) : error ? (
                        <View className="flex items-center justify-center py-10">
                            <Text className="text-red-500">{error}</Text>
                        </View>
                    ) : cards.length > 0 ? (
                        cards.map((card, index) => (
                            <View key={index} className="mb-4 bg-white p-4 rounded-xl shadow">
                                <Text className="text-lg font-bold text-[#014029]">{card.cardName || '카드명'}</Text>
                                <Text className="text-sm text-gray-600 mb-2">{card.companyName || '회사명'}</Text>
                                <View className="bg-gray-100 p-2 rounded-md mt-1">
                                    <Text className="text-sm font-medium">혜택: {card.benefit || '정보 없음'}</Text>
                                    <Text className="text-sm">연회비: {card.annualFee || '정보 없음'}</Text>
                                </View>
                            </View>
                        ))
                    ) : (
                        <Text className="text-center text-gray-500">현재 제공되는 카드 상품이 없습니다</Text>
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

export default CardProduct;


