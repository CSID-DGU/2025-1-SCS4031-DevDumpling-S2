import { useState } from 'react';
import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, Modal } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';

const UserTypeNone = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const [showUserTypeModal, setShowUserTypeModal] = useState(true);

    const navigation = useNavigation();

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
                </ScrollView>

                <Modal
                    visible={showUserTypeModal}
                    transparent={true}
                    animationType="fade"
                >
                    <View className="flex-1 justify-center items-center bg-black/50">
                    <View className="bg-white p-5 rounded-2xl w-4/5 max-w-md items-center shadow-lg">
                        <Text className="text-xl font-bold text-center mb-1">
                        아직 사용자 유형이
                        </Text>
                        <Text className="text-xl font-bold text-center mb-5">
                        분석되지 않았어요!
                        </Text>

                        <Text className="text-center text-gray-600 mb-2">
                        당신에게 꼭 맞는 퀴즈를 위해
                        </Text>
                        <Text className="text-center text-gray-600 mb-4">
                        사용자 유형 분석이 필요해요
                        </Text>

                        <TouchableOpacity
                        onPress={() => navigation.navigate('UserTypeLoading')}
                        className="bg-Fineed-green py-3 px-5 rounded-lg w-full items-center mt-2"
                        >
                        <Text className="text-white font-medium">유형 분석하기</Text>
                        </TouchableOpacity>
                    </View>
                    </View>
                </Modal>
            </View>
        </>
    );
};

export default UserTypeNone;
