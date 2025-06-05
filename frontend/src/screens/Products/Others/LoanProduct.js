// LoanProductDetail.js
import React, { useState } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    useWindowDimensions,
    Linking,
    Alert,
} from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

const LoanProductDetail = ({ route, navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const { product } = route.params; // 반드시 route.params.product를 받습니다

    const handleJoin = async () => {
        if (!product.applicationMethods) {
            Alert.alert('알림', '현재 가입 링크가 제공되지 않습니다.');
            return;
        }

        let url = '';
        if (Array.isArray(product.applicationMethods)) {
            const parts = product.applicationMethods[0].split(' ');
            url = parts.length > 1 ? parts[1] : '';
        } else {
            url = product.applicationMethods;
        }

        if (!url) {
            Alert.alert('알림', '현재 가입 링크가 제공되지 않습니다.');
            return;
        }

        const supported = await Linking.canOpenURL(url);
        if (supported) {
            await Linking.openURL(url);
        } else {
            Alert.alert('오류', '해당 URL을 열 수 없습니다.');
        }
    };

    // 날짜 형식 변환 헬퍼 (YYYYMMDD -> YYYY-MM-DD)
    const formatDate = (dateString) => {
        if (!dateString || dateString.length !== 8) return dateString || '정보 없음';
        const yy = dateString.substring(0, 4);
        const mm = dateString.substring(4, 6);
        const dd = dateString.substring(6, 8);
        return `${yy}-${mm}-${dd}`;
    };

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
                    {/* ─────────────────────────────────────────────────────
             1) 상품 이름 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xs text-[#6D6D6D]">{product.kor_co_nm || ''}</Text>
                        <Text className="text-2xl font-bold text-[#014029]">
                            {product.fin_prdt_nm || '-'}
                        </Text>
                        <Text className="text-xs">
                            {product.loan_inci_expn
                                ? product.loan_inci_expn.split('\n')[0]
                                : product.dly_rate
                                    ? product.dly_rate.split('\n')[0]
                                    : ''}
                        </Text>
                    </View>

                    {/* ─────────────────────────────────────────────────────
             2) 가입 조건 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">가입 조건</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">상품 유형</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {product.fin_prdt_nm.includes('전세') ? '전세자금대출' : '개인신용대출'}
                                </Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">가입 방법</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {product.join_way || '정보 없음'}
                                </Text>
                            </View>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────
             3) 상품 안내 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 안내</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">이자율</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {product.dly_rate ? product.dly_rate.replace(/\n/g, ' ') : '정보 없음'}
                                </Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">대출 한도</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {product.loan_lmt ? product.loan_lmt : '정보 없음'}
                                </Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">공시 기간</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">
                                        {product.dcls_strt_day || product.dcls_end_day
                                            ? `${formatDate(product.dcls_strt_day)} ~ ${formatDate(
                                                product.dcls_end_day
                                            )}`
                                            : '정보 없음'}
                                    </Text>
                                </View>
                            </View>
                        </View>
                    </View>

                    {/* ─────────────────────────────────────────────────────
             4) 수수료·비용 안내 (추가) */}
                    {(product.loan_inci_expn || product.erly_rpay_fee) && (
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-m font-bold mb-2">수수료·비용 안내</Text>
                            <View className="flex-col gap-3">
                                {product.loan_inci_expn && (
                                    <View className="flex-col gap-1">
                                        <Text className="text-sm font-bold">인지세·보증료</Text>
                                        {product.loan_inci_expn.split('\n').map((line, idx) => (
                                            <Text key={`inci-${idx}`} className="text-sm flex-wrap">
                                                {line.trim()}
                                            </Text>
                                        ))}
                                    </View>
                                )}
                                {product.erly_rpay_fee && (
                                    <View className="flex-col gap-1">
                                        <Text className="text-sm font-bold">중도상환 수수료</Text>
                                        {product.erly_rpay_fee.split('\n').map((line, idx) => (
                                            <Text key={`rpay-${idx}`} className="text-sm flex-wrap">
                                                {line.trim()}
                                            </Text>
                                        ))}
                                    </View>
                                )}
                            </View>
                        </View>
                    )}

                    {/* ─────────────────────────────────────────────────────
             5) 대상 주택 요건 */}
                    {(product.LTV || product.propertyMaxAreaSqm || product.propertyMaxAppraisedValue) && (
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-m font-bold mb-2">대상 주택 요건</Text>
                            <View className="flex-col gap-3">
                                {product.LTV && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">LTV</Text>
                                        <Text className="text-sm flex-1 flex-wrap">{product.LTV}</Text>
                                    </View>
                                )}
                                {product.propertyMaxAreaSqm && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">최대 면적 (㎡)</Text>
                                        <Text className="text-sm flex-1 flex-wrap">{product.propertyMaxAreaSqm}</Text>
                                    </View>
                                )}
                                {product.propertyMaxAppraisedValue && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">최대 감정가</Text>
                                        <Text className="text-sm flex-1 flex-wrap">
                                            {product.propertyMaxAppraisedValue}
                                        </Text>
                                    </View>
                                )}
                            </View>
                        </View>
                    )}

                    {/* ─────────────────────────────────────────────────────
             6) 가입 방법 상세 */}
                    {(product.applicationMethods ||
                        product.requiredDocuments ||
                        product.serviceBanks ||
                        product.contacts ||
                        product.borrowerCosts) && (
                            <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                                <Text className="text-m font-bold mb-2">가입 방법</Text>
                                <View className="flex-col gap-3">
                                    {product.applicationPeriodDeadline && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">신청 기간</Text>
                                            <Text className="text-sm flex-1 flex-wrap">
                                                {product.applicationPeriodDeadline}
                                            </Text>
                                        </View>
                                    )}
                                    {product.applicationMethods && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">신청 방법</Text>
                                            <Text className="text-sm flex-1 flex-wrap">
                                                {Array.isArray(product.applicationMethods)
                                                    ? product.applicationMethods.join(', ')
                                                    : product.applicationMethods}
                                            </Text>
                                        </View>
                                    )}
                                    {product.requiredDocuments && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">제출 서류</Text>
                                            <Text className="text-sm flex-1 flex-wrap">
                                                {Array.isArray(product.requiredDocuments)
                                                    ? product.requiredDocuments.join(', ')
                                                    : product.requiredDocuments}
                                            </Text>
                                        </View>
                                    )}
                                    {product.serviceBanks && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">취급 은행</Text>
                                            <Text className="text-sm flex-1 flex-wrap">
                                                {Array.isArray(product.serviceBanks)
                                                    ? product.serviceBanks.join(', ')
                                                    : product.serviceBanks}
                                            </Text>
                                        </View>
                                    )}
                                    {product.contacts && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">문의처</Text>
                                            <Text className="text-sm flex-1 flex-wrap">{product.contacts}</Text>
                                        </View>
                                    )}
                                    {product.borrowerCosts && (
                                        <View className="flex-row gap-2">
                                            <Text className="text-sm font-bold w-20">신청자 부담 비용</Text>
                                            <Text className="text-sm flex-1 flex-wrap">
                                                {product.borrowerCosts}
                                            </Text>
                                        </View>
                                    )}
                                </View>
                            </View>
                        )}
                </ScrollView>

                {/* ─────────────────────────────────────────────────────
           7) “가입하러 가기” 버튼 */}
                <View className="flex-row justify-center items-center">
                    <TouchableOpacity
                        onPress={handleJoin}
                        className="m-4 px-4 py-2 w-full rounded-full bg-[#014029] shadow-md items-center justify-center"
                    >
                        <Text className="text-white text-lg font-bold">가입하러 가기</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </>
    );
};

export default LoanProductDetail;
