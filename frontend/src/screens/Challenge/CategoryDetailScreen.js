import React, { useState, useEffect } from 'react';
import { View, Text, Image, TouchableOpacity, ActivityIndicator, FlatList, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { fetchChallengesByCategory, fetchChallengeCategories, getCategoryCode, getCategoryName } from './ChallengeApi';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

const SecretChallengeImg = require('../../../assets/images/secretchallenge.png');
const VectorImg = require('../../../assets/images/Vector.png');

export default function CategoryDetailScreen(props) {
  // 안전하게 파라미터 처리
  const route = props.route || {};
  const params = route.params || {};
  const categoryName = params.categoryName || '카테고리';
  const categoryCode = params.categoryCode || '';

  const navigation = useNavigation();
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [categoryImage, setCategoryImage] = useState(null);
  const [allCategories, setAllCategories] = useState([]);

  // 카테고리 이미지 맵
  const getCategoryImage = (category) => {
    const categoryImageMap = {
      'FOOD': require('../../../assets/images/donut.png'),
      'CAFE_SNACK': require('../../../assets/images/donut.png'),
      'SHOPPING': require('../../../assets/images/test.png'),
      'SAVINGS': require('../../../assets/images/piggy-bank.png'),
      'ALCOHOL_ENTERTAINMENT': require('../../../assets/images/secretchallenge.png'),
      'BEAUTY': require('../../../assets/images/secretchallenge.png'),
      'TRAVEL': require('../../../assets/images/plane-ticket.png'),
      'PET': require('../../../assets/images/secretchallenge.png'),
      'MART_CONVENIENCE': require('../../../assets/images/secretchallenge.png'),
      'GAME_OTT': require('../../../assets/images/secretchallenge.png'),
      'HOUSING_COMMUNICATION': require('../../../assets/images/secretchallenge.png'),
      'TRANSPORTATION': require('../../../assets/images/secretchallenge.png'),
      'HEALTH_EXERCISE': require('../../../assets/images/secretchallenge.png'),
      'NEW_DISCOUNT': require('../../../assets/images/secretchallenge.png'),
      'PRIVATE': require('../../../assets/images/secretchallenge.png')
    };

    return categoryImageMap[category] || SecretChallengeImg;
  };

  // URL 안전하게 처리
  const safeUri = (uri) => {
    if (!uri) return '';
    return uri.startsWith('http') ? uri : `https://${uri}`;
  };

  // 백엔드에서 모든 카테고리 정보 가져오기
  useEffect(() => {
    const loadCategoryData = async () => {
      try {
        const categoriesData = await fetchChallengeCategories();
        console.log('백엔드 카테고리 데이터:', categoriesData);
        setAllCategories(categoriesData || []);

        // 현재 카테고리에 맞는 이미지 URL 찾기
        if (Array.isArray(categoriesData) && categoriesData.length > 0) {
          const currentCategory = categoriesData.find(cat =>
            cat.name === categoryName || getCategoryName(cat.name) === categoryName
          );

          if (currentCategory && currentCategory.imageUrl) {
            console.log('카테고리 이미지 URL 찾음:', currentCategory.imageUrl);
            setCategoryImage(currentCategory.imageUrl);
          }
        }
      } catch (err) {
        console.error('카테고리 정보 로딩 오류:', err);
      }
    };

    loadCategoryData();
  }, [categoryName]);

  // 카테고리별 챌린지 목록 가져오기
  useEffect(() => {
    const loadChallenges = async () => {
      // 한글 카테고리명을 영문 코드로 변환
      const englishCategoryCode = getCategoryCode(categoryName || categoryCode);
      console.log('변환된 카테고리 코드:', englishCategoryCode, '원본:', categoryName || categoryCode);

      if (!englishCategoryCode) {
        setLoading(false);
        setError(`유효한 카테고리 코드가 없습니다 (${categoryName || categoryCode})`);
        return;
      }

      try {
        setLoading(true);
        console.log(`카테고리 ${categoryName}(${englishCategoryCode})의 챌린지 로딩 중...`);

        // 변환된 영문 코드로 API 호출
        const data = await fetchChallengesByCategory(englishCategoryCode);
        console.log('받은 챌린지 데이터:', data);
        
        // 데이터 구조 확인 및 처리
        const challengesData = data.content || [];
        console.log('처리된 챌린지 데이터:', challengesData);
        
        // 각 챌린지의 ID 확인
        challengesData.forEach((challenge, index) => {
          console.log(`챌린지 ${index} ID:`, challenge.id, '전체 데이터:', challenge);
        });
        
        setChallenges(challengesData);
        setLoading(false);
      } catch (err) {
        console.error('챌린지 로딩 오류:', err);
        setError('챌린지를 불러오는 중 오류가 발생했습니다');
        setLoading(false);
      }
    };

    loadChallenges();
  }, [categoryCode, categoryName]);

  const renderChallengeItem = ({ item }) => {
    console.log('렌더링할 챌린지 아이템:', item);

    // 이미지 소스 가져오기
    let src;
    if (item.imageUrl) {
      src = { uri: safeUri(item.imageUrl) };
    } else if (categoryImage) {
      src = { uri: safeUri(categoryImage) };
    } else {
      const imgSrc = getCategoryImage(item.category);
      src = typeof imgSrc === 'string' ? { uri: safeUri(imgSrc) } : imgSrc;
    }

    // ID 처리
    const challengeId = item.id;
    console.log('사용할 challengeId:', challengeId, '아이템:', item);

    if (!challengeId) {
      console.error('챌린지 ID가 없습니다:', item);
      return null;
    }

    return (
      <TouchableOpacity
        className="bg-white rounded-[20px] p-4 shadow-sm mb-3"
        onPress={() => {
          console.log('챌린지 상세 화면으로 이동:', challengeId);
          if (!challengeId) {
            console.error('challengeId가 없습니다!');
            return;
          }
          navigation.navigate('ChallengeDetailScreen', { challengeId });
        }}
      >
        <View className="flex-row items-center">
          <Image source={src} style={{ width: 56, height: 56 }} resizeMode="contain" />
          <View className="ml-3 flex-1">
            <Text className="text-[16px] font-bold" numberOfLines={1}>{item.title}</Text>
            <Text className="text-[12px] text-[#6D6D6D]" numberOfLines={1}>
              좋아요 {item.likes || 0}개 · {item.currentParticipants || 0}명 참여 중
            </Text>
          </View>
          <Icon name="heart-outline" size={24} color="#D1D5DB" />
        </View>
      </TouchableOpacity>
    );
  };

  if (loading) {
    return (
      <View className="flex-1 justify-center items-center">
        <ActivityIndicator size="large" color="#014029" />
      </View>
    );
  }

  if (error) {
    return (
      <View className="flex-1 justify-center items-center">
        <Text className="text-red-500">{error}</Text>
        <TouchableOpacity
          className="mt-4 bg-Fineed-green py-2 px-4 rounded-lg"
          onPress={() => navigation.goBack()}
        >
          <Text className="text-white">돌아가기</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <>
      <Header />
      <SafeAreaView className="flex-1 bg-Fineed-background">

        <View className="flex-1 px-4">
          {challenges.length > 0 ? (
            <FlatList
              data={challenges}
              renderItem={renderChallengeItem}
              keyExtractor={(item) => item.id?.toString() || Math.random().toString()}
              showsVerticalScrollIndicator={false}
              contentContainerStyle={{ paddingTop: 16, paddingBottom: 16 }}
            />
          ) : (
            <View className="flex-1 justify-center items-center">
              <Text className="text-[#6D6D6D] text-center">
                아직 이 카테고리에 챌린지가 없습니다.{'\n'}
                새로운 챌린지를 만들어 보세요!
              </Text>
              <TouchableOpacity
                className="mt-4 bg-Fineed-green py-2 px-4 rounded-lg"
                onPress={() => navigation.navigate('CreateChallenge')}
              >
                <Text className="text-white">챌린지 만들기</Text>
              </TouchableOpacity>
            </View>
          )}
        </View>
      </SafeAreaView>
    </>
  );
}
