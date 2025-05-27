import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView, TouchableOpacity, ActivityIndicator, Alert, SafeAreaView } from 'react-native';
import { useNavigation, useFocusEffect } from '@react-navigation/native';
import { fetchChallengeDetail, joinChallenge, getCategoryName, fetchChallengeCategories } from './ChallengeApi';
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
  const [participationChecked, setParticipationChecked] = useState(false); // 참여 상태 확인 완료 여부
  const [userData, setUserData] = useState(null);

  // 로그인 상태 확인
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
      } catch (error) {
        console.error('로그인 상태 확인 오류:', error);
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
        loadChallengeDetail();
      }
      return () => {
        // 화면에서 벗어날 때 정리
      };
    }, [challengeId, userData])
  );

  // 챌린지 상세 정보 불러오기 함수
  const loadChallengeDetail = async () => {
    try {
      console.log(`챌린지 상세 정보 로드 시작 (ID: ${challengeId})`);
      
      // 1. AsyncStorage에서 참여 상태 확인 (참여 데이터 관리)
      try {
        if (userData) {
          const userId = userData.id;
          // 참여 데이터를 더 정확하게 관리하기 위해 {challengeId_userId} 형태로 저장
          const participatingChallenges = JSON.parse(await AsyncStorage.getItem('participatingChallenges') || '[]');
          const participationKey = `${challengeId}_${userId}`;
          
          const isParticipatingFromStorage = participatingChallenges.includes(participationKey);
          console.log(`AsyncStorage에서 참여 상태 확인 (${participationKey}): ${isParticipatingFromStorage}`);
          
          if (isParticipatingFromStorage) {
            console.log('로컬 저장소에서 참여중 상태 확인됨');
            setIsParticipating(true);
          }
        } else {
          console.log('사용자 정보 없음, 저장소 확인 스킵');
        }
      } catch (storageErr) {
        console.error('AsyncStorage 읽기 오류:', storageErr);
      }
      
      // 2. 백엔드 API에서 데이터 가져오기
      const data = await fetchChallengeDetail(challengeId);
      console.log('챌린지 상세 정보 로드됨:', data);
      setChallenge(data);
      
      // 3. API 데이터로 참여 상태 확인
      if (userData && data.participants && Array.isArray(data.participants)) {
        console.log(`참여자 수: ${data.participants.length}`);
        console.log('현재 사용자 ID:', userData.id);
        
        const userIdString = String(userData.id);
        let foundParticipation = false;
        
        // 각 참여자를 개별적으로 확인하여 로그 출력
        data.participants.forEach(participant => {
          const participantIdString = String(participant.userId);
          console.log(`참여자 ID 비교: ${participantIdString} vs ${userIdString}`);
          if (participantIdString === userIdString) {
            console.log('일치하는 참여자 발견!');
            foundParticipation = true;
          }
        });
        
        console.log('서버 데이터 기반 참여 상태:', foundParticipation);
        
        // 4. 서버 데이터에서 참여 중임이 확인되면 무조건 참여중으로 설정
        if (foundParticipation) {
          setIsParticipating(true);
          
          // AsyncStorage에도 저장
          try {
            if (userData) {
              const userId = userData.id;
              const participationKey = `${challengeId}_${userId}`;
              const participatingChallenges = JSON.parse(await AsyncStorage.getItem('participatingChallenges') || '[]');
              
              if (!participatingChallenges.includes(participationKey)) {
                participatingChallenges.push(participationKey);
                await AsyncStorage.setItem('participatingChallenges', JSON.stringify(participatingChallenges));
                console.log(`참여 중인 챌린지 저장됨 (${participationKey}):`, participatingChallenges);
              }
            } else {
              console.log('사용자 정보 없음, 저장 스킵');
            }
          } catch (storageErr) {
            console.error('AsyncStorage 저장 오류:', storageErr);
          }
        }
      } else {
        console.log('참여자 확인 불가: 사용자 정보 또는 참여자 목록 없음');
        // 참여 상태는 서버에서 false를 받았지만 AsyncStorage에서 이미 true로 설정되어 있다면 그대로 유지
        // 원래 참여했던 챌린지라면 서버 오류로 인해 데이터가 완전히 사라지지 않도록 보호
      }
      
      setParticipationChecked(true);
      setLoading(false);
    } catch (err) {
      console.error('챌린지 상세 정보 불러오기 오류:', err);
      setError('챌린지 정보를 불러오는 중 오류가 발생했습니다.');
      setLoading(false);
    }
  };
  
  // 초기 로드
  useEffect(() => {
    if (challengeId && userData) {
      loadChallengeDetail();
    }
  }, [challengeId, userData]);

  // URL 안전하게 처리
  const safeUri = (uri) => {
    if (!uri) return '';
    return uri.startsWith('http') ? uri : `https://${uri}`;
  };

  // 카테고리 이미지 불러오기
  useEffect(() => {
    const loadCategoryData = async () => {
      try {
        // fetchChallengeCategories를 사용하여 백엔드에서 카테고리 정보 가져오기
        const categoriesData = await fetchChallengeCategories();
        console.log('백엔드 카테고리 데이터:', categoriesData);

        // 현재 챌린지의 카테고리에 맞는 이미지 URL 찾기
        if (challenge && challenge.category && Array.isArray(categoriesData) && categoriesData.length > 0) {
          const currentCategory = categoriesData.find(cat =>
            cat.category === challenge.category
          );

          if (currentCategory && currentCategory.imageUrl) {
            console.log('카테고리 이미지 URL 찾음:', currentCategory.imageUrl);
            setCategoryImage(currentCategory.imageUrl);
          } else {
            // 같은 배열에서 다른 방법으로 이미지 찾기 시도
            console.log('백엔드에서 category가 아닌 다른 키로 카테고리 찾기 시도...');
            for (const cat of categoriesData) {
              if (cat.name === challenge.category || cat.name === getCategoryName(challenge.category)) {
                console.log('다른 키를 통해 카테고리 찾음:', cat);
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

    if (isParticipating) {
      Alert.alert('안내', '이미 참여 중인 챌린지입니다.');
      return;
    }

    try {
      setLoading(true);
      console.log(`챌린지 참여 요청 시작 (ID: ${challengeId})`);
      
      const result = await joinChallenge(challengeId);
      console.log('챌린지 참여 API 결과:', result);

      // 이미 참여 중이거나 참여 성공인 경우
      if (result.success || result.alreadyJoined) {
        console.log('참여 상태를 true로 변경');
        setIsParticipating(true);
        
        // 로컬 상태 업데이트
        setChallenge(prev => {
          if (!prev) return null;
          
          const updatedParticipants = [...(prev.participants || [])];
          
          // 이미 있는지 확인
          const alreadyExists = updatedParticipants.some(
            p => String(p.userId) === String(userData.id)
          );
          
          // 없으면 추가
          if (!alreadyExists) {
            console.log('참여자 목록에 현재 사용자 추가');
            updatedParticipants.push({
              userId: userData.id,
              nickname: userData.nickname
            });
          }
          
          return {
            ...prev,
            currentParticipants: (prev.currentParticipants || 0) + (alreadyExists ? 0 : 1),
            participants: updatedParticipants
          };
        });

        // 참여 완료 메시지
        const message = result.alreadyJoined ? 
          '이미 참여 중인 챌린지입니다.' : 
          '챌린지 참여가 완료되었습니다!';
          
        Alert.alert('성공', message);
        
        // 데이터를 다시 로드하여 최신 상태 유지
        await loadChallengeDetail();
        
        // 상태를 AsyncStorage에 저장하여 영구적으로 유지
        try {
          if (userData) {
            const userId = userData.id;
            const participationKey = `${challengeId}_${userId}`;
            
            // 데이터베이스와 일관되게 challengeId_userId 형태로 저장
            const participatingChallenges = JSON.parse(await AsyncStorage.getItem('participatingChallenges') || '[]');
            if (!participatingChallenges.includes(participationKey)) {
              participatingChallenges.push(participationKey);
              await AsyncStorage.setItem('participatingChallenges', JSON.stringify(participatingChallenges));
              console.log(`참여 중인 챌린지 저장됨 (${userId}님이 ${challengeId} 챌린지 참여):`, participatingChallenges);
            }
          } else {
            console.log('사용자 정보 없음, 저장 불가');
          }
        } catch (storageErr) {
          console.error('AsyncStorage 저장 오류:', storageErr);
        }
      } else {
        // 참여에 실패한 경우
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

          {/* 제목과 아이콘 */}
          <View className="flex-row items-center justify-center mt-2 mb-4">
            {challenge.isPrivate && (
              <Icon name="lock-closed" size={20} color="#000" style={{ marginRight: 8 }} />
            )}
            <Text className="text-[24px] font-bold flex-1 text-center">{challenge.title}</Text>
          </View>

          {/* 카테고리 이미지 - 중앙 배치 */}
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
                  {challenge.participants && challenge.participants[0] ? 
                    challenge.participants[0].nickname : 
                    ''}
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
                  {challenge.participants && challenge.participants[1] ? 
                    challenge.participants[1].nickname : 
                    ''}
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
                  {challenge.participants && challenge.participants[2] ? 
                    challenge.participants[2].nickname : 
                    ''}
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

            <View className="flex-row py-2">
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
            className={`py-4 rounded-[10px] items-center justify-center mb-6 ${buttonDisabled ? 'bg-gray-400' : 'bg-Fineed-green'}`}
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
