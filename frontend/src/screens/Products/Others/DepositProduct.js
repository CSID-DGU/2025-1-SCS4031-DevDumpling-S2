// ─────────────────────────────────────────────────────────────────────────────
// 파일명: Products/Others/DepositProduct.js
// 설명:
//  • 예·적금 상품을 선택했을 때 상세 정보를 보여주는 화면
//  • FinancialHome에서 넘겨준 `product` 객체가 있으면 그것을 우선 사용하고,
//    없으면 API를 재호출하여 같은 fin_prdt_cd 상품을 찾아 사용
//  • API 응답: /api/fss/deposit, /api/fss/saving → { result: { baseList: [...] } }
// ─────────────────────────────────────────────────────────────────────────────

import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    useWindowDimensions,
    ActivityIndicator,
} from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import axios from 'axios';

const DepositProduct = ({ navigation, route }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    // FinancialHome에서 넘겨준 상품 객체(product)가 있으면 사용
    // 그렇지 않으면 productId(fin_prdt_cd)를 이용해서 다시 API에서 찾아야 함
    const passedProduct = route.params?.product || null;
    const productId = route.params?.productId || null; // fin_prdt_cd

    const [product, setProduct] = useState(passedProduct);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // 은행별 금리 비교(예시 데이터)
    // 실제로는 API가 제공하는 값이 있으면 그쪽을 쓰시면 됩니다.
    const [expandedBankIds, setExpandedBankIds] = useState([]);
    const banks = [
        {
            id: 'woori',
            name: '우리은행',
            rate: '3.50%', // 예시
            benefit: '인터넷 신규가입 우대금리',
        },
        {
            id: 'shinhan',
            name: '신한은행',
            rate: '3.30%',
            benefit: '스마트뱅킹 이체 우대',
        },
        // 필요에 따라 더 추가
    ];

    // 날짜 포맷함수: YYYYMMDD 또는 YYYYMM → 사람이 읽을 수 있는 문자열로 변환
    const formatDate = (raw) => {
        if (!raw) return '-';
        // raw 가 "20250530" (YYYYMMDD)인 경우
        if (raw.length === 8) {
            const yy = raw.substring(0, 4);
            const mm = raw.substring(4, 6);
            const dd = raw.substring(6, 8);
            return `${yy}-${mm}-${dd}`;
        }
        // raw 가 "202505" (YYYYMM)인 경우
        if (raw.length === 6) {
            const yy = raw.substring(0, 4);
            const mm = raw.substring(4, 6);
            return `${yy}년 ${mm}월`;
        }
        return raw;
    };

    // 텍스트가 길면 특정 길이까지만 잘라서 뒤에 "..." 붙이기
    const truncateText = (text, maxLen = 50) => {
        if (!text) return '-';
        if (text.length <= maxLen) return text;
        return text.substring(0, maxLen) + '...';
    };

    // 은행별 금리 항목을 펼치거나 닫기
    const toggleBank = (id) => {
        setExpandedBankIds((prev) => {
            if (prev.includes(id)) {
                return prev.filter((bankId) => bankId !== id);
            } else {
                return [...prev, id];
            }
        });
    };

    // 상품이 이미 넘어왔으면(ApiFetch 불필요) 바로 렌더링
    // 그렇지 않으면 API에서 모든 예금/적금 배열을 가져와서 productId로 필터링
    useEffect(() => {
        if (passedProduct) {
            return;
        }

        // passedProduct가 없을 때만 실행
        const fetchDepositProduct = async () => {
            setLoading(true);
            setError(null);

            try {
                const API = axios.create({ baseURL: 'http://52.78.59.11:8080', timeout: 30000 });

                // 1) 예금 데이터 > result.baseList 로 가져오기
                const depositRes = await API.get('/api/fss/deposit');
                const depositList = Array.isArray(depositRes.data?.result?.baseList)
                    ? depositRes.data.result.baseList
                    : [];
                console.log(`[DepositProduct] 예금 데이터 개수: ${depositList.length}`);

                // 2) 적금 데이터 > result.baseList 로 가져오기
                const savingRes = await API.get('/api/fss/saving');
                const savingList = Array.isArray(savingRes.data?.result?.baseList)
                    ? savingRes.data.result.baseList
                    : [];
                console.log(`[DepositProduct] 적금 데이터 개수: ${savingList.length}`);

                // 3) 두 배열 합치기
                const allProducts = [...depositList, ...savingList];
                console.log(`[DepositProduct] 총 상품 개수: ${allProducts.length}`);

                // 4) productId에 해당하는 상품 찾기
                let selected = null;
                if (productId) {
                    selected = allProducts.find((p) => p && p.fin_prdt_cd === productId);
                }
                if (!selected && allProducts.length > 0) {
                    selected = allProducts[0];
                }

                if (selected) {
                    setProduct(selected);
                    console.log('[DepositProduct] 선택된 상품 객체 →\n', JSON.stringify(selected, null, 2));
                } else {
                    console.warn('[DepositProduct] 해당 상품을 찾을 수 없습니다. productId:', productId);
                    setError('선택하신 상품 정보를 찾을 수 없습니다.');
                }
            } catch (err) {
                console.error('[DepositProduct] API 호출 오류 →', err);
                setError('상품 정보를 불러오는 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchDepositProduct();
    }, [passedProduct, productId]);

    if (loading) {
        return (
            <>
                <Header />
                <View className="flex-1 justify-center items-center bg-white">
                    <ActivityIndicator size="large" color="#014029" />
                    <Text className="mt-2">상품 정보를 불러오는 중…</Text>
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

    // 상품 정보가 아직 없으면 빈 화면
    if (!product) {
        return null;
    }

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24,
                    }}
                >
                    {/* ─────────────────────────────────────────────────────────────
             상품 기본 정보
          ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xs text-[#6D6D6D]">예·적금 상품</Text>
                        <Text className="text-2xl font-bold text-[#014029] mb-1">
                            {product.fin_prdt_nm || '상품명 정보 없음'}
                        </Text>
                        <Text className="text-xs">{product.kor_co_nm || '금융사 정보 없음'}</Text>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             상품 혜택 (가입방법 / 만기이자 / 특별조건)
          ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 혜택</Text>
                        <View className="flex-row gap-5">
                            <View className="flex-col gap-1 w-14">
                                <Text className="text-sm font-bold">가입방법</Text>
                                <Text className="text-sm font-bold">만기이자</Text>
                                <Text className="text-sm font-bold">특별조건</Text>
                            </View>
                            <View className="flex-col gap-1 flex-1">
                                {/* join_way, mtrt_int, spcl_cnd 필드 매핑 */}
                                <Text className="text-sm">
                                    {product.join_way ? product.join_way.replace(/\n/g, ', ') : '정보 없음'}
                                </Text>
                                <Text className="text-sm">
                                    {product.mtrt_int ? product.mtrt_int.replace(/\n/g, ' ') : '정보 없음'}
                                </Text>
                                <Text className="text-sm">{product.spcl_cnd || '해당사항 없음'}</Text>
                            </View>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             가입 대상 및 정보 (가입자격 / 가입제한 / 기타사항)
          ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">가입 대상 및 정보</Text>
                        <View className="flex-row gap-5">
                            <View className="flex-col gap-1 w-14">
                                <Text className="text-sm font-bold">가입대상</Text>
                                <Text className="text-sm font-bold">가입제한</Text>
                                <Text className="text-sm font-bold">기타사항</Text>
                            </View>
                            <View className="flex-col gap-1 flex-1">
                                <Text className="text-sm">{product.join_member || '정보 없음'}</Text>
                                <Text className="text-sm">
                                    {product.join_deny === '1' ? '제한 있음' : '제한 없음'}
                                </Text>
                                <Text className="text-sm">{truncateText(product.etc_note)}</Text>
                            </View>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             상품 안내 (공시일자 / 공시기간 / 최대한도 / 기타정보)
          ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 안내</Text>
                        <View className="flex-row gap-5">
                            <View className="flex-col gap-1 w-14">
                                <Text className="text-sm font-bold">공시일자</Text>
                                <Text className="text-sm font-bold">공시기간</Text>
                                <Text className="text-sm font-bold">최대한도</Text>
                                <Text className="text-sm font-bold">기타정보</Text>
                            </View>
                            <View className="flex-col gap-1 flex-1">
                                <Text className="text-sm">{formatDate(product.dcls_strt_day)}</Text>
                                <Text className="text-sm">{formatDate(product.dcls_month)}</Text>
                                <Text className="text-sm">
                                    {product.max_limit !== null && product.max_limit !== undefined
                                        ? product.max_limit
                                        : '한도 없음'}
                                </Text>
                                <Text className="text-sm">{truncateText(product.etc_note)}</Text>
                            </View>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             은행별 금리 비교 (예시, 실제 API 필드가 있다면 그 필드를 사용하세요)
          ───────────────────────────────────────────────────────────── */}
                    <Text className="text-xl font-bold mb-4">은행별 금리 비교</Text>
                    {banks.map((bank) => (
                        <TouchableOpacity
                            key={bank.id}
                            onPress={() => toggleBank(bank.id)}
                            className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-4 rounded-2xl shadow-md"
                        >
                            <View className="flex-row justify-between items-center">
                                <Text className="text-m font-bold">{bank.name}</Text>
                                <Icon
                                    name={expandedBankIds.includes(bank.id) ? 'chevron-up' : 'chevron-down'}
                                    size={20}
                                    color="#333"
                                />
                            </View>

                            {expandedBankIds.includes(bank.id) && (
                                <View className="mt-3">
                                    <View className="flex-row gap-5">
                                        <View className="flex-col gap-1 w-14">
                                            <Text className="text-sm font-bold">기본금리</Text>
                                            <Text className="text-sm font-bold">우대금리</Text>
                                        </View>
                                        <View className="flex-col gap-1 flex-1">
                                            <Text className="text-sm">{bank.rate}</Text>
                                            <Text className="text-sm">{bank.benefit}</Text>
                                        </View>
                                    </View>
                                </View>
                            )}
                        </TouchableOpacity>
                    ))}

                    {/* ─────────────────────────────────────────────────────────────
             가입하러 가기 버튼 (실제 가입 페이지로 이동)
          ───────────────────────────────────────────────────────────── */}
                    <View className="flex-row justify-center items-center mb-10">
                        <TouchableOpacity
                            onPress={() => {
                                // 실제 가입 URL로 이동하거나, 해당 은행 앱으로 링크를 걸어줄 수 있습니다.
                                // 예: 은행별 인터넷 뱅킹 가입 페이지 열기 등
                                console.log('가입하러 가기 클릭됨');
                            }}
                            className="px-4 py-2 w-full rounded-full bg-[#014029] shadow-md items-center justify-center"
                        >
                            <Text className="text-white text-lg font-bold">가입하러 가기</Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default DepositProduct;
