import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, TextInput } from 'react-native';
import Header from '../../../components/layout/Header';

const LoanProduct = ({ navigation }) => {

    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    const [age, setAge] = useState('');

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding + 5,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    {/* 제목 */}
                    <View className="items-left pb-10">
                        <Text className="text-3xl font-bold">추가 정보 입력 중...</Text>
                    </View>

                    {/* 기본 항목 */}
                    <View className="items-left pb-5 gap-5">
                        <Text className="text-xl font-medium text-[#014029]">기본 항목</Text>

                        {/* 성별 */}
                        <View className="flex-row gap-3 items-center justify-start">
                            <Text className="text-xl font-bold pr-5">성별</Text>
                            <TouchableOpacity
                                className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md items-center justify-center">
                                <Text className="text-m">여성</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md items-center justify-center">
                                <Text className="text-m">남성</Text>
                            </TouchableOpacity>
                        </View>

                        {/* 나이 */}
                        <View className="flex-col gap-3 items-start justify-center">
                            <Text className="text-xl font-bold pr-5">나이</Text>
                            <View className="flex-row gap-3 items-center justify-start">
                                <Text className="text-lg">만</Text>
                                <TextInput
                                    keyboardType="numeric"
                                    className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md text-center"
                                    placeholder="20"
                                    placeholderTextColor="#999"
                                    value={age}
                                    onChangeText={setAge}
                                    />
                                <Text className="text-lg">세</Text>
                            </View>
                        </View>
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default LoanProduct;


