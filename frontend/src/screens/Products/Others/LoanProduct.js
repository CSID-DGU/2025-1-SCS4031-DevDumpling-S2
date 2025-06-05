import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import Header from '../../../components/layout/Header';
import axios from 'axios';

const LoanProduct = ({ navigation }) => {
    const [rentLoans, setRentLoans] = useState([]);
    const [creditLoans, setCreditLoans] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [activeTab, setActiveTab] = useState('rent'); // 'rent' 또는 'credit'

    // API에서 대출 상품 정보 가져오기
    useEffect(() => {
        const fetchLoans = async () => {
            try {
                setLoading(true);
                // API 서버 기본 URL
                const API_BASE_URL = 'http://52.78.59.11:8080';
                
                // 두 가지 대출 관련 API 호출 (전세자금대출, 개인신용대출)
                const [rentLoanResponse, creditLoanResponse] = await Promise.all([
                    axios.get(`${API_BASE_URL}/api/fss/rentloan`),
                    axios.get(`${API_BASE_URL}/api/fss/creditloan`)
                ]);
                
                // 백엔드 API 응답 구조에 맞게 데이터 추출
                const rentLoanData = rentLoanResponse.data?.baseList?.result || [];
                const creditLoanData = creditLoanResponse.data?.baseList?.result || [];
                
                console.log('전세자금대출 데이터 개수:', rentLoanData.length);
                console.log('개인신용대출 데이터 개수:', creditLoanData.length);
                
                setRentLoans(rentLoanData);
                setCreditLoans(creditLoanData);
                setLoading(false);
            } catch (err) {
                console.error('대출 상품 데이터 가져오기 오류:', err);
                setError('대출 상품 정보를 불러오는데 실패했습니다.');
                setLoading(false);
            }
        };

        fetchLoans();
    }, []);
    
    // 표시할 대출 상품들
    const displayLoans = activeTab === 'rent' ? rentLoans : creditLoans;
    
    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">대출 상품</Text>
                    </View>
                </View>
                
                {/* 대출 상품 탭 */}
                <View className="flex-row rounded-lg overflow-hidden mb-4">
                    <TouchableOpacity 
                        className={`flex-1 py-3 ${activeTab === 'rent' ? 'bg-[#014029]' : 'bg-gray-200'}`}
                        onPress={() => setActiveTab('rent')}
                    >
                        <Text className={`text-center font-medium ${activeTab === 'rent' ? 'text-white' : 'text-gray-700'}`}>전세자금대출</Text>
                    </TouchableOpacity>
                    <TouchableOpacity 
                        className={`flex-1 py-3 ${activeTab === 'credit' ? 'bg-[#014029]' : 'bg-gray-200'}`}
                        onPress={() => setActiveTab('credit')}
                    >
                        <Text className={`text-center font-medium ${activeTab === 'credit' ? 'text-white' : 'text-gray-700'}`}>개인신용대출</Text>
                    </TouchableOpacity>
                </View>

                <ScrollView className="flex-1 p-4">
                    {loading ? (
                        <View className="flex items-center justify-center py-10">
                            <ActivityIndicator size="large" color="#014029" />
                            <Text className="mt-2 text-[#014029]">대출 상품 정보를 불러오는 중...</Text>
                        </View>
                    ) : error ? (
                        <View className="flex items-center justify-center py-10">
                            <Text className="text-red-500">{error}</Text>
                        </View>
                    ) : displayLoans.length > 0 ? (
                        displayLoans.map((loan, index) => (
                            <View key={index} className="mb-4 bg-white p-4 rounded-xl shadow">
                                <Text className="text-lg font-bold text-[#014029]">{loan.fin_prdt_nm || '상품명'}</Text>
                                <Text className="text-sm text-gray-600 mb-2">{loan.kor_co_nm || '금융사'}</Text>
                                
                                <View className="bg-gray-100 p-3 rounded-md mt-2">
                                    <View className="flex-row justify-between mb-1">
                                        <Text className="text-sm">금리:</Text>
                                        <Text className="text-sm font-bold">
                                            {loan.lend_rate_min && loan.lend_rate_max ? 
                                              `${loan.lend_rate_min}% ~ ${loan.lend_rate_max}%` : 
                                              (loan.lend_rate_min ? `${loan.lend_rate_min}%` : '-')}
                                        </Text>
                                    </View>
                                    <View className="flex-row justify-between mb-1">
                                        <Text className="text-sm">{activeTab === 'rent' ? '대출한도:' : '신용한도:'}</Text>
                                        <Text className="text-sm font-bold">
                                            {loan.loan_lmt ? `최대 ${Number(loan.loan_lmt).toLocaleString()}원` : '-'}
                                        </Text>
                                    </View>
                                    {loan.join_way && (
                                        <View className="flex-row justify-between">
                                            <Text className="text-sm">가입방법:</Text>
                                            <Text className="text-sm">{loan.join_way}</Text>
                                        </View>
                                    )}
                                </View>
                            </View>
                        ))
                    ) : (
                        <View className="items-center py-10">
                            <Text className="text-center text-gray-500">현재 제공되는 {activeTab === 'rent' ? '전세자금' : '개인신용'}대출 상품이 없습니다</Text>
                        </View>
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

export default LoanProduct;


