import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, FlatList } from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

const YouthProduct = ({ route, navigation }) => {
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

    // 은행 정보 파싱
    const parseBanks = () => {
        if (!product.providersDetail) return [];
        try {
            const banksData = JSON.parse(product.providersDetail.join(''));
            return banksData.map((bank, index) => ({
                id: `bank-${index}`,
                name: bank.bank,
                rate: bank.interestRate,
                benefit: bank.specialNote || ''
            }));
        } catch (e) {
            return [];
        }
    };

    const banks = parseBanks();

    // 날짜 포맷팅 함수
    const formatDate = (dateString) => {
        if (!dateString) return '상시';
        const date = new Date(dateString);
        return date.toLocaleDateString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        });
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
                        <Text className="text-xs">{product.summary || product.Summary || ''}</Text>
                    </View>

                    {/* 상품 혜택 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 혜택</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">지원금액</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.governmentSupport?.contribution || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">세제혜택</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.governmentSupport?.taxBenefit || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">금리</Text>
                                <View className="flex-1">
                                    <Text className="text-sm flex-wrap">{product.interest?.baseRate || '해당없음'}</Text>
                                    {product.interest?.note && (
                                        <Text className="text-sm flex-wrap">{product.interest.note}</Text>
                                    )}
                                </View>
                            </View>
                        </View>
                    </View>

                    {/* 가입 대상 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">가입 대상</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">나이</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.eligibility?.age || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">소득조건</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.eligibility?.personalIncome || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">가구소득</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.eligibility?.householdIncome || '해당없음'}</Text>
                            </View>
                        </View>
                    </View>

                    {/* 상품 안내 */}
                    <View className="flex-col justify-center gap-1 mb-5 bg-[#F9F9F9] p-6 rounded-2xl shadow-md">
                        <Text className="text-m font-bold mb-2">상품 안내</Text>
                        <View className="flex-col gap-3">
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">가입기간</Text>
                                <Text className="text-sm flex-1 flex-wrap">
                                    {product.applicationPeriod ? 
                                        `${formatDate(product.applicationPeriod.start)} ~ ${formatDate(product.applicationPeriod.end)}` 
                                        : '상시'}
                                </Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">가입금액</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.monthlyDepositLimit || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">계약기간</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.contractPeriod || '해당없음'}</Text>
                            </View>
                            <View className="flex-row">
                                <Text className="text-sm font-bold w-20">유의사항</Text>
                                <Text className="text-sm flex-1 flex-wrap">{product.cautions?.join(', ') || '해당없음'}</Text>
                            </View>
                        </View>
                    </View>

                    {/* 은행별 금리 비교 */}
                    {banks.length > 0 && (
                        <>
                            <Text className="text-xl font-bold m-4">은행별 금리 비교</Text>
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
                                                    <Text className="text-sm font-bold">기본 금리</Text>
                                                    <Text className="text-sm font-bold">우대 금리</Text>
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
                        </>
                    )}
                </ScrollView>

                {product.applicationUrl && (
                    <View className="flex-row justify-center items-center">
                        <TouchableOpacity
                            onPress={() => navigation.navigate('AddYouthInfo')}
                            className="m-4 px-4 py-2 w-full rounded-full bg-[#014029] shadow-md items-center justify-center">
                            <Text className="text-white text-lg font-bold">가입하러 가기</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>
        </>
    );
};

export default YouthProduct;


