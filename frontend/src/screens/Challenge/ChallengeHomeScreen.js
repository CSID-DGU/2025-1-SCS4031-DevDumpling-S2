import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { fetchChallengeCategories, fetchParticipatingChallenges } from './ChallengeApi';
import axios from 'axios';
const BASE_URL = 'http://52.78.59.11:8080/api';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import AsyncStorage from '@react-native-async-storage/async-storage';
// 로컬 이미지 (새 챌린지, 비공개 챌린지)
import SecretChallengeImg from '../../../assets/images/secretchallenge.png';
import VectorImg from '../../../assets/images/Vector.png';

export default function ChallengeHomeScreen() {
  const navigation = useNavigation();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [participatingChallenges, setParticipatingChallenges] = useState([]);
  const [userName, setUserName] = useState('');
  const [recommendedChallenges, setRecommendedChallenges] = useState([
    {
      id: 101,
      title: '떠나자! 여행 자금 모으기',
      description: '님과 비슷한 20대가 많이 참여 중이에요!',
      participantCount: 89,
      likes: 130,
      category: 'TRAVEL'
    }
  ]);

  // 카테고리 코드 변환 함수 (한글 → 영문 코드)
  const getCategoryCode = (name) => {
    const cleanName = name.replace(/\/+/g, '');

    const categoryMap = {
      '새 챌린지': 'NEW_DISCOUNT',
      '비공개 챌린지': 'PRIVATE',
      '도서교육': 'BOOK_EDUCATION',
      '식비': 'FOOD',
      '카페간식': 'CAFE_SNACK',
      '저축': 'SAVINGS',
      '술유흥': 'ALCOHOL_ENTERTAINMENT',
      '쇼핑': 'SHOPPING',
      '미용': 'BEAUTY',
      '여행': 'TRAVEL',
      '반려동물': 'PET',
      '편의점마트잡화': 'MART_CONVENIENCE',
      '게임': 'GAME_OTT',
      '주거통신': 'HOUSING_COMMUNICATION',
      '교통': 'TRANSPORTATION',
      '의료건강피트니스': 'HEALTH_EXERCISE'
    };

    // 1. 원본 이름으로 찾기
    if (categoryMap[name]) return categoryMap[name];
    // 2. 슬래시 제거 후 찾기
    if (categoryMap[cleanName]) return categoryMap[cleanName];
    // 3. 기본값
    return 'FOOD';
  };

  // 카테고리 코드 → 이미지 URL
  const getCategoryImage = (code) => {
    // 로컬 이미지는 새 챌린지와 비공개 챌린지만
    if (code === 'PRIVATE') {
      return SecretChallengeImg;
    }
    if (code === 'NEW_DISCOUNT') {
      return VectorImg;
    }

    // 기타 카테고리는 백엔드 URL 사용
    const base = 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/';
    return `${base}${code}.png`;
  };

  // 한글 path 가 포함된 이미지 안전 처리
  const safeUri = (uri) => encodeURI(uri);

  // 데이터 불러오기
  useEffect(() => {
    const loadData = async () => {
      try {
        setLoading(true);
        const token = await AsyncStorage.getItem('userToken');
        setIsLoggedIn(!!token);

        // 사용자 닉네임 가져오기 - 로컬에 저장된 userData에서 가져오기
        if (token) {
          try {
            const userData = await AsyncStorage.getItem('userData');
            console.log('userData:', userData);
            if (userData) {
              const parsedUserData = JSON.parse(userData);
              console.log('parsedUserData:', parsedUserData);
              setUserName(parsedUserData.nickname || '');
            }
          } catch (userError) {
            console.error('사용자 정보 가져오기 실패:', userError);
          }
        }

        // API 호출
        const [categoriesData, participatingChallengesData] = await Promise.all([
          fetchChallengeCategories(),
          fetchParticipatingChallenges()
        ]);

        console.log('Backend categories:', categoriesData);

        // 원하는 표시 순서 정리
        const displayOrder = [
          { name: '새 챌린지', code: 'NEW_DISCOUNT' },
          { name: '비공개 챌린지', code: 'PRIVATE' },
          { name: '여행', code: 'TRAVEL' },
          { name: '저축', code: 'SAVINGS' },
          { name: '식비', code: 'FOOD' },
          { name: '카페/간식', code: 'CAFE_SNACK' },
          { name: '마트/편의점', code: 'MART_CONVENIENCE' },
          { name: '술/유흥', code: 'ALCOHOL_ENTERTAINMENT' },
          { name: '반려동물', code: 'PET' },
          { name: '쇼핑', code: 'SHOPPING' },
          { name: '미용', code: 'BEAUTY' },
          { name: '게임/OTT', code: 'GAME_OTT' },
          { name: '교통', code: 'TRANSPORTATION' },
          { name: '건강/운동', code: 'HEALTH_EXERCISE' },
          { name: '도서/교육', code: 'NEW_DISCOUNT' }
        ];

        // Map 백엔드 데이터로 빠르게 찾기
        const backendMap = new Map();

        // 슬래시 제거 매핑 (프론트 표시명 -> 백엔드명)
        const nameMapping = {
          '카페/간식': '카페간식',
          '도서/교육': '도서교육',
          '술/유흥': '술유흥',
          '마트/편의점': '편의점마트잡화',
          '게임/OTT': '게임',
          '건강/운동': '의료건강피트니스',
          '주거/통신': '주거통신'
        };

        // 백엔드 데이터 맵 구성 - 원본명으로 저장
        categoriesData.forEach(cat => {
          backendMap.set(cat.name, cat);
        });

        const orderedCategories = displayOrder.map(({ name, code }) => {
          // 1. 백엔드에서 정확히 일치하는 이름으로 찾기
          if (backendMap.has(name)) {
            return {
              ...backendMap.get(name),
              displayName: name // 화면에 보여줄 이름 (슬래시 포함)
            };
          }

          // 2. 매핑을 통해 변환된 이름으로 찾기
          const mappedName = nameMapping[name];
          if (mappedName && backendMap.has(mappedName)) {
            return {
              ...backendMap.get(mappedName),
              displayName: name // 화면에 보여줄 이름 (슬래시 포함)
            };
          }

          // 3. 기본값
          return {
            name,
            displayName: name,
            imageUrl: getCategoryImage(code)
          };
        });

        setCategories(orderedCategories);
        setParticipatingChallenges(participatingChallengesData.content || []);
        // TODO: 추천 챌린지 API 완성되면 교체
        setRecommendedChallenges(recommendedChallenges.map(rc => ({ ...rc, description: `${userName}${rc.description}` })));
        setLoading(false);
      } catch (err) {
        console.error('데이터 불러오기 오류:', err);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleCategoryPress = (categoryName) => {
    console.log(`${categoryName} 카테고리 선택됨`);
    if (categoryName === '새 챌린지') {
      navigation.navigate('CreateChallengeScreen');
    } else {
      navigation.navigate('CategoryDetailScreen', { categoryName });
    }
  };

  const handleChallengePress = (challengeId) => {
    // 챌린지 상세 페이지로 이동 (추후 구현)
    console.log(`챌린지 ID: ${challengeId} 선택됨`);
    // navigation.navigate('ChallengeDetail', { challengeId });
  };

  if (loading) {
    return (
      <View className="flex-1 justify-center items-center bg-Fineed-background">
        <ActivityIndicator size="large" color="#014029" />
      </View>
    );
  }

  return (
    <View className="flex-1 bg-Fineed-background">
      <Header />
      <ScrollView className="flex-1 px-4">
        <View className="mb-6 mt-3">
          <View className="mb-2">
            <Text className="text-[24px] font-bold">{userName || 'User'}님께 추천하는 챌린지</Text>
          </View>

          {/* 추천 챌린지 카드 */}
          <View className="bg-white rounded-[20px] p-5 shadow-sm mb-4 w-full">
            <View className="flex-row justify-between items-center mb-3">
              <Text className="text-[13px] text-[#6D6D6D]">{userName || 'User'}님과 비슷한 20대가 많이 참여 중이에요!</Text>
              <TouchableOpacity>
                <Text className="text-[12px] text-[#6D6D6D]">자세히 보기 {'>'}</Text>
              </TouchableOpacity>
            </View>

            {/* 추천 챌린지 메인 컨텐츠 */}
            {recommendedChallenges.length > 0 && recommendedChallenges[0] && (
              <TouchableOpacity
                className="mt-3 w-full"
                activeOpacity={0.8}
                onPress={() => handleChallengePress(recommendedChallenges[0].id)}
              >
                <View className="flex-row items-center bg-white rounded-lg p-4">
                  {/* 여행 이미지 - 로컬 이미지 파일 사용 */}
                  <View className="mr-4">
                    <Image
                      source={require('../../../assets/images/plane-ticket.png')}
                      style={{ width: 100, height: 80 }}
                      resizeMode="contain"
                    />
                  </View>

                  {/* 텍스트 영역 */}
                  <View className="flex-1">
                    <Text className="text-xl font-bold">{recommendedChallenges[0].title}</Text>
                    <Text className="text-[#6D6D6D] text-sm mt-1">좋아요 {recommendedChallenges[0].likes} · {recommendedChallenges[0].participantCount}명 참여 중</Text>
                  </View>
                </View>
              </TouchableOpacity>
            )}
          </View>
        </View>

        {/* 참여중인 챌린지 */}
        <View className="mb-6">
          <Text className="text-xl font-bold mb-3">참여중인 챌린지</Text>

          {participatingChallenges.length > 0 ? (
            <View>
              {participatingChallenges.map((item) => (
                <TouchableOpacity
                  key={item.id}
                  className="bg-white rounded-[20px] p-4 flex-row items-center shadow-sm mb-3"
                  onPress={() => handleChallengePress(item.id)}
                >
                  <Image
                    source={item.imageUrl ? { uri: safeUri(item.imageUrl) } : getCategoryIcon(item.category)}
                    style={{ width: 56, height: 56 }}
                    resizeMode="contain"
                  />
                  <View className="flex-1 ml-3">
                    <Text className="text-[16px] font-bold text-black mb-1">{item.title}</Text>
                    <Text className="text-[12px] text-[#6D6D6D]">
                      현재 {item.progress || 0}% · 달성률 {item.progress || 0}%
                    </Text>
                  </View>
                  <View className="ml-2">
                    <Icon name="chatbubble-ellipses-outline" size={24} color="#6D6D6D" />
                  </View>
                </TouchableOpacity>
              ))}
            </View>
          ) : (
            <View className="bg-white rounded-[20px] p-5 shadow-sm">
              <Text className="text-center py-6 text-[#6D6D6D]">
                지금 참여중인 챌린지가 없어요.{"\n"}
                새로운 챌린지에 도전해보세요!
              </Text>
            </View>
          )}
        </View>

        {/* 구분선 */}
        <View className="h-[1px] bg-[#E5E5E5] mb-6" />

        {/* 카테고리 섹션 */}
        <View className="mb-6">
          {/* 카테고리 데이터가 백엔드에서 전달되었는지 확인 */}
          {categories.length > 0 ? (
            <View className="flex-row flex-wrap justify-between">
              {categories.map((category, index) => (
                <TouchableOpacity
                  key={index}
                  className="w-[32%] items-center mb-6"
                  onPress={() => handleCategoryPress(category.name)}
                >
                  <View className="w-24 h-24 bg-white rounded-xl items-center justify-center shadow-md mb-2">
                    {category.name === '새 챌린지' ? (
                      <Icon name="add-circle" size={48} color="#014029" />
                    ) : (
                      <Image
                        source={(() => {
                          // 이미지 URL 확인 및 안전 처리
                          const categoryCode = getCategoryCode(category.name);
                          // 비공개 챌린지는 로컬 이미지 사용
                          if (category.name === '비공개 챌린지') {
                            return SecretChallengeImg;
                          }

                          // 백엔드에서 제공한 이미지 URL이 있는 경우 사용
                          if (category.imageUrl) {
                            return { uri: safeUri(category.imageUrl) };
                          }

                          // '새 챌린지', '비공개 챌린지'는 로컬 이미지 사용
                          const src = getCategoryImage(categoryCode);
                          return typeof src === 'string' ? { uri: safeUri(src) } : src;
                        })()}
                        className="w-16 h-16"
                        resizeMode="contain"
                        onError={() => console.log(`이미지 로드 실패: ${category.name}`)}
                      />
                    )}
                  </View>
                  <Text className="text-sm text-center">{category.displayName || category.name}</Text>
                </TouchableOpacity>
              ))}
            </View>
          ) : (
            <View className="flex-row flex-wrap justify-between">
              {/* 기본 카테고리 목록 (백엔드에서 가져오지 못했을 때 표시) */}
              {[
                { name: '새 챌린지', code: 'NEW_DISCOUNT' },
                { name: '비공개 챌린지', code: 'PRIVATE' },
                { name: '도서/교육', icon: 'book-outline', color: '#014029' },
                { name: '식비', icon: 'restaurant-outline', color: '#6D6D6D' },
                { name: '카페/간식', icon: 'cafe-outline', color: '#BD7B2C' },
                { name: '저축', icon: 'wallet-outline', color: '#10B981' },
                { name: '술/유흥', icon: 'beer-outline', color: '#F9C800' },
                { name: '쇼핑', icon: 'cart-outline', color: '#EB4747' },
                { name: '미용', icon: 'cut-outline', color: '#FF9CCE' },
                { name: '여행', icon: 'airplane-outline', color: '#3B82F6' },
                { name: '반려동물', icon: 'paw-outline', color: '#FF6B96' }
              ].map((category, index) => (
                <TouchableOpacity
                  key={index}
                  className="w-[32%] items-center mb-6"
                  onPress={() => handleCategoryPress(category.name)}
                >
                  <View className="w-24 h-24 bg-white rounded-xl items-center justify-center shadow-md mb-2">
                    <Icon name={category.icon} size={42} color={category.color} />
                  </View>
                  <Text className="text-sm text-center">{category.displayName || category.name}</Text>
                </TouchableOpacity>
              ))}
            </View>
          )}
        </View>
      </ScrollView>
    </View>
  );
}
