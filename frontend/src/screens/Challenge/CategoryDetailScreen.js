import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView, TouchableOpacity, ActivityIndicator, FlatList } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { fetchChallengesByCategory } from './ChallengeApi';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import SecretChallengeImg from '../../../assets/images/secretchallenge.png';
import VectorImg from '../../../assets/images/Vector.png';

export default function CategoryDetailScreen({ route }) {
  const { categoryName } = route.params;
  const navigation = useNavigation();
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 카테고리명을 영어로 변환하는 함수 (백엔드 API 호출용)
  const getCategoryCode = (name) => {
    const categoryMap = {
      '새 챌린지': 'NEW_DISCOUNT',
      '비공개 챌린지': 'PRIVATE',
      '도서교육': 'NEW_DISCOUNT',
      '식비': 'FOOD',
      '카페/간식': 'CAFE_SNACK',
      '저축': 'SAVINGS',
      '술/유흥': 'ALCOHOL_ENTERTAINMENT',
      '쇼핑': 'SHOPPING',
      '미용': 'BEAUTY',
      '여행': 'TRAVEL',
      '반려동물': 'PET',
      '편의점/마트/잡화': 'MART_CONVENIENCE',
      '게임/OTT': 'GAME_OTT',
      '주거/통신': 'HOUSING_COMMUNICATION',
      '교통': 'TRANSPORTATION',
      '의료/건강/피트니스': 'HEALTH_EXERCISE'
    };

    return categoryMap[name] || 'FOOD'; // 기본값으로 FOOD 사용
  };

  useEffect(() => {
    loadChallenges();
  }, [categoryName]);

  const loadChallenges = async () => {
    setLoading(true);
    setError(null);
    
    try {
      // 카테고리 코드 가져오기
      const categoryCode = getCategoryCode(categoryName);
      console.log(`카테고리 유형 변환: ${categoryName} -> ${categoryCode}`);
      
      // 백엔드에 맞게 파라미터 전달
      const data = await fetchChallengesByCategory(categoryCode);
      
      // 데이터 설정
      setChallenges(data.content || []);
      setLoading(false);
      
      // 로그 추가
      console.log(`${categoryName} 챌린지 가져오기 성공: ${data.content?.length}개 찾음`);
    } catch (err) {
      console.error(`${categoryName} 챌린지 목록 불러오기 오류:`, err);
      setError('챌린지 목록을 불러올 수 없습니다.');
      setChallenges([]);
      setLoading(false);
    }
  };

  const renderChallengeItem = ({ item }) => {
    // 카테고리별 기본 이미지 URL/로컬 리소스
    const getChallengeImage = (category) => {
      switch (category) {
        case 'PRIVATE':
          return SecretChallengeImg;
        case 'NEW_DISCOUNT':
          return VectorImg;
        default:
          return `https://myapp-logos.s3.amazonaws.com/ChallengeIcons/${category}.png`;
      }
    };

    const imgSrc = item.imageUrl || getChallengeImage(item.category);

    return (
      <TouchableOpacity
        className="bg-white rounded-[20px] p-4 shadow-sm mb-3"
        onPress={() => navigation.navigate('ChallengeDetail', { challengeId: item.id })}
      >
        <View className="flex-row items-center">
          <Image
            source={typeof imgSrc === 'string' ? { uri: imgSrc } : imgSrc}
            style={{ width: 56, height: 56 }}
            resizeMode="contain"
          />
          <View className="ml-3 flex-1">
            <Text className="text-[16px] font-bold text-black" numberOfLines={1}>{item.title}</Text>
            <Text className="text-[12px] text-[#6D6D6D]" numberOfLines={1}>
              좋아요 {item.likes || 0}개 · {item.participantCount || 0}명 참여 중
            </Text>
          </View>
          <Icon name="heart-outline" size={24} color="#D1D5DB" />
        </View>
      </TouchableOpacity>
    );
  };

  const renderEmptyState = () => (
    <View className="items-center justify-center py-10">
      <Icon name="search-outline" size={48} color="#D9D9D9" />
      <Text className="text-[16px] text-[#6D6D6D] text-center mt-4">
        {`${categoryName} 카테고리에 챌린지가 없습니다.`}
      </Text>
    </View>
  );

  return (
    <View className="flex-1 bg-Fineed-background">
      <Header />
      <View className="px-4 py-2">
        <TouchableOpacity
          onPress={() => navigation.goBack()}
          className="flex-row items-center justify-center bg-Fineed-green rounded-full py-3 mb-4"
        >
          <Icon name="arrow-back" size={20} color="#fff" />
          <Text className="text-base font-bold text-white ml-1">{categoryName} 챌린지</Text>
        </TouchableOpacity>

        {/* 카테고리 필터 탭 제거 */}

        {loading ? (
          <View className="flex-1 justify-center items-center py-10">
            <ActivityIndicator size="large" color="#014029" />
          </View>
        ) : error ? (
          <View className="flex-1 justify-center items-center py-10">
            <Text className="text-red-500 text-center">{error}</Text>
          </View>
        ) : (
          <FlatList
            data={challenges.length > 0 ? challenges : []}
            renderItem={renderChallengeItem}
            keyExtractor={(item) => item.id.toString()}
            ListEmptyComponent={renderEmptyState}
            contentContainerStyle={{ paddingBottom: 20 }}
            showsVerticalScrollIndicator={false}
          />
        )}
      </View>
    </View>
  );
}
