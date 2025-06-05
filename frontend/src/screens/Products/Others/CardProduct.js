// ─────────────────────────────────────────────────────────────────────────────
// 파일명: Products/Others/CardProduct.js
// 설명:
//  • FinancialHome에서 넘겨온 카드 객체가 있으면 우선 사용
//  • 전달된 객체가 없으면 API를 다시 호출하여 productId(id)로 필터링
//  • API 예시 응답 필드(id, cardName, company, detailUrl, imageUrl, benefits)를
//    그대로 화면에 매핑하여 보여줍니다.
// ─────────────────────────────────────────────────────────────────────────────

import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    ActivityIndicator,
    Image,
    Linking,
} from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const CardProduct = ({ navigation, route }) => {
    // route.params.product: FinancialHome에서 넘겨준 전체 카드 객체
    // route.params.productId: 카드 ID만 넘겨준 경우
    const passedProduct = route.params?.product || null;
    const productId = route.params?.productId || null;

    const [cardList, setCardList] = useState([]); // API로 받아온(또는 전달받은) 카드 배열
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // API에서 카드 데이터를 가져오거나,
    // 전달받은 passedProduct가 있으면 그것만 배열에 담아서 사용
    useEffect(() => {
        const fetchCards = async () => {
            try {
                setLoading(true);
                setError(null);

                // 1) 만약 FinancialHome에서 전달된 상품 객체가 있으면,
                //    그대로 화면에 표시할 배열로 세팅하고 즉시 종료
                if (passedProduct) {
                    console.log('[CardProduct] 전달받은 카드 객체 사용:', passedProduct.cardName);
                    setCardList([passedProduct]);
                    setLoading(false);
                    return;
                }

                // 2) 전달된 객체가 없으면 productId를 이용해서 API 재조회
                const API_BASE_URL = 'http://52.78.59.11:8080';
                console.log('[CardProduct] 카드 API 호출 (신용/체크카드)');

                // credit + check 두 API를 병렬 호출
                const [creditRes, checkRes] = await Promise.all([
                    axios.get(`${API_BASE_URL}/api/cards/credit`),
                    axios.get(`${API_BASE_URL}/api/cards/check`),
                ]);

                const creditCards = Array.isArray(creditRes.data) ? creditRes.data : [];
                const checkCards = Array.isArray(checkRes.data) ? checkRes.data : [];
                const allCards = [...creditCards, ...checkCards];
                console.log(
                    `[CardProduct] 총 카드 수: ${allCards.length}개 (신용카드: ${creditCards.length}, 체크카드: ${checkCards.length})`
                );

                // 3) productId가 있으면 해당 ID 상품을 찾아서, 없으면 전체 배열
                if (productId) {
                    const matched = allCards.find(
                        (c) =>
                            // API 필드가 cardId 또는 id 형태일 수 있습니다.
                            c.cardId === productId || c.card_id === productId || c.id === productId
                    );
                    if (matched) {
                        console.log('[CardProduct] 필터된 카드 객체:', matched.cardName);
                        setCardList([matched]);
                    } else {
                        console.warn('[CardProduct] 해당 ID 카드 찾기 실패, 전체 목록 표시:', productId);
                        setCardList(allCards);
                    }
                } else {
                    setCardList(allCards);
                }

                setLoading(false);
            } catch (err) {
                console.error('[CardProduct] 카드 데이터 조회 오류 →', err);
                setError('카드 정보를 불러오던 중 오류가 발생했습니다.');
                setLoading(false);
            }
        };

        fetchCards();
    }, [passedProduct, productId]);

    // 로딩 / 오류 처리
    if (loading) {
        return (
            <>
                <Header />
                <View className="flex-1 justify-center items-center bg-white">
                    <ActivityIndicator size="large" color="#014029" />
                    <Text className="mt-2 text-[#014029]">카드 정보를 불러오는 중...</Text>
                </View>
            </>
        );
    }

    if (error) {
        return (
            <>
                <Header />
                <View className="flex-1 justify-center items-center bg-white px-5">
                    <Text className="text-red-500">{error}</Text>
                    <TouchableOpacity
                        onPress={() => navigation.goBack()}
                        className="mt-4 px-4 py-2 bg-[#014029] rounded-full"
                    >
                        <Text className="text-white">뒤로가기</Text>
                    </TouchableOpacity>
                </View>
            </>
        );
    }

    // 카드 배열이 비어있으면 빈 화면
    if (cardList.length === 0) {
        return (
            <>
                <Header />
                <View className="flex-1 justify-center items-center bg-white px-5">
                    <Text>등록된 카드가 없습니다.</Text>
                    <TouchableOpacity
                        onPress={() => navigation.goBack()}
                        className="mt-4 px-4 py-2 bg-[#014029] rounded-full"
                    >
                        <Text className="text-white">뒤로가기</Text>
                    </TouchableOpacity>
                </View>
            </>
        );
    }

    // 표시할 카드 객체 (배열 첫 번째)
    const card = cardList[0];

    // API 예시 필드에서 받아올 수 있는 값들
    const cardName = card.cardName || card.card_name || '-';
    const companyName = card.company || card.company_name || '-';
    const detailUrl = card.detailUrl || card.detail_url || null;
    const imageUrl = card.imageUrl || card.image_url || null;
    const benefitsRaw = card.benefits || card.benefit || ''; // 문자열 형태 (예: "['공과금: 10%할인', ...]")
    // 혜택 목록 문자열이 "['..','..']" 형태로 내려오므로, 배열처럼 파싱해 볼 수 있습니다.
    let benefitList = [];
    try {
        benefitList = JSON.parse(benefitsRaw.replace(/'/g, '"'));
        if (!Array.isArray(benefitList)) {
            benefitList = [];
        }
    } catch (e) {
        // JSON 파싱에 실패하면 빈 배열로 둡니다.
        benefitList = [];
    }

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerStyle={{
                        paddingHorizontal: 16,
                        paddingTop: 16,
                        paddingBottom: 24,
                    }}
                >
                    {/* ─────────────────────────────────────────────────────────────
              카드 이미지 (이미지 URL이 있으면 보여주기)
            ───────────────────────────────────────────────────────────── */}
                    {imageUrl ? (
                        <Image
                            source={{ uri: imageUrl }}
                            className="w-full h-48 mb-5 rounded-2xl bg-gray-200"
                            resizeMode="contain"
                        />
                    ) : null}

                    {/* ─────────────────────────────────────────────────────────────
              카드 기본 정보 (이름, 카드사)
            ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-2xl font-bold text-[#014029] mb-2">{cardName}</Text>
                        <Text className="text-sm text-gray-600">{companyName}</Text>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
              혜택 정보
            ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xl font-bold mb-4">혜택 정보</Text>
                        {benefitList.length > 0 ? (
                            benefitList.map((item, idx) => (
                                <Text key={idx} className="text-base mb-2">
                                    • {item}
                                </Text>
                            ))
                        ) : (
                            <Text className="text-base text-gray-500">혜택 정보가 없습니다.</Text>
                        )}
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
              카드 자세히 보기 버튼은 하단으로 이동했습니다
            ───────────────────────────────────────────────────────────── */}
                </ScrollView>

                {/* ─────────────────────────────────────────────────────────────
           카드 자세히 보기 버튼
          ───────────────────────────────────────────────────────────── */}
                <TouchableOpacity
                    onPress={() => {
                        if (detailUrl) {
                            Linking.openURL(detailUrl).catch((err) =>
                                console.error('[CardProduct] detailUrl 열기 실패:', err)
                            );
                        } else {
                            // 상세 URL이 없는 경우 뒤로가기
                            navigation.goBack();
                        }
                    }}
                    className="m-4 px-4 py-2 rounded-full bg-Fineed-green shadow-md items-center justify-center"
                >
                    <Text className="text-white text-lg font-bold">카드 자세히 보기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
};

export default CardProduct;
