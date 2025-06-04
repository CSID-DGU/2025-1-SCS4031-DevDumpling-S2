import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  Image,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Alert,
  SafeAreaView
} from 'react-native';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import {
  fetchChallengeDetail,
  joinChallenge,
  getCategoryName,
  fetchChallengeCategories
} from './ChallengeApi';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default function ChallengeDetailScreen({ route }) {
  const { challengeId } = route.params;
  const navigation = useNavigation();

  // 상태 관리
  const [challenge, setChallenge] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categoryImage, setCategoryImage] = useState(null);
  const [isParticipating, setIsParticipating] = useState(false);
  const [isCreator, setIsCreator] = useState(false);
  const [participationChecked, setParticipationChecked] = useState(false); // 참여 상태 확인 여부
  const [userData, setUserData] = useState(null);

  // 로그인 상태 확인 및 userData 세팅
  useEffect(() => {
    const checkLoginStatus = async () => {
      try {
        const token = await AsyncStorage.getItem('userToken');
        const userDataString = await AsyncStorage.getItem('userData');
        if (token && userDataString) {
          const parsedUserData = JSON.parse(userDataString);
          console.log('로그인된 사용자 정보:', parsedUserData);
          setUserData(parsedUserData);
        } else {
          console.log('로그인된 사용자 정보 없음');
          setUserData(null);
        }
      } catch (err) {
        console.error('로그인 상태 확인 오류:', err);
        setUserData(null);
      }
    };
    checkLoginStatus();
  }, []);

  // 화면이 포커스될 때마다 데이터 다시 로드
  useFocusEffect(
    React.useCallback(() => {
      if (challengeId) {
        setLoading(true);
        // 초기 상태 리셋
        setIsParticipating(false);
        setIsCreator(false);
        setParticipationChecked(false);
        loadChallengeDetail();
      }
      return () => { };
    }, [challengeId, userData])
  );

  // 챌린지 상세 정보 불러오기 함수
  const loadChallengeDetail = async () => {
    try {
      console.log(`챌린지 상세 정보 로드 시작 (ID: ${challengeId})`);

      // 1. AsyncStorage에서 참여 상태 확인
      if (userData) {
        try {
          const userId = userData.id;
          const participationKey = `${challengeId}_${userId}`;
          const stored = await AsyncStorage.getItem('participatingChallenges');
          const participatingChallenges = stored ? JSON.parse(stored) : [];
          const isParticipatingFromStorage = participatingChallenges.includes(participationKey);
          console.log(`AsyncStorage 참여 상태 (${participationKey}): ${isParticipatingFromStorage}`);
          if (isParticipatingFromStorage) {
            setIsParticipating(true);
          }
        } catch (storageErr) {
          console.error('AsyncStorage 읽기 오류:', storageErr);
        }
      }

      // 2. 백엔드 API에서 챌린지 상세 정보 가져오기
      const data = await fetchChallengeDetail(challengeId);
      console.log('챌린지 상세 정보 로드됨:', data);
      setChallenge(data);

      // 3. 서버 응답으로 참여 상태 및 생성자 여부 판단
      if (userData) {
        const currentUserId = String(userData.id);

        // 3-1. 생성자 여부 확인
        if (data.creatorNickname && data.creatorNickname === userData.nickname) {
          console.log('내가 만든 챌린지 확인됨');
          setIsCreator(true);
        }

        // 3-2. participants 배열에서 참여 여부 확인
        if (Array.isArray(data.participants)) {
          const found = data.participants.some((p) => String(p.userId) === currentUserId);
          console.log('서버 데이터 기반 참여 상태:', found);
          if (found) {
            setIsParticipating(true);
            // AsyncStorage에도 저장
            try {
              const participationKey = `${challengeId}_${currentUserId}`;
              const stored = await AsyncStorage.getItem('participatingChallenges');
              const participatingChallenges = stored ? JSON.parse(stored) : [];
              if (!participatingChallenges.includes(participationKey)) {
                participatingChallenges.push(participationKey);
                await AsyncStorage.setItem(
                  'participatingChallenges',
                  JSON.stringify(participatingChallenges)
                );
                console.log(`참여 챌린지 저장됨 (${participationKey}):`, participatingChallenges);
              }
            } catch (storageErr) {
              console.error('AsyncStorage 저장 오류:', storageErr);
            }
          }
        }
      }

      setParticipationChecked(true);
      setLoading(false);
    } catch (err) {
      console.error('챌린지 상세 정보 불러오기 오류:', err);
      setError('챌린지 정보를 불러오는 중 오류가 발생했습니다.');
      setLoading(false);
    }
  };

  // URL을 안전하게 처리
  const safeUri = (uri) => {
    if (!uri) return '';
    return uri.startsWith('http') ? uri : `https://${uri}`;
  };

  // 카테고리 이미지 불러오기
  useEffect(() => {
    const loadCategoryData = async () => {
      try {
        const categoriesData = await fetchChallengeCategories();
        console.log('백엔드 카테고리 데이터:', categoriesData);
        if (challenge && challenge.category && Array.isArray(categoriesData)) {
          const matched = categoriesData.find(
            (cat) => cat.category === challenge.category
          );
          if (matched?.imageUrl) {
            setCategoryImage(matched.imageUrl);
          } else {
            for (const cat of categoriesData) {
              if (
                cat.name === challenge.category ||
                cat.name === getCategoryName(challenge.category)
              ) {
                if (cat.imageUrl) {
                  setCategoryImage(cat.imageUrl);
                  break;
                }
              }
            }
          }
        }
      } catch (err) {
        console.error('카테고리 정보 로딩 오류:', err);
      }
    };
    if (challenge) {
      loadCategoryData();
    }
  }, [challenge]);

  // 챌린지 참여 함수
  const handleJoinChallenge = async () => {
    if (!userData) {
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

    if (isCreator) {
      Alert.alert('안내', '내가 만든 챌린지에는 참여할 수 없습니다.');
      return;
    }

    if (isParticipating) {
      Alert.alert('안내', '이미 참여 중인 챌린지입니다.');
      return;
    }

    if (challenge.currentParticipants >= challenge.maxParticipants) {
      Alert.alert('안내', '이미 마감된 챌린지입니다.');
      return;
    }

    if (challenge.status === 'COMPLETED') {
      Alert.alert('안내', '종료된 챌린지입니다.');
      return;
    }

    try {
      setLoading(true);
      console.log(`챌린지 참여 요청 시작 (ID: ${challengeId})`);

      const result = await joinChallenge(challengeId);
      console.log('챌린지 참여 API 결과:', result);

      if (result.success || result.alreadyJoined) {
        setIsParticipating(true);

        // 로컬 상태 업데이트
        setChallenge((prev) => {
          if (!prev) return null;
          const updatedParticipants = [...(prev.participants || [])];
          const currentUserId = String(userData.id);

          const alreadyExists = updatedParticipants.some(
            (p) => String(p.userId) === currentUserId
          );
          if (!alreadyExists) {
            updatedParticipants.push({
              userId: userData.id,
              nickname: userData.nickname
            });
          }

          return {
            ...prev,
            currentParticipants: prev.currentParticipants + (alreadyExists ? 0 : 1),
            participants: updatedParticipants
          };
        });

        const message = result.alreadyJoined
          ? '이미 참여 중인 챌린지입니다.'
          : '챌린지 참여가 완료되었습니다!';
        Alert.alert('성공', message);

        // 최신 상태 다시 로드
        await loadChallengeDetail();

        // AsyncStorage에도 저장
        try {
          const currentUserId = String(userData.id);
          const participationKey = `${challengeId}_${currentUserId}`;
          const stored = await AsyncStorage.getItem('participatingChallenges');
          const participatingChallenges = stored ? JSON.parse(stored) : [];
          if (!participatingChallenges.includes(participationKey)) {
            participatingChallenges.push(participationKey);
            await AsyncStorage.setItem(
              'participatingChallenges',
              JSON.stringify(participatingChallenges)
            );
            console.log(`참여 챌린지 저장됨 (${participationKey}):`, participatingChallenges);
          }
        } catch (storageErr) {
          console.error('AsyncStorage 저장 오류:', storageErr);
        }
      } else {
        Alert.alert('오류', '챌린지 참여에 실패했습니다.');
      }
    } catch (err) {
      console.error('챌린지 참여 실패:', err);
      if (err.response) {
        console.error('에러 응답:', err.response.data);
      }
      Alert.alert('오류', err.message || '챌린지 참여 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 로딩 화면
  if (loading) {
    return (
      <View className="flex-1 justify-center items-center bg-Fineed-background">
        <ActivityIndicator size="large" color="#014029" />
      </View>
    );
  }

  // 오류 화면
  if (error || !challenge) {
    return (
      <View className="flex-1 justify-center items-center p-4 bg-Fineed-background">
        <Text className="text-red-500 text-lg mb-4">
          {error || '챌린지 정보를 불러올 수 없습니다.'}
        </Text>
        <TouchableOpacity
          className="bg-Fineed-green py-2 px-4 rounded-lg"
          onPress={() => navigation.goBack()}
        >
          <Text className="text-white">돌아가기</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // 카테고리 한글명
  const categoryName = getCategoryName(challenge.category) || '카테고리';

  // 버튼 상태 및 텍스트 설정
  const getButtonStatus = () => {
    if (!participationChecked) {
      return { text: '로딩 중...', disabled: true };
    }
    if (isCreator) {
      return { text: '내가 만든 챌린지', disabled: true };
    }
    if (isParticipating) {
      return { text: '참여중', disabled: true };
    }
    if (challenge.currentParticipants >= challenge.maxParticipants) {
      return { text: '마감되었습니다', disabled: true };
    }
    if (challenge.status === 'COMPLETED') {
      return { text: '종료된 챌린지', disabled: true };
    }
    return { text: '참여하기', disabled: false };
  };

  const { text: buttonText, disabled: buttonDisabled } = getButtonStatus();

  return (
    <>
      <Header />
      <SafeAreaView className="flex-1 bg-Fineed-background">
        <ScrollView className="flex-1 px-4">
          {/* 카테고리 */}
          <View className="mt-2 items-center">
            <Text className="text-gray-600 font-medium">{categoryName}</Text>
          </View>

          {/* 제목과 잠금 아이콘 */}
          <View className="flex-row items-center justify-center mt-2 mb-4">
            {challenge.isPrivate && (
              <Icon name="lock-closed" size={20} color="#000" style={{ marginRight: 8 }} />
            )}
            <Text className="text-[24px] font-bold flex-1 text-center">{challenge.title}</Text>
          </View>

          {/* 카테고리 이미지 (중앙 배치) */}
          <View className="items-center justify-center my-6">
            {categoryImage && (
              <Image
                source={{ uri: safeUri(categoryImage) }}
                style={{ width: 120, height: 120 }}
                resizeMode="contain"
              />
            )}
          </View>

          {/* 설명 텍스트 */}
          <View className="items-center mb-4">
            <Text className="text-gray-700 text-lg">
              {challenge.description}
            </Text>
          </View>

          {/* 참여자 수 */}
          <View className="items-center mb-4">
            <Text className="text-[18px] font-bold">
              현재 {challenge.currentParticipants}명 참여 중
            </Text>
          </View>

          {/* 순위 섹션 */}
          <View className="bg-white rounded-[20px] mb-4 p-5 shadow-sm">
            {/* 1등 */}
            <View className="flex-row items-center mb-3">
              <View className="bg-yellow-400 w-8 h-8 rounded-full items-center justify-center mr-3">
                <Text className="text-white font-bold">1</Text>
              </View>
              <View className="flex-1">
                <Text className="font-medium text-[16px]">
                  {challenge.participants && challenge.participants[0]
                    ? challenge.participants[0].nickname
                    : ''}
                </Text>
              </View>
            </View>

            {/* 2등 */}
            <View className="flex-row items-center mb-3">
              <View className="bg-gray-400 w-8 h-8 rounded-full items-center justify-center mr-3">
                <Text className="text-white font-bold">2</Text>
              </View>
              <View className="flex-1">
                <Text className="font-medium text-[16px]">
                  {challenge.participants && challenge.participants[1]
                    ? challenge.participants[1].nickname
                    : ''}
                </Text>
              </View>
            </View>

            {/* 3등 */}
            <View className="flex-row items-center">
              <View className="bg-orange-400 w-8 h-8 rounded-full items-center justify-center mr-3">
                <Text className="text-white font-bold">3</Text>
              </View>
              <View className="flex-1">
                <Text className="font-medium text-[16px]">
                  {challenge.participants && challenge.participants[2]
                    ? challenge.participants[2].nickname
                    : ''}
                </Text>
              </View>
            </View>
          </View>

          {/* 챌린지 정보 테이블 */}
          <View className="bg-white rounded-[20px] p-5 mb-6 shadow-sm">
            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">카테고리</Text>
              </View>
              <View className="w-2/3">
                <Text>{categoryName}</Text>
              </View>
            </View>

            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">챌린지 이름</Text>
              </View>
              <View className="w-2/3">
                <Text>{challenge.title}</Text>
              </View>
            </View>

            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">목표 금액</Text>
              </View>
              <View className="w-2/3">
                <Text>{challenge.targetAmount}</Text>
              </View>
            </View>

            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">시작일</Text>
              </View>
              <View className="w-2/3">
                <Text>{challenge.startDate}</Text>
              </View>
            </View>

            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">종료일</Text>
              </View>
              <View className="w-2/3">
                <Text>{challenge.endDate}</Text>
              </View>
            </View>

            <View className="flex-row py-2 border-b border-gray-100">
              <View className="w-1/3">
                <Text className="text-gray-700">챌린지 공개 여부</Text>
              </View>
              <View className="w-2/3">
                <Text>{challenge.isPrivate ? '비공개' : '공개'}</Text>
              </View>
            </View>
          </View>

          {/* 참여 버튼 */}
          <TouchableOpacity
            className={`py-4 rounded-[10px] items-center justify-center mb-6 ${buttonDisabled ? 'bg-gray-400' : 'bg-Fineed-green'
              }`}
            onPress={handleJoinChallenge}
            disabled={buttonDisabled}
          >
            <Text className="text-white font-bold text-[18px]">{buttonText}</Text>
          </TouchableOpacity>
        </ScrollView>
      </SafeAreaView>
    </>
  );
}
