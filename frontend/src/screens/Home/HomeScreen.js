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
  const [userData, setUserData] = useState(null);
  const [challenges, setChallenges] = useState([]);
  const [categories, setCategories] = useState([]);
  const [challengeLoading, setChallengeLoading] = useState(true);

  useEffect(() => {
    checkMydataConsent();
  }, []);

  const checkMydataConsent = async () => {
    try {
      const token = await AsyncStorage.getItem('userToken');
      console.log('유저 토큰:', token ? '토큰 있음' : '토큰 없음');

      if (!token) return;
      
      // 로컬에 저장된 userData에서 마이데이터 동의 상태 확인
      const userDataStr = await AsyncStorage.getItem('userData');
      if (userDataStr) {
        const userData = JSON.parse(userDataStr);
        console.log('로컬 userData:', userData);
        
        // userData에서 마이데이터 동의 상태 확인
        console.log('마이데이터 동의 값:', userData.myDataConsent, typeof userData.myDataConsent);
        console.log('유저 타입:', userData.userType);
        
        // userType이 있으면 이미 마이데이터 동의한 것으로 간주
        if (userData.userType) {
          console.log('유저 타입이 있으므로 마이데이터 동의된 것으로 간주');
          
          // 로컬 userData를 업데이트하여 myDataConsent를 true로 설정
          userData.myDataConsent = true;
          await AsyncStorage.setItem('userData', JSON.stringify(userData));
          console.log('로컬 userData 업데이트 완료');
          return;
        }
        
        // myDataConsent가 true 또는 1이 아닌 경우에만 동의 창 표시
        if (userData.myDataConsent !== true && 
            userData.myDataConsent !== 1 && 
            userData.myDataConsent !== '1') {
          console.log('로컬 userData에서 마이데이터 동의 안됨: 팝업 표시');
          setShowMydataModal(true);
          return;
        }
        
        console.log('로컬 userData에서 마이데이터 동의 확인됨');
      }

      // 백엔드 API로 마이데이터 동의 상태 확인 (추가 검증)
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
        // API 오류 시에도 로컬 userData 기반으로 동의 상태 확인
        const userDataStr = await AsyncStorage.getItem('userData');
        if (userDataStr) {
          const userData = JSON.parse(userDataStr);
          if (!userData.myDataConsent) {
            console.log('API 오류, 로컬 userData 확인 결과 동의 안됨: 팝업 표시');
            setShowMydataModal(true);
          }
        }
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
        consent: true
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

  useEffect(() => {
    fetchUserData();
    fetchChallenges();
  }, [navigation]);

  const fetchUserData = async () => {
    try {
      const storedUserData = await AsyncStorage.getItem('userData');
      if (storedUserData) {
        setUserData(JSON.parse(storedUserData));
        console.log('HomeScreen userData:', JSON.parse(storedUserData));
      }
    } catch (error) {
      console.error('사용자 데이터 불러오기 실패:', error);
    }
  };

  const fetchChallenges = async () => {
    try {
      const token = await AsyncStorage.getItem('userToken');
      if (!token) {
        console.log('토큰이 없어 챌린지를 불러올 수 없습니다.');
        setChallengeLoading(false);
        return;
      }

      // 1. 카테고리 정보 가져오기
      const catRes = await axios.get(`${API_BASE_URL}/api/challenges/categories`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setCategories(catRes.data);

      // 2. 참여 중인 챌린지 목록 가져오기
      const chalRes = await axios.get(`${API_BASE_URL}/api/challenges/participating`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      console.log('참여 중인 챌린지 전체 응답:', chalRes.data);

      let challengeList = [];
      if (chalRes.data) {
        if (Array.isArray(chalRes.data)) {
          challengeList = chalRes.data;
        } else if (chalRes.data.content && Array.isArray(chalRes.data.content)) {
          challengeList = chalRes.data.content;
        } else if (chalRes.data.challenges && Array.isArray(chalRes.data.challenges)) {
          challengeList = chalRes.data.challenges;
        }
      }
      
      console.log('처리된 챌린지 목록:', JSON.stringify(challengeList, null, 2));
      
      if (challengeList.length === 0) {
        console.log('참여 중인 챌린지가 없습니다.');
        setChallenges([]);
        setChallengeLoading(false);
        return;
      }

      // 3. 각 챌린지의 상세 정보 가져오기
      const challengeDetails = await Promise.all(
        challengeList.map(async (challenge) => {
          try {
            // challenge 객체 구조 확인
            console.log('처리할 챌린지 데이터:', JSON.stringify(challenge, null, 2));
            
            let challengeId;
            if (typeof challenge === 'object' && challenge !== null) {
              challengeId = challenge.id || challenge.challengeId;
            } else {
              challengeId = challenge;
            }

            if (!challengeId) {
              console.error('유효하지 않은 챌린지 데이터:', JSON.stringify(challenge, null, 2));
              return null;
            }

            console.log(`챌린지 ID ${challengeId}로 상세 정보 요청`);
            const detailResponse = await axios.get(`${API_BASE_URL}/api/challenges/${challengeId}`, {
              headers: { Authorization: `Bearer ${token}` }
            });
            console.log(`챌린지 ${challengeId} 상세 정보:`, detailResponse.data);
            return detailResponse.data;
          } catch (error) {
            console.error(`챌린지 ${JSON.stringify(challenge)} 상세 정보 로드 실패:`, error);
            return null;
          }
        })
      );

      // null이 아닌 챌린지만 필터링
      const validChallenges = challengeDetails.filter(challenge => challenge !== null);
      
      setChallenges(validChallenges);
      
    } catch (error) {
      console.error('챌린지 로드 중 오류:', error);
      if (error.response) {
        console.error('에러 상태:', error.response.status);
        console.error('에러 데이터:', error.response.data);
      }
      setChallenges([]);
      setCategories([]);
    } finally {
      setChallengeLoading(false);
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
          <TouchableOpacity onPress={() => navigation.navigate('ChallengeHomeScreen')}>
            <Text className="text-[20px] font-bold text-black mb-2">지금 뜨고 있는 챌린지</Text>
          </TouchableOpacity>
          <ChallengeSection
            userData={userData}
            challenges={challenges}
            categories={categories}
            challengeLoading={challengeLoading}
            navigation={navigation}
          />
        </View>

        {/* 금융 상품 보러가기 */}
        <View className="mb-8">
          <TouchableOpacity onPress={() => navigation.navigate('ProductsHome')}>
            <Text className="text-[20px] font-bold text-black mb-2">신청 가능한 청년 우대 상품</Text>
          </TouchableOpacity>
          <RatingSection userData={userData} />
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
