import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, FlatList, Linking, Alert } from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

const YouthLoanProduct = ({ route, navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const [expandedBankIds, setExpandedBankIds] = useState([]);
    const { product } = route.params;

    const toggleBank = (id) => {
        setExpandedBankIds(prev => {
            if (prev.includes(id)) {
                return prev.filter(bankId => bankId !== id);
            } else {
                return [...prev, id];
            }
        });
    };

    const handleJoin = async () => {
        if (!product.applicationMethods) {
            Alert.alert('알림', '현재 가입 링크가 제공되지 않습니다.');
            return;
        }

        // applicationMethods가 배열인 경우 첫 번째 URL을 사용
        const url = Array.isArray(product.applicationMethods) 
            ? product.applicationMethods[0].split(' ')[1] 
            : product.applicationMethods;

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

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    {/* 상품 이름 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-xs text-[#6D6D6D]">{product.category || ''}</Text>
                        <Text className="text-2xl font-bold text-[#014029]">{product.productName}</Text>
                        <Text className="text-xs">{product.Summary || product.summary || ''}</Text>
                    </View>

                    {/* 가입 조건 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">가입 조건</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">나이</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.eligibility?.ageRange || '해당없음'}</Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">주거 상태</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.eligibility?.householdStatus || '해당없음'}</Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">소득 상한</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.eligibility?.incomeCeiling || '해당없음'}</Text>
                                </View>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">순자산 상한</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.eligibility?.netAssetCeiling || '해당없음'}</Text>
                                </View>
                            </View>
                        </View>
                    </View>

                    {/* 상품 안내 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 안내</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">이자율</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.interestRate || '해당없음'}</Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">대출 한도</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.loanLimit || '해당없음'}</Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">대출 기간</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.termDetails || '해당없음'}</Text>
                                </View>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">상환 방식</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.repaymentMethod || '해당없음'}</Text>
                                </View>
                            </View>
                        </View>
                    </View>

                    {/* 대상 주택 요건 - 해당 데이터가 있을 때만 표시 */}
                    {(product.depositLimitMetro || product.depositLimitOther || product.propertyMaxAreaSqm || 
                      product.propertyMaxAppraisedValue || product.LTV) && (
                        <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                            <Text className="text-m font-bold mb-2">대상 주택 요건</Text>
                            <View className="flex-col gap-3">
                                {product.depositLimitMetro && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">수도권 보증금 대출 한도</Text>
                                        <Text className="text-sm flex-1 flex-wrap">{product.depositLimitMetro}</Text>
                                    </View>
                                )}
                                {product.depositLimitOther && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">비수도권 보증금 대출 한도</Text>
                                        <Text className="text-sm flex-1 flex-wrap">{product.depositLimitOther}</Text>
                                    </View>
                                )}
                                {product.propertyMaxAreaSqm && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">대상 주택 최대 면적 (㎡)</Text>
                                        <View className="flex-1">
                                            <Text className="text-sm flex-wrap">{product.propertyMaxAreaSqm}</Text>
                                        </View>
                                    </View>
                                )}
                                {product.propertyMaxAppraisedValue && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">대상 주택 최대 감정가</Text>
                                        <View className="flex-1">
                                            <Text className="text-sm flex-wrap">{product.propertyMaxAppraisedValue}</Text>
                                        </View>
                                    </View>
                                )}
                                {product.LTV && (
                                    <View className="flex-row gap-3">
                                        <Text className="text-sm font-bold w-40">주택담보인정비율 (Loan To Value)</Text>
                                        <View className="flex-1">
                                            <Text className="text-sm flex-wrap">{product.LTV}</Text>
                                        </View>
                                    </View>
                                )}
                            </View>
                        </View>
                    )}

                    {/* 가입 방법 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">가입 방법</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">신청 기간</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.applicationPeriodDeadline || '해당없음'}</Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">신청 방법</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {Array.isArray(product.applicationMethods) 
                                        ? product.applicationMethods.join(', ') 
                                        : product.applicationMethods || '해당없음'}
                                </Text>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">제출 서류</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">
                                        {Array.isArray(product.requiredDocuments) 
                                            ? product.requiredDocuments.join(', ') 
                                            : product.requiredDocuments || '해당없음'}
                                    </Text>
                                </View>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">취급 은행</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">
                                        {Array.isArray(product.serviceBanks) 
                                            ? product.serviceBanks.join(', ') 
                                            : product.serviceBanks || '해당없음'}
                                    </Text>
                                </View>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">문의처</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.contacts || '해당없음'}</Text>
                                </View>
                            </View>
                            <View className="flex-row gap-2">
                                <Text className="text-sm font-bold w-20">신청자 부담 비용</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.borrowerCosts || '해당없음'}</Text>
                                </View>
                            </View>
                        </View>
                    </View>
                </ScrollView>

                <View className="flex-row justify-center items-center">
                    <TouchableOpacity
                        onPress={handleJoin}
                        className="m-4 px-4 py-2 w-full rounded-full bg-[#014029] shadow-md items-center justify-center">
                        <Text className="text-white text-lg font-bold">가입하러 가기</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </>
    );
};

export default YouthLoanProduct;


