import React, { useState } from 'react';
import { Alert } from 'react-native';
import { View, Text, ScrollView, TouchableOpacity, useWindowDimensions, TextInput, Modal, Pressable } from 'react-native';
import Header from '../../../components/layout/Header';

const AddYouthInfo = ({ navigation }) => {

    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    const [gender, setGender] = useState('');
    const [age, setAge] = useState('');
    const [income, setIncome] = useState('');
    const [region, setRegion] = useState('');
    const [regionModalVisible, setRegionModalVisible] = useState(false);
    const [familyIncome, setFamilyIncome] = useState('');
    const [familyMember, setFamilyMember] = useState('');
    const [militaryService, setMilitaryService] = useState('0');

    const isFormValid =
        gender !== '' &&
        age !== '' &&
        income !== '' &&
        region !== '' &&
        familyIncome !== '' &&
        familyMember !== '' &&
        militaryService !== '';



    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-2 px-4 pb-12">
                <ScrollView
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding + 5,
                        paddingTop: 16,
                        paddingBottom: 24,
                        justifyContent: 'center'
                    }}>
                    {/* 제목 */}
                    <View className="items-left pb-10">
                        <Text className="text-3xl font-bold">추가 정보 입력 중...</Text>
                    </View>

                    {/* 기본 정보 */}
                    <View className="items-left pb-5 gap-10">
                        {/* 성별 */}
                        <View className="flex-row gap-3 items-center justify-start">
                            <Text className="text-xl font-bold pr-5">성별</Text>
                            <TouchableOpacity
                                onPress={() => setGender('female')}
                                className={`px-4 py-2 w-16 rounded-full shadow-md items-center justify-center
                                ${gender === 'female' ? 'bg-[#014029]' : 'bg-[#F9F9F9]'}`}>
                                <Text className={`text-m ${gender === 'female' ? 'text-white' : 'text-[#014029]'}`}>여성</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                onPress={() => setGender('male')}
                                className={`px-4 py-2 w-16 rounded-full shadow-md items-center justify-center
                                ${gender === 'male' ? 'bg-[#014029]' : 'bg-[#F9F9F9]'}`}>
                                <Text className={`text-m ${gender === 'male' ? 'text-white' : 'text-[#014029]'}`}>남성</Text>
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
                                    placeholderTextColor="#D3D3D3"
                                    value={age}
                                    onChangeText={setAge}
                                    />
                                <Text className="text-lg">세</Text>
                            </View>
                        </View>

                        {/* 개인소득 */}
                        <View className="flex-col gap-3 items-start justify-center">
                            <Text className="text-xl font-bold pr-5">개인소득</Text>
                            <View className="flex-row gap-3 items-center justify-start">
                                <Text className="text-lg">월</Text>
                                <TextInput
                                    keyboardType="numeric"
                                    className="bg-[#F9F9F9] px-4 py-2 w-20 rounded-full shadow-md text-center"
                                    placeholder="300"
                                    placeholderTextColor="#D3D3D3"
                                    value={income}
                                    onChangeText={setIncome}
                                    />
                                <Text className="text-lg">만원</Text>
                            </View>
                        </View>

                        {/* 거주지역 */}
                        <View className="flex-row gap-3 items-center justify-start">
                            <Text className="text-xl font-bold pr-5">거주 지역</Text>
                            <TouchableOpacity
                                onPress={() => setRegionModalVisible(true)}
                                className="bg-[#F9F9F9] px-4 py-2 w-48 rounded-full shadow-md items-center justify-center">
                                <Text className="text-center text-m">{region || '지역 선택    '}</Text>
                            </TouchableOpacity>
                        </View>

                        {/* 가구 총 소득 */}
                        <View className="flex-col gap-3 items-start justify-center">
                            <Text className="text-xl font-bold pr-5">가구 총 소득</Text>
                            <View className="flex-row gap-3 items-center justify-start">
                                <Text className="text-lg">월</Text>
                                <TextInput
                                    keyboardType="numeric"
                                    className="bg-[#F9F9F9] px-4 py-2 w-24 rounded-full shadow-md text-center"
                                    placeholder="500"
                                    placeholderTextColor="#D3D3D3"
                                    value={familyIncome}
                                    onChangeText={setFamilyIncome}
                                    />
                                <Text className="text-lg">만원</Text>
                            </View>
                        </View>

                        {/* 가구원 수 */}
                        <View className="flex-col gap-3 items-start justify-center">
                            <Text className="text-xl font-bold pr-5">가구원 수</Text>
                            <View className="flex-row gap-3 items-center justify-start">
                                <Text className="text-lg">총</Text>
                                <TextInput
                                    keyboardType="numeric"
                                    className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md text-center"
                                    placeholder="3"
                                    placeholderTextColor="#D3D3D3"
                                    value={familyMember}
                                    onChangeText={setFamilyMember}
                                    />
                                <Text className="text-lg">명</Text>
                            </View>
                        </View>

                        {/* 병역이행개월수 */}
                        <View className="flex-col gap-3 items-start justify-center">
                            <Text className="text-xl font-bold pr-5">병역이행개월수</Text>
                            <Text className="text-sm text-[#6D6D6D]">*해당 사항이 없을 경우 0개월로 유지해주세요</Text>
                            <View className="flex-row gap-3 items-center justify-start">
                                <Text className="text-lg">총</Text>
                                <TextInput
                                    keyboardType="numeric"
                                    className="bg-[#F9F9F9] px-4 py-2 w-16 rounded-full shadow-md text-center"
                                    placeholder="0"
                                    placeholderTextColor="#D3D3D3"
                                    value={militaryService}
                                    onChangeText={setMilitaryService}
                                    />
                                <Text className="text-lg">개월</Text>
                            </View>
                        </View>
                    </View>
                </ScrollView>

                {/* 거주지역 모달 */}
                <Modal
                    animationType="fade"
                    transparent={true}
                    visible={regionModalVisible}
                    onRequestClose={() => setRegionModalVisible(false)}
                    >
                    <Pressable
                        onPress={() => setRegionModalVisible(false)}
                        style={{
                        flex: 1,
                        backgroundColor: 'rgba(0, 0, 0, 0.4)',
                        justifyContent: 'center',
                        alignItems: 'center',
                        }}
                    >
                        <Pressable
                        onPress={() => {}}
                        style={{
                            backgroundColor: 'white',
                            borderRadius: 16,
                            padding: 24,
                            width: 280,
                            elevation: 5,
                        }}
                        >
                        {['서울', '경기', '인천', '강원', '충청', '전라', '경상', '제주'].map((city) => (
                            <TouchableOpacity
                            key={city}
                            onPress={() => {
                                setRegion(city);
                                setRegionModalVisible(false);
                            }}
                            style={{
                                paddingVertical: 12,
                                borderBottomWidth: 1,
                                borderBottomColor: '#ccc',
                            }}
                            >
                            <Text style={{ textAlign: 'center', fontSize: 18 }}>{city}</Text>
                            </TouchableOpacity>
                        ))}
                        </Pressable>
                    </Pressable>
                    </Modal>

                    <View className="flex-row justify-center items-center">
                        <TouchableOpacity
                            disabled={!isFormValid}
                            className={`m-4 px-4 py-2 w-full rounded-full shadow-md items-center justify-center ${
                            isFormValid ? 'bg-[#014029]' : 'bg-[#D3D3D3]'
                            }`}
                            onPress={() => {
                            if (isFormValid) {
                                console.log('입력 완료!');
                                navigation.goBack(); // or 다음 화면으로 이동
                            } else {
                                Alert.alert('모든 항목을 입력해주세요.');
                            }
                            }}
                        >
                            <Text className="text-white text-lg font-bold">입력 완료</Text>
                        </TouchableOpacity>
                    </View>

            </View>
        </>
    );
};

export default AddYouthInfo;


