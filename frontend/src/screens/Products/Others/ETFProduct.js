// ETFProduct.js
import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    ActivityIndicator
} from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const ETFProduct = ({ navigation, route }) => {
    const { product } = route.params || {};
    const productId = route.params?.productId;
    const [etfProducts, setEtfProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 날짜 형식을 YYYY-MM-DD로 변환
    const formatDate = (dateString) => {
        if (!dateString || dateString.length !== 8) return dateString || '-';
        const yy = dateString.substring(0, 4);
        const mm = dateString.substring(4, 6);
        const dd = dateString.substring(6, 8);
        return `${yy}-${mm}-${dd}`;
    };

    useEffect(() => {
        const fetchEtfProducts = async () => {
            try {
                setLoading(true);
                const API_BASE_URL = 'http://52.78.59.11:8080';

                // 이미 상품 정보가 전달되었는지 확인
                if (product) {
                    console.log('[ETFProduct] 전달받은 상품 정보 사용:', product.isuNm);
                    setEtfProducts([product]);
                    setLoading(false);
                    return;
                }

                // API에서 ETF 목록 가져오기
                try {
                    console.log('[ETFProduct] GET 요청 시도 → /api/etfs');
                    const response = await axios.get(`${API_BASE_URL}/api/etfs`);
                    console.log('[ETFProduct] GET 요청 성공:', response.data.length, '개 데이터');

                    if (productId) {
                        const filtered = response.data.find((item) => item.isuCd === productId);
                        setEtfProducts(filtered ? [filtered] : response.data);
                    } else {
                        setEtfProducts(response.data);
                    }
                    setLoading(false);
                } catch (getErr) {
                    console.log(
                        `[ETFProduct] GET 요청 실패: ${getErr.message}, 상태코드: ${getErr.response?.status}`
                    );

                    if (getErr.response?.status === 404) {
                        try {
                            console.log('[ETFProduct] POST 요청으로 재시도 → /api/etfs/fetch');
                            await axios.post(`${API_BASE_URL}/api/etfs/fetch`);
                            console.log('[ETFProduct] POST 요청 성공 → 데이터 갱신됨');

                            console.log('[ETFProduct] 다시 GET 요청 시도 → /api/etfs');
                            const retryResponse = await axios.get(`${API_BASE_URL}/api/etfs`);
                            console.log(
                                '[ETFProduct] 다시 GET 요청 성공:',
                                retryResponse.data.length,
                                '개 데이터'
                            );

                            if (productId) {
                                const filtered = retryResponse.data.find((item) => item.isuCd === productId);
                                setEtfProducts(filtered ? [filtered] : retryResponse.data);
                            } else {
                                setEtfProducts(retryResponse.data);
                            }
                        } catch (retryErr) {
                            console.error('[ETFProduct] 재시도 실패:', retryErr);
                            throw retryErr;
                        }
                    } else {
                        throw getErr;
                    }
                }
            } catch (err) {
                console.error('[ETFProduct] ETF 상품 데이터 가져오기 최종 오류:', err);
                setError('ETF 상품 정보를 불러오는데 실패했습니다.');
                setLoading(false);
            }
        };

        fetchEtfProducts();
    }, [product, productId]);

    // 표시할 첫 번째 ETF 상품
    const etfProduct = etfProducts.length > 0 ? etfProducts[0] : {};

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                {loading ? (
                    <View className="flex items-center justify-center py-10">
                        <ActivityIndicator size="large" color="#014029" />
                        <Text className="mt-2 text-[#014029]">ETF 상품 정보를 불러오는 중...</Text>
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
                            paddingBottom: 24,
                        }}
                    >
                        {/* ─────────────────────────────────────────────────────────────
               1) 상품 이름 및 기본 정보 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-2xl font-bold text-[#014029] mb-2">
                                {etfProduct.isuNm || etfProduct.idxIndNm || '상품명'}
                            </Text>
                            <Text className="text-sm text-gray-600 mb-1">
                                {etfProduct.mktNm !== '' ? etfProduct.mktNm : etfProduct.sectTpNm || '시장 정보 없음'}
                            </Text>
                            <Text className="text-xs text-[#6D6D6D]">
                                기준일자: {formatDate(etfProduct.basDd)}
                            </Text>
                        </View>

                        {/* ─────────────────────────────────────────────────────────────
               2) 현재가 및 등락률 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-xl font-bold mb-4">시세 정보</Text>
                            <View className="flex-row justify-between mb-2">
                                <Text className="text-lg font-bold">현재가</Text>
                                <Text className="text-xl font-bold">
                                    {etfProduct.tddClsprc != null ? `${Number(etfProduct.tddClsprc).toLocaleString()}원` : '-'}
                                </Text>
                            </View>
                            <View className="flex-row justify-between">
                                <Text className="text-lg font-bold">등락률</Text>
                                <Text
                                    className={`text-lg font-bold ${etfProduct.flucRt > 0
                                            ? 'text-red-500'
                                            : etfProduct.flucRt < 0
                                                ? 'text-blue-500'
                                                : 'text-gray-500'
                                        }`}
                                >
                                    {etfProduct.flucRt != null
                                        ? `${etfProduct.flucRt > 0 ? '▲' : etfProduct.flucRt < 0 ? '▼' : ''} ${Math.abs(
                                            etfProduct.flucRt
                                        ).toFixed(2)}%`
                                        : '-'}
                                </Text>
                            </View>
                        </View>

                        {/* ─────────────────────────────────────────────────────────────
               3) 상세 정보 */}
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-xl font-bold mb-4">상세 정보</Text>
                            <View className="flex-row gap-5">
                                <View className="flex-col gap-3 w-28">
                                    <Text className="text-sm font-bold">거래량</Text>
                                    <Text className="text-sm font-bold">거래대금</Text>
                                    <Text className="text-sm font-bold">시가총액</Text>
                                    <Text className="text-sm font-bold">순자산가치</Text>
                                    <Text className="text-sm font-bold">기초지수</Text>
                                    <Text className="text-sm font-bold">지수 등락률</Text>
                                </View>
                                <View className="flex-col gap-3 flex-1">
                                    <Text className="text-sm">
                                        {etfProduct.accTrdvol != null
                                            ? Number(etfProduct.accTrdvol).toLocaleString()
                                            : etfProduct.listShrs != null
                                                ? Number(etfProduct.listShrs).toLocaleString()
                                                : '-'}
                                    </Text>
                                    <Text className="text-sm">
                                        {etfProduct.accTrdval != null
                                            ? Number(etfProduct.accTrdval).toLocaleString() + '원'
                                            : '-'}
                                    </Text>
                                    <Text className="text-sm">
                                        {etfProduct.mktcap != null
                                            ? Number(etfProduct.mktcap).toLocaleString() + '원'
                                            : '-'}
                                    </Text>
                                    <Text className="text-sm">
                                        {etfProduct.nav != null ? Number(etfProduct.nav).toLocaleString() + '원' : '-'}
                                    </Text>
                                    <Text className="text-sm">{etfProduct.idxIndNm || '-'}</Text>
                                    <Text className="text-sm">
                                        {etfProduct.flucRtIdx != null
                                            ? `${etfProduct.flucRtIdx > 0 ? '▲' : etfProduct.flucRtIdx < 0 ? '▼' : ''} ${Math.abs(
                                                etfProduct.flucRtIdx
                                            ).toFixed(2)}%`
                                            : '-'}
                                    </Text>
                                </View>
                            </View>
                        </View>
                    </ScrollView>
                )}

                <TouchableOpacity
                    className="m-4 px-4 py-2 rounded-full bg-[#014029] shadow-md items-center justify-center"
                    onPress={() => navigation.navigate('FinancialHome', { category: 'ETF' })}
                >
                    <Text className="text-white text-lg font-bold">목록으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
};

export default ETFProduct;
