// InsuranceProductDetail.js
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

const InsuranceProductDetail = ({ navigation, route }) => {
    const { product } = route.params || {};
    const productId = route.params?.productId;

    const [insurance, setInsurance] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // FSS 응답의 날짜 형식을 YYYY-MM-DD로 변환
    const formatDate = (dateString) => {
        if (!dateString || dateString.length !== 8) return dateString || '정보 없음';
        const yy = dateString.substring(0, 4);
        const mm = dateString.substring(4, 6);
        const dd = dateString.substring(6, 8);
        return `${yy}-${mm}-${dd}`;
    };

    useEffect(() => {
        const fetchInsurance = async () => {
            try {
                setLoading(true);

                // 1) route.params로 이미 전달된 보험 상품 정보가 있으면 그대로 사용
                if (product) {
                    console.log('[InsuranceProductDetail] 전달받은 보험 상품:', product.prdNm || product.ptrn);
                    setInsurance(product);
                    setLoading(false);
                    return;
                }

                // 2) 전달된 정보가 없으면 API에서 전체 리스트 조회 후 ID로 필터링
                const API_BASE_URL = 'http://52.78.59.11:8080';
                console.log('[InsuranceProductDetail] 보험 상품 API 호출');
                const response = await axios.get(`${API_BASE_URL}/api/insurance/list`);
                const allInsurances = Array.isArray(response.data) ? response.data : [];

                console.log(`[InsuranceProductDetail] 총 ${allInsurances.length}개 보험 상품 로드됨`);

                if (productId) {
                    const matched = allInsurances.find(
                        (item) =>
                            String(item.id) === String(productId) ||
                            String(item.prdCd) === String(productId)
                    );
                    if (matched) {
                        console.log('[InsuranceProductDetail] 필터링된 보험 상품:', matched.prdNm || matched.ptrn);
                        setInsurance(matched);
                    } else {
                        console.warn('[InsuranceProductDetail] 해당 ID의 보험 상품을 찾을 수 없음:', productId);
                        setInsurance(null);
                        setError('해당 보험 상품을 찾을 수 없습니다.');
                    }
                } else {
                    // productId가 없다면 첫 번째 항목을 보여주도록 설정
                    setInsurance(allInsurances[0] || null);
                }

                setLoading(false);
            } catch (err) {
                console.error('[InsuranceProductDetail] API 오류:', err);
                setError('보험 상품 정보를 불러오는데 실패했습니다.');
                setLoading(false);
            }
        };

        fetchInsurance();
    }, [product, productId]);

    if (loading) {
        return (
            <>
                <Header />
                <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                    <View className="flex items-center justify-center py-10">
                        <ActivityIndicator size="large" color="#014029" />
                        <Text className="mt-2 text-[#014029]">보험 정보를 불러오는 중...</Text>
                    </View>
                </View>
            </>
        );
    }

    if (!insurance) {
        return (
            <>
                <Header />
                <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                    <View className="flex items-center justify-center py-10">
                        <Text className="text-red-500">{error || '보험 상품 정보가 없습니다.'}</Text>
                    </View>
                    <TouchableOpacity
                        className="m-4 px-4 py-2 rounded-full bg-[#014029] shadow-md items-center justify-center"
                        onPress={() => navigation.navigate('FinancialHome', { category: '보험' })}
                    >
                        <Text className="text-white text-lg font-bold">목록으로 돌아가기</Text>
                    </TouchableOpacity>
                </View>
            </>
        );
    }

    // ↓ FSS 스펙 필드 통합 ↓
    const companyName = insurance.cmpyNm || '-';            // 보험사명
    const productName = insurance.prdNm || '-';             // 상품명
    const pattern = insurance.ptrn || '-';              // 보험 유형(예: 4세대 실손의료보험)
    const mog = insurance.mog || '-';              // 보장 항목(예: 요양병원의료비)
    const baseDate = insurance.basDt ? formatDate(insurance.basDt) : '-'; // 공시일자
    const provider = insurance.ofrInstNm || '-';          // 제공 기관(예: 손해보험협회)
    const maleRate = insurance.mlInsRt != null ? insurance.mlInsRt : '-';   // 남성 보험료
    const femaleRate = insurance.fmlInsRt != null ? insurance.fmlInsRt : '-'; // 여성 보험료
    const minAge = insurance.age || '-';                // 예시: 가입 가능 나이(문서에 36으로 표시됨)

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
             1) 보험 이름 및 회사 정보 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-2xl font-bold text-[#014029] mb-2">{productName}</Text>
                        <Text className="text-sm text-gray-600 mb-1">
                            {companyName} | {pattern}
                        </Text>
                        <Text className="text-xs text-[#6D6D6D]">
                            공시일자: {baseDate}
                        </Text>
                        <Text className="text-xs text-[#6D6D6D]">
                            제공기관: {provider}
                        </Text>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             2) 보장 내용 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xl font-bold mb-4">보장 항목</Text>
                        <Text className="text-base">{mog}</Text>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             3) 남/여 보험료 안내 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xl font-bold mb-2">보험료 안내</Text>
                        <View className="flex-row justify-between mb-2">
                            <Text className="text-sm font-bold">남성 보험료</Text>
                            <Text className="text-sm">{maleRate}</Text>
                        </View>
                        <View className="flex-row justify-between">
                            <Text className="text-sm font-bold">여성 보험료</Text>
                            <Text className="text-sm">{femaleRate}</Text>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             4) 가입 조건 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xl font-bold mb-4">가입 조건</Text>
                        <View className="flex-row gap-5">
                            <View className="flex-col gap-1 w-24">
                                <Text className="text-sm font-bold">가입 가능 나이</Text>
                            </View>
                            <View className="flex-col gap-1 flex-1">
                                <Text className="text-sm">
                                    {typeof minAge === 'string' && minAge !== '-'
                                        ? `${minAge}세`
                                        : minAge === '-'
                                            ? '제한 없음'
                                            : `${minAge}세`}
                                </Text>
                            </View>
                        </View>
                    </View>
                </ScrollView>

                {/* ─────────────────────────────────────────────────────────────
           5) “목록으로 돌아가기” 버튼 */}
                <TouchableOpacity
                    className="m-4 px-4 py-2 rounded-full bg-[#014029] shadow-md items-center justify-center"
                    onPress={() => navigation.navigate('FinancialHome', { category: '보험' })}
                >
                    <Text className="text-white text-lg font-bold">목록으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
};

export default InsuranceProductDetail;
