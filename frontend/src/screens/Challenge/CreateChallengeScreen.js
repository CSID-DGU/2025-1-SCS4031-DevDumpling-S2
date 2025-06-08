import React, { useState } from 'react';
import {
  SafeAreaView,
  ScrollView,
  View,
  Text,
  TouchableOpacity,
  TextInput,
  Switch,
  Alert,
  Modal,
  Pressable
} from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Icon from 'react-native-vector-icons/Ionicons';
import Header from '../../components/layout/Header';
import { createChallenge } from './ChallengeApi';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function CreateChallengeScreen() {
  const navigation = useNavigation();
  const [category, setCategory] = useState('FOOD');
  const [categoryLabel, setCategoryLabel] = useState('식비');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [goalType, setGoalType] = useState('TOTAL');
  const [goalValue, setGoalValue] = useState('');
  const [startDate, setStartDate] = useState(formatDate(new Date()));
  const [endDate, setEndDate] = useState(formatDate(new Date(Date.now() + 30 * 24 * 3600 * 1000)));
  const [isPublic, setIsPublic] = useState(true);
  const [showGoalModal, setShowGoalModal] = useState(false);
  const [showCatModal, setShowCatModal] = useState(false);
  const [selectedGoalLabel, setSelectedGoalLabel] = useState('총 식비 줄이기');
  const [maxParticipants, setMaxParticipants] = useState('50');

  function formatDate(d) {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${dd}`;
  }

  const goalOptions = [
    { label: '총 식비 줄이기', value: 'TOTAL', unit: '원 이하' },
    { label: '배달음식 줄이기', value: 'DELIVERY', unit: '회 이하' },
    { label: '외식 줄이기', value: 'DINING', unit: '회 이하' },
    { label: '집밥 비율 높이기', value: 'PERCENT', unit: '% 이상' },
    { label: '직접 입력하기', value: 'CUSTOM', unit: '원 이하' }
  ];
  const getUnit = () => goalOptions.find(o => o.value === goalType)?.unit;

  const handleSubmit = async () => {
    if (!title || !description) {
      return Alert.alert('오류', '챌린지 제목과 설명을 입력해주세요.');
    }

    if (!goalValue) {
      return Alert.alert('오류', '목표 금액을 입력해주세요.');
    }

    try {
      // 목표 금액을 정수로 변환
      const targetAmount = parseInt(goalValue.replace(/,/g, ''), 10);
      
      // 카테고리별 적절한 목표 금액 필드 설정 - 백엔드 API 스펙에 맞게 camelCase 사용
      const challengePayload = {
        title,
        description,
        startDate,
        endDate,
        maxParticipants: parseInt(maxParticipants, 10),
        type: isPublic ? 'PUBLIC' : 'PRIVATE',
        category
      };
      
      // 카테고리별 특정 타겟 금액 필드 추가 (camelCase로 수정)
      // 백엔드 Enum 값 분석하여 올바른 값 사용
      switch(category) {
        case 'FOOD':
          challengePayload.foodTargetAmount = targetAmount;
          // Challenge.java의 FoodChallengeGoalType Enum 값 사용
          challengePayload.foodGoalType = 'TOTAL_FOOD_EXPENSE_LIMIT';
          break;
        case 'CAFE_SNACK':
          challengePayload.cafeSnackTargetAmount = targetAmount;
          // Challenge.java의 CafeSnackChallengeGoalType Enum 값 사용
          challengePayload.cafeSnackGoalType = 'TOTAL_CAFE_SNACK_EXPENSE_LIMIT';
          break;
        case 'SAVINGS':
          challengePayload.targetSavingsAmount = targetAmount;
          // Challenge.java의 SavingsGoalType Enum 값 사용
          challengePayload.savingsGoalType = 'TOTAL_AMOUNT_IN_PERIOD';
          break;
        default:
          // 다른 카테고리의 경우 일반적인 targetAmount 사용
          // 하지만 이런 경우에는 기본적으로 다른 카테고리 데이터로 하렬덨
          // 여기서는 실패를 방지하기 위해 기본적으로 FOOD 형태로 설정
          challengePayload.foodTargetAmount = targetAmount;
          challengePayload.foodGoalType = 'TOTAL_FOOD_EXPENSE_LIMIT';
          break;
      }
      
      console.log('챌린지 생성 요청 데이터:', JSON.stringify(challengePayload));
      
      const response = await createChallenge(challengePayload);
      
      console.log('챌린지 생성 성공 응답:', response);
      console.log('챌린지 생성 응답 구조 확인:', JSON.stringify(response, null, 2));
      console.log('챌린지 ID 필드 있는지 확인 - id:', response.id);
      console.log('챌린지 ID 필드 있는지 확인 - challengeId:', response.challengeId);
      
      if (response.inviteCode) {
        Alert.alert('성공', `비공개 챌린지가 생성되었습니다.\n초대 코드: ${response.inviteCode}`);
      } else {
        Alert.alert('성공', '챌린지가 생성되었습니다.');
      }
      
      // 챌린지 ID와 목표 금액 정보를 AsyncStorage에 저장
      if (response && response.challengeId) {
        // ID를 문자열로 변환하여 저장 (중요)
        const challengeId = String(response.challengeId);
        console.log('저장할 챌린지 ID:', challengeId, ', 타입:', typeof challengeId);
        const targetAmountData = {
          category,
          targetAmount
        };
        
        // 카테고리 특화된 필드 정보도 저장
        if (category === 'FOOD') {
          targetAmountData.foodTargetAmount = targetAmount;
          targetAmountData.foodGoalType = challengePayload.foodGoalType;
        } else if (category === 'CAFE_SNACK') {
          targetAmountData.cafeSnackTargetAmount = targetAmount;
          targetAmountData.cafeSnackGoalType = challengePayload.cafeSnackGoalType;
        } else if (category === 'SAVINGS') {
          targetAmountData.targetSavingsAmount = targetAmount;
          targetAmountData.savingsGoalType = challengePayload.savingsGoalType;
        }
        
        // AsyncStorage에 저장
        try {
          const existingData = await AsyncStorage.getItem('challengeTargetAmounts');
          const data = existingData ? JSON.parse(existingData) : {};
          
          data[challengeId] = targetAmountData;
          
          await AsyncStorage.setItem('challengeTargetAmounts', JSON.stringify(data));
          console.log(`챌린지 ${challengeId} 목표금액 저장 완료:`, targetAmountData);
          
          // 저장 후 전체 데이터 확인
          const verifyData = await AsyncStorage.getItem('challengeTargetAmounts');
          console.log('저장 후 AsyncStorage 전체 데이터:', verifyData);
          if (verifyData) {
            const parsedData = JSON.parse(verifyData);
            console.log('데이터 파싱 후:', parsedData);
            console.log('저장된 모든 챌린지 ID:', Object.keys(parsedData));
          }
        } catch (storageError) {
          console.error('목표 금액 저장 오류:', storageError);
        }
      }
      
      // API 응답에서 챌린지 ID를 가져옵니다
      const newChallengeId = response.challengeId; // challengeId로 변경

      if (!newChallengeId) {
        Alert.alert('오류', '챌린지 생성 후 ID를 받지 못했습니다. 홈 화면으로 이동합니다.');
        navigation.replace('ChallengeHomeScreen');
        return;
      }

      if (response.inviteCode) {
        Alert.alert('성공', `비공개 챌린지가 생성되었습니다.\n초대 코드: ${response.inviteCode}\n챌린지 상세 화면으로 이동합니다.`);
      } else {
        Alert.alert('성공', '챌린지가 생성되었습니다. 챌린지 상세 화면으로 이동합니다.');
      }
      
      // ChallengeDetailScreen으로 이동하면서 challengeId를 파라미터로 전달
      navigation.replace('ChallengeDetailScreen', { challengeId: newChallengeId });
    } catch (error) {
      console.error('챌린지 생성 실패:', error.response?.data || error);
      Alert.alert('실패', '챌린지 생성에 실패했습니다. 입력 정보를 확인해주세요.');
    }
  };

  return (
    <SafeAreaView className="flex-1 bg-[#F0F0F0]">
      <Header />
      <ScrollView contentContainerStyle={{ paddingBottom: 40 }} className="px-6 pt-6">
        <Text className="text-3xl font-extrabold mb-8">챌린지를 생성 중이에요</Text>

        {/* 카테고리 */}
        <View className="flex-row items-center mb-6">
          <Text className="w-1/3 text-base font-bold">카테고리</Text>
          <TouchableOpacity
            className="bg-white h-12 rounded-full flex-1 flex-row items-center justify-between px-6 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
            onPress={() => setShowCatModal(true)}
          >
            <Text className="text-lg text-gray-400">{categoryLabel}</Text>
            <Icon name="chevron-down-outline" size={20} color="#6D6D6D" />
          </TouchableOpacity>
        </View>

        {/* 챌린지 이름 */}
        <View className="flex-row items-center mb-6">
          <Text className="w-1/3 text-base font-bold">챌린지 이름</Text>
          <TextInput
            className="bg-white h-12 rounded-full flex-1 px-6 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
            placeholder="무슨 챌린지인가요?"
            placeholderTextColor="#C4C4C4"
            value={title}
            onChangeText={setTitle}
          />
        </View>

        {/* 챌린지 설명 */}
        <Text className="text-base font-bold mb-2">챌린지 설명</Text>
        <TextInput
          className="bg-white rounded-2xl px-6 py-4 mb-6 h-24 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
          placeholder="어떤 챌린지인지 설명해주세요"
          placeholderTextColor="#C4C4C4"
          multiline
          value={description}
          onChangeText={setDescription}
        />

        {/* 목표 */}
        <Text className="text-base font-bold mb-2">목표</Text>
        <TouchableOpacity
          className="bg-white h-12 rounded-full flex-row items-center justify-between px-6 mb-4 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
          onPress={() => setShowGoalModal(true)}
        >
          <Text className="text-lg text-gray-700">{selectedGoalLabel}</Text>
          <Icon name="chevron-down-outline" size={20} color="#6D6D6D" />
        </TouchableOpacity>
        <View className="flex-row items-center mb-6">
          <TextInput
            className="flex-1 bg-white h-12 rounded-full px-6 shadow-[0px_4px_10px_rgba(0,0,0,0.05)] text-right"
            placeholder="100000"
            placeholderTextColor="#C4C4C4"
            keyboardType="numeric"
            value={goalValue}
            onChangeText={setGoalValue}
          />
          <View className="bg-white h-12 rounded-full px-6 ml-2 flex-row items-center justify-center shadow-[0px_4px_10px_rgba(0,0,0,0.05)]">
            <Text className="text-gray-700">{getUnit()}</Text>
          </View>
        </View>

        {/* 날짜 */}
        <Text className="text-base font-bold mb-2">시작일</Text>
        <TextInput
          className="bg-white h-12 rounded-full px-6 mb-6 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
          value={startDate}
          onChangeText={setStartDate}
        />

        <Text className="text-base font-bold mb-2">종료일</Text>
        <TextInput
          className="bg-white h-12 rounded-full px-6 mb-6 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
          value={endDate}
          onChangeText={setEndDate}
        />
        
        {/* 최대 참가자 수 */}
        <Text className="text-base font-bold mb-2">최대 참가자 수</Text>
        <View className="flex-row items-center mb-8">
          <TextInput
            className="bg-white h-12 rounded-full px-6 flex-1 shadow-[0px_4px_10px_rgba(0,0,0,0.05)]"
            placeholder="50"
            placeholderTextColor="#C4C4C4"
            keyboardType="numeric"
            value={maxParticipants}
            onChangeText={setMaxParticipants}
          />
          <View className="bg-white h-12 rounded-full px-6 ml-2 flex-row items-center justify-center shadow-[0px_4px_10px_rgba(0,0,0,0.05)]">
            <Text className="text-gray-700">명</Text>
          </View>
        </View>

        {/* 공개 여부 */}
        <View className="flex-row items-center mb-8">
          <Text className="w-1/3 text-base font-bold">챌린지 공개 여부</Text>
          <Switch
            value={isPublic}
            onValueChange={setIsPublic}
            trackColor={{ true: '#014029', false: '#E5E5E5' }}
            thumbColor="#FFFFFF"
          />
          <Text className="ml-3 text-lg text-gray-700">{isPublic ? '공개' : '비공개'}</Text>
        </View>

        {/* 버튼 */}
        <TouchableOpacity
          className="bg-[#014029] h-12 rounded-full items-center justify-center shadow-md"
          onPress={handleSubmit}
        >
          <Text className="text-white text-lg font-bold">시작하기</Text>
        </TouchableOpacity>

        {/* 여유 공간 */}
        <View className="h-6" />
      </ScrollView>

      {/* 목표 모달 */}
      <Modal transparent visible={showGoalModal} animationType="fade">
        <Pressable className="absolute inset-0 bg-black/40 justify-center items-center" onPress={() => setShowGoalModal(false)}>
          <View className="bg-white w-4/5 rounded-2xl overflow-hidden">
            {goalOptions.map((opt, i) => (
              <TouchableOpacity
                key={i}
                className="py-4 px-6 border-b border-gray-200"
                onPress={() => {
                  setGoalType(opt.value);
                  setSelectedGoalLabel(opt.label);
                  setShowGoalModal(false);
                }}
              >
                <Text className="text-base text-gray-700">{opt.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </Pressable>
      </Modal>

      {/* 카테고리 모달 */}
      <Modal transparent visible={showCatModal} animationType="fade">
        <Pressable className="absolute inset-0 bg-black/40 justify-center items-center" onPress={() => setShowCatModal(false)}>
          <View className="bg-white w-4/5 rounded-2xl overflow-hidden">
            {[
              { label: '식비', value: 'FOOD' },
              { label: '카페/간식', value: 'CAFE_SNACK' },
              { label: '저축', value: 'SAVINGS' },
              { label: '술/유흥', value: 'ALCOHOL_ENTERTAINMENT' },
              { label: '쇼핑', value: 'SHOPPING' },
              { label: '미용', value: 'BEAUTY' },
              { label: '여행', value: 'TRAVEL' },
              { label: '반려동물', value: 'PET' }
            ].map((c, i) => (
              <TouchableOpacity
                key={i}
                className="py-4 px-6 border-b border-gray-200"
                onPress={() => {
                  setCategory(c.value);
                  setCategoryLabel(c.label);
                  setShowCatModal(false);
                }}
              >
                <Text className="text-base text-gray-700">{c.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </Pressable>
      </Modal>
    </SafeAreaView>
  );
}
