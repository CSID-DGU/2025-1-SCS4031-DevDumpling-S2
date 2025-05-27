import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView, TouchableOpacity, ActivityIndicator, Alert } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { fetchChallengeDetail, joinChallenge } from './ChallengeApi';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function ChallengeDetailScreen({ route }) {
  const { challengeId } = route.params;
  const navigation = useNavigation();
  const [challenge, setChallenge] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const checkLoginStatus = async () => {
      const token = await AsyncStorage.getItem('userToken');
      setIsLoggedIn(!!token);
    };

    checkLoginStatus();
  }, []);

  useEffect(() => {
    const loadChallengeDetail = async () => {
      try {
        const data = await fetchChallengeDetail(challengeId);
        setChallenge(data);
        setLoading(false);
      } catch (err) {
        console.error('챌린지 상세 정보 불러오기 오류:', err);
        setError('챌린지 정보를 불러오는 중 오류가 발생했습니다.');
        setLoading(false);
      }
    };

    loadChallengeDetail();
  }, [challengeId]);

  const handleJoinChallenge = async () => {
    if (!isLoggedIn) {
      Alert.alert(
        '로그인 필요',
        '챌린지에 참여하려면 로그인이 필요합니다.',
        [
          { text: '취소', style: 'cancel' },
          { text: '로그인', onPress: () => navigation.navigate('Login') }
        ]
      );
      return;
    }

    try {
      setLoading(true);
      await joinChallenge(challengeId);
      Alert.alert('성공', '챌린지 참여가 완료되었습니다!', [
        { text: '확인', onPress: () => navigation.goBack() }
      ]);
    } catch (err) {
      Alert.alert('오류', err.message || '챌린지 참여 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <View className="flex-1 justify-center items-center bg-Fineed-background">
        <ActivityIndicator size="large" color="#014029" />
      </View>
    );
  }

  if (error || !challenge) {
    return (
      <View className="flex-1 justify-center items-center bg-Fineed-background">
        <Header />
        <Text className="text-red-500 text-center px-4">{error || '챌린지 정보를 불러올 수 없습니다.'}</Text>
        <TouchableOpacity
          className="mt-4 bg-Fineed-green py-2 px-4 rounded-lg"
          onPress={() => navigation.goBack()}
        >
          <Text className="text-white">돌아가기</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // API에서 받아온 챌린지 데이터 사용
  const challengeData = challenge || {
    id: 1,
    title: '떠나자!',
    subtitle: '여행 자금 모으기',
    description: '매일 커피 한 잔 줄이고 여행 자금 모으기! 한 달 동안 커피 대신 물을 마시고 저축해보세요.',
    category: '저축 챌린지',
    startDate: '2025-06-01',
    endDate: '2025-06-30',
    targetAmount: 150000,
    participantCount: 89,
    likes: 130,
    difficulty: '중간',
    isPrivate: false,
    createdBy: '파이니드',
    imageUrl: 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/TRAVEL.png',
    rules: [
      '매일 커피 한 잔 줄이기',
      '절약한 금액 기록하기',
      '매주 저축 내역 공유하기'
    ],
    participants: [
      { nickname: '사용자1', profileImage: null, progress: 80 },
      { nickname: '사용자2', profileImage: null, progress: 65 },
      { nickname: '사용자3', profileImage: null, progress: 45 },
    ]
  };

  return (
    <View className="flex-1 bg-Fineed-background">
      <Header />
      <ScrollView className="flex-1 px-4">
        {/* 챌린지 헤더 */}
        <View className="bg-white rounded-[20px] p-5 shadow-md mb-4 mt-2">
          <View className="flex-row justify-between mb-2">
            <Text className="text-[12px] text-[#6D6D6D]">{challengeData.category}</Text>
            <Text className="text-[12px] text-[#6D6D6D]">
              난이도: {challengeData.difficulty || '중간'}
            </Text>
          </View>

          <View className="flex-row items-center flex-wrap mb-4">
            <Image
              source={{ uri: challengeData.imageUrl }}
              style={{ width: 80, height: 80 }}
              resizeMode="contain"
              className="mb-2"
            />
            <View className="ml-2 flex-1">
              <Text className="text-[24px] font-bold text-black mb-1">{challengeData.title}</Text>
              {challengeData.subtitle && (
                <Text className="text-[20px] font-bold text-black">{challengeData.subtitle}</Text>
              )}
            </View>
          </View>

          <View className="flex-row items-center justify-between mb-4">
            <View className="flex-row items-center">
              <Icon name="heart-outline" size={16} color="#6D6D6D" />
              <Text className="text-[12px] text-[#6D6D6D] ml-1">좋아요 {challengeData.likes}</Text>
            </View>
            <View className="flex-row items-center">
              <Icon name="people-outline" size={16} color="#6D6D6D" />
              <Text className="text-[12px] text-[#6D6D6D] ml-1">{challengeData.participantCount}명 참여 중</Text>
            </View>
            <View className="flex-row items-center">
              <Icon name="calendar-outline" size={16} color="#6D6D6D" />
              <Text className="text-[12px] text-[#6D6D6D] ml-1">
                {challengeData.startDate?.substring(0, 10)} ~ {challengeData.endDate?.substring(0, 10)}
              </Text>
            </View>
          </View>

          <TouchableOpacity
            className="bg-Fineed-green py-3 rounded-lg items-center"
            onPress={handleJoinChallenge}
          >
            <Text className="text-white font-medium">챌린지 참여하기</Text>
          </TouchableOpacity>
        </View>

        {/* 챌린지 설명 */}
        <View className="bg-white rounded-[20px] p-5 shadow-md mb-4">
          <Text className="text-[16px] font-bold mb-3">챌린지 설명</Text>
          <Text className="text-[14px] text-black mb-4">
            {challengeData.description || '챌린지 설명이 없습니다.'}
          </Text>

          {challengeData.rules && challengeData.rules.length > 0 && (
            <View className="mb-2">
              <Text className="text-[16px] font-bold mb-2">챌린지 규칙</Text>
              {challengeData.rules.map((rule, index) => (
                <View key={index} className="flex-row items-center mb-2">
                  <Icon name="checkmark-circle" size={16} color="#014029" />
                  <Text className="text-[14px] text-black ml-2">{rule}</Text>
                </View>
              ))}
            </View>
          )}

          {challengeData.targetAmount > 0 && (
            <View className="mt-3">
              <Text className="text-[16px] font-bold mb-2">목표 금액</Text>
              <Text className="text-[14px] text-black">
                {challengeData.targetAmount.toLocaleString()}원
              </Text>
            </View>
          )}
        </View>

        {/* 참여자 현황 */}
        {challengeData.participants && challengeData.participants.length > 0 && (
          <View className="bg-white rounded-[20px] p-5 shadow-md mb-4">
            <Text className="text-[16px] font-bold mb-3">참여자 현황</Text>
            {challengeData.participants.map((participant, index) => (
              <View key={index} className="flex-row items-center justify-between mb-3">
                <View className="flex-row items-center">
                  <View className="w-8 h-8 bg-gray-200 rounded-full items-center justify-center">
                    {participant.profileImage ? (
                      <Image
                        source={{ uri: participant.profileImage }}
                        className="w-8 h-8 rounded-full"
                      />
                    ) : (
                      <Icon name="person" size={16} color="#6D6D6D" />
                    )}
                  </View>
                  <Text className="text-[14px] text-black ml-2">{participant.nickname}</Text>
                </View>
                <View className="flex-row items-center">
                  <Text className="text-[12px] text-[#6D6D6D] mr-2">{participant.progress}%</Text>
                  <View className="w-24 h-2 bg-gray-200 rounded-full">
                    <View
                      className="h-2 bg-Fineed-green rounded-full"
                      style={{ width: `${participant.progress}%` }}
                    />
                  </View>
                </View>
              </View>
            ))}
          </View>
        )}

        {/* 관련 챌린지 */}
        <View className="bg-white rounded-[20px] p-5 shadow-md mb-6">
          <Text className="text-[16px] font-bold mb-3">관련 챌린지</Text>
          <Text className="text-center py-4 text-[#6D6D6D]">
            관련 챌린지가 없습니다.
          </Text>
        </View>
      </ScrollView>
    </View>
  );
}
