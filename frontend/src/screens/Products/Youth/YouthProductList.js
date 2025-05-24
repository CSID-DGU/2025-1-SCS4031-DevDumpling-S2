import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions } from 'react-native';
import Header from '../../../components/layout/Header';

const YouthProduct = ({ navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    const navigateToAddYouthInfo = () => {
        navigation.navigate('AddYouthInfo');
    };

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>

                    {/* 추가 정보 안내란 */}
                    <View className="items-left pb-5">
                        <TouchableOpacity onPress={navigateToAddYouthInfo}>
                            <Text className="text-2xl font-bold">추가 정보를 입력하면</Text>
                            <Text className="text-2xl font-bold">나에게 더 맞는 상품을 볼 수 있어요 →</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 1차 정보 수집 버튼 모음 */}
                    <View className="flex-row justify-start gap-3 pb-5">
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md items-center justify-center">
                            <Text className="text-sm">성별</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md items-center justify-center">
                            <Text className="text-sm">나이</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            className="bg-[#F9F9F9] px-4 py-2 w-30 rounded-full shadow-md items-center justify-center">
                            <Text className="text-sm">개인 소득</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 신청 가능 상품 리스트 */}
                    <View className="flex-col justify-center gap-3 mb-5">
                        <TouchableOpacity className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md">
                            <Text className="text-xs text-[#6D6D6D] mb-2">자유적금·자산형성·세제혜택</Text>
                            <Text className="text-2xl font-bold text-[#014029] mb-2">청년내일저축계좌</Text>
                            <Text className="text-sm">신청기간: 5월 2일~5월 21일</Text>
                            <Text className="text-sm">저소득 근로 청년에게 장려금을 얹어주는 고금리 적금</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 구분선 */}
                    <View className="border-t border-gray-200 w-full mb-5" />

                    {/* 신청 불가능 상품 리스트 */}
                    <View className="flex-col justify-center gap-3">
                        <Text className="text-lg font-bold">❌ 현재 가입 기간이 아니에요</Text>

                        <TouchableOpacity className="bg-[#D9D9D9] p-4 rounded-2xl shadow-md">
                            <Text className="text-xs text-[#6D6D6D] mb-2">자유적금·자산형성·세제혜택</Text>
                            <Text className="text-2xl font-bold text-[#014029] mb-2">청년내일저축계좌</Text>
                            <Text className="text-sm">신청기간: 5월 2일~5월 21일</Text>
                            <Text className="text-sm">저소득 근로 청년에게 장려금을 얹어주는 고금리 적금</Text>
                        </TouchableOpacity>
                    </View>

                </ScrollView>
            </View>
        </>
    );
};

export default YouthProduct;


