import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity, Modal } from 'react-native';
import Header from '../../components/layout/Header';
import ChallengeSection from '../../components/common/ChallengeSection';
import RatingSection from '../../components/common/RatingSection';
import QuizAndNews from '../../components/common/QuizAndNews';
import CommunitySection from '../../components/common/CommunitySection';
import { useNavigation } from '@react-navigation/native';
import { useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';
import Icon from 'react-native-vector-icons/Ionicons';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function HomeScreen() {
  const { width } = useWindowDimensions();
  const horizontalPadding = width > 380 ? 16 : 12;
  const navigation = useNavigation();
  const [showMydataModal, setShowMydataModal] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(false);
  const [agreements, setAgreements] = useState({
    all: false,
    personalInfo: false,
    termsOfUse: false,
    dataCollection: false,
    sensitiveInfo: false
  });

  useEffect(() => {
    checkMydataConsent();
  }, []);

  const checkMydataConsent = async () => {
    try {
      const token = await AsyncStorage.getItem('userToken');
      console.log('유저 토큰:', token ? '토큰 있음' : '토큰 없음');

      if (!token) return;

      try {
        const response = await axios.get(`${API_BASE_URL}/users/mydata-consent`, {
          headers: { Authorization: `Bearer ${token}` }
        });

        console.log('마이데이터 API 응답:', response.data);

        if (response.data && response.data.hasConsent === false) {
          setShowMydataModal(true);
          console.log('마이데이터 동의 안됨: 팝업 표시');
        } else {
          console.log('마이데이터 동의됨 또는 응답 확인 불가');
        }
      } catch (apiError) {
        console.error('마이데이터 API 호출 오류:', apiError);

        // 개발 중에는 API 오류 시 항상 팝업 표시 (테스트용)
        setShowMydataModal(true);
        console.log('API 오류로 테스트용 팝업 표시');
      }
    } catch (error) {
      console.error('마이데이터 동의 확인 중 오류:', error);
    }
  };

  const handleMydataConsent = async () => {
    setShowMydataModal(false);
    setShowTermsModal(true);
  };

  // 전체 동의 처리
  const handleAllAgreements = (value) => {
    setAgreements({
      all: value,
      personalInfo: value,
      termsOfUse: value,
      dataCollection: value,
      sensitiveInfo: value
    });
  };

  // 개별 동의 처리
  const handleSingleAgreement = (key, value) => {
    const newAgreements = {
      ...agreements,
      [key]: value
    };

    // 모든 개별 약관이 동의되었는지 확인
    const allChecked =
      newAgreements.personalInfo &&
      newAgreements.termsOfUse &&
      newAgreements.dataCollection &&
      newAgreements.sensitiveInfo;

    setAgreements({
      ...newAgreements,
      all: allChecked
    });
  };

  // 약관 동의 제출
  const submitAgreements = async () => {
    // 모든 필수 약관에 동의했는지 확인
    if (!agreements.personalInfo || !agreements.termsOfUse ||
      !agreements.dataCollection || !agreements.sensitiveInfo) {
      return; // 모든 약관에 동의하지 않으면 비활성화
    }

    try {
      const token = await AsyncStorage.getItem('userToken');

      // 약관 동의 정보를 서버에 전송
      await axios.put(`${API_BASE_URL}/users/mydata-consent`, {
        myDataConsent: true
      }, {
        headers: { Authorization: `Bearer ${token}` }
      });

      // 약관 동의 모달 닫기
      setShowTermsModal(false);

      // 마이데이터 로딩 화면으로 이동
      navigation.navigate('MyDataConsent');
    } catch (error) {
      console.error('약관 동의 제출 중 오류:', error);
    }
  };
  return (
    <View className="flex-1 bg-[#EFEFEF]">
      <Header />
      <ScrollView
        className="flex-1"
        contentContainerStyle={{
          paddingHorizontal: horizontalPadding,
          paddingTop: 16,
          paddingBottom: 24
        }}
      >
        {/* 지금 뜨고 있는 챌린지 */}
        <View className="mb-8">
          <Text className="text-[20px] font-bold text-black mb-2">지금 뜨고 있는 챌린지</Text>
          <ChallengeSection />
        </View>

        {/* 다른 사람들의 평점 */}
        <View className="mb-8">
          <Text className="text-[20px] font-bold text-black mb-2">다른 사람들의 평점은 어떨까?</Text>
          <RatingSection />
        </View>

        {/* 맞춤 퀴즈 & 추천 기사 */}
        <View className="mb-8">
          <QuizAndNews />
        </View>

        {/* 커뮤니티 */}
        <View className="mb-4">
          <TouchableOpacity onPress={() => navigation.navigate('Community')}>
            <Text className="text-[20px] font-bold text-Fineed-green mb-2">커뮤니티</Text>
          </TouchableOpacity>
          <CommunitySection />
        </View>
      </ScrollView>

      <Modal
        visible={showMydataModal}
        transparent={true}
        animationType="fade"
      >
        <View className="flex-1 justify-center items-center bg-black/50">
          <View className="bg-white p-5 rounded-2xl w-4/5 max-w-md items-center shadow-lg">
            <Text className="text-xl font-bold text-center mb-1">
              아직 마이데이터 연동을
            </Text>
            <Text className="text-xl font-bold text-center mb-5">
              하지 않으셨네요!
            </Text>

            <Text className="text-center text-gray-600 mb-2">
              원활한 FINEED 이용을 위해
            </Text>
            <Text className="text-center text-gray-600 mb-4">
              마이데이터가 필요합니다 😊
            </Text>

            <TouchableOpacity
              onPress={handleMydataConsent}
              className="bg-Fineed-green py-3 px-5 rounded-lg w-full items-center mt-2"
            >
              <Text className="text-white font-medium">마이데이터 가져오기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* 마이데이터 약관 동의 모달 */}
      <Modal
        visible={showTermsModal}
        transparent={true}
        animationType="fade"
      >
        <View className="flex-1 justify-center items-center bg-black/50">
          <View className="bg-white p-5 rounded-2xl w-4/5 max-w-md">
            <View className="pb-4">
              <Text className="text-xl font-bold text-center mb-3">
                FINEED와 연결하기 위해{"\n"}약관 동의가 필요해요
              </Text>

              {/* 전체 동의 */}
              <TouchableOpacity
                className="flex-row items-center py-3 border-b border-gray-200"
                onPress={() => handleAllAgreements(!agreements.all)}
              >
                <Icon
                  name={agreements.all ? "checkmark-circle" : "ellipse-outline"}
                  size={24}
                  color={agreements.all ? "#014029" : "#D9D9D9"}
                />
                <Text className="ml-2 font-bold">전체 동의하기</Text>
              </TouchableOpacity>

              {/* 개인정보 제3자 정보 제공 동의 */}
              <TouchableOpacity
                className="flex-row items-center py-3"
                onPress={() => handleSingleAgreement('personalInfo', !agreements.personalInfo)}
              >
                <Icon
                  name={agreements.personalInfo ? "checkmark-circle" : "ellipse-outline"}
                  size={20}
                  color={agreements.personalInfo ? "#014029" : "#D9D9D9"}
                />
                <Text className="ml-2 text-sm">[필수] 개인정보 제3자 정보 제공 동의</Text>
              </TouchableOpacity>

              {/* FINEED 이용약관 동의 */}
              <TouchableOpacity
                className="flex-row items-center py-3"
                onPress={() => handleSingleAgreement('termsOfUse', !agreements.termsOfUse)}
              >
                <Icon
                  name={agreements.termsOfUse ? "checkmark-circle" : "ellipse-outline"}
                  size={20}
                  color={agreements.termsOfUse ? "#014029" : "#D9D9D9"}
                />
                <Text className="ml-2 text-sm">[필수] FINEED 이용약관 동의</Text>
              </TouchableOpacity>

              {/* 개인정보 수집·이용 동의 */}
              <TouchableOpacity
                className="flex-row items-center py-3"
                onPress={() => handleSingleAgreement('dataCollection', !agreements.dataCollection)}
              >
                <Icon
                  name={agreements.dataCollection ? "checkmark-circle" : "ellipse-outline"}
                  size={20}
                  color={agreements.dataCollection ? "#014029" : "#D9D9D9"}
                />
                <Text className="ml-2 text-sm">[필수] 개인정보 수집·이용 동의</Text>
              </TouchableOpacity>

              {/* 민감정보 처리에 관한 동의 */}
              <TouchableOpacity
                className="flex-row items-center py-3"
                onPress={() => handleSingleAgreement('sensitiveInfo', !agreements.sensitiveInfo)}
              >
                <Icon
                  name={agreements.sensitiveInfo ? "checkmark-circle" : "ellipse-outline"}
                  size={20}
                  color={agreements.sensitiveInfo ? "#014029" : "#D9D9D9"}
                />
                <Text className="ml-2 text-sm">[필수] 민감정보 처리에 관한 동의</Text>
              </TouchableOpacity>
            </View>

            {/* 동의하기 버튼 */}
            <TouchableOpacity
              onPress={submitAgreements}
              className={`py-3 px-5 rounded-lg w-full items-center mt-4 ${agreements.all ? 'bg-Fineed-green' : 'bg-gray-300'
                }`}
              disabled={!agreements.all}
            >
              <Text className="text-white font-medium">동의하기</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
  );
}
