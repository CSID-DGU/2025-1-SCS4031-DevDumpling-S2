import React, { useState, useEffect } from 'react';
import { View, Text, Image, ScrollView, TouchableOpacity, ActivityIndicator, FlatList } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import { fetchChallengesByCategory } from './ChallengeApi';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';

export default function CategoryDetailScreen({ route }) {
  const { categoryName } = route.params;
  const navigation = useNavigation();
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeFilter, setActiveFilter] = useState('ALL'); // ALL, ONGOING, UPCOMING, COMPLETED

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
  }, [categoryName, activeFilter]);

  const loadChallenges = async () => {
    try {
      setLoading(true);
      const status = activeFilter === 'ALL' ? null : activeFilter.toLowerCase();
      const categoryCode = getCategoryCode(categoryName);
      const data = await fetchChallengesByCategory(categoryCode, status);
      setChallenges(data.content || []);
      setLoading(false);
    } catch (err) {
      console.error('챌린지 목록 불러오기 오류:', err);
      setError('챌린지 목록을 불러오는 중 오류가 발생했습니다.');
      setLoading(false);
    }
  };

  const renderFilterTabs = () => {
    const filters = [
      { id: 'ALL', label: '전체' },
      { id: 'ONGOING', label: '진행중' },
      { id: 'UPCOMING', label: '예정' },
      { id: 'COMPLETED', label: '완료' }
    ];

    return (
      <View className="flex-row bg-white rounded-lg shadow-sm mb-4 p-1">
        {filters.map(filter => (
          <TouchableOpacity
            key={filter.id}
            className={`flex-1 py-2 px-3 rounded-md ${activeFilter === filter.id ? 'bg-Fineed-green' : 'bg-transparent'}`}
            onPress={() => setActiveFilter(filter.id)}
          >
            <Text 
              className={`text-center text-sm ${activeFilter === filter.id ? 'text-white font-medium' : 'text-gray-700'}`}
            >
              {filter.label}
            </Text>
          </TouchableOpacity>
        ))}
      </View>
    );
  };

  const renderChallengeItem = ({ item }) => {
    // 카테고리별 기본 이미지 URL 맵핑
    const getChallengeImage = (category) => {
      const imageMap = {
        'FOOD': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/FOOD.png',
        'TRAVEL': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/TRAVEL.png',
        'TRANSPORTATION': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/TRANSPORTATION.png',
        'PET': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/PET.png',
        'SAVINGS': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/SAVINGS.png',
        'CAFE_SNACK': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/CAFE_SNACK.png',
        'MART_CONVENIENCE': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/MART_CONVENIENCE.png',
        'ALCOHOL_ENTERTAINMENT': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/ALCOHOL_ENTERTAINMENT.png',
        'SHOPPING': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/SHOPPING.png',
        'BEAUTY': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/BEAUTY.png',
        'GAME_OTT': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/GAME_OTT.png',
        'HEALTH_EXERCISE': 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/HEALTH_EXERCISE.png'
      };
      
      return imageMap[category] || 'https://myapp-logos.s3.amazonaws.com/ChallengeIcons/NEW_DISCOUNT.png';
    };

    return (
      <TouchableOpacity
        className="bg-white rounded-[20px] p-4 shadow-md mb-4"
        onPress={() => navigation.navigate('ChallengeDetail', { challengeId: item.id })}
      >
        <View className="flex-row items-center mb-3">
          <Image
            source={{ uri: item.imageUrl || getChallengeImage(item.category) }}
            style={{ width: 60, height: 60 }}
            resizeMode="contain"
          />
          <View className="ml-3 flex-1">
            <Text className="text-[16px] font-bold text-black mb-1">{item.title}</Text>
            <Text className="text-[12px] text-[#6D6D6D]">
              {item.startDate?.substring(0, 10)} ~ {item.endDate?.substring(0, 10)}
            </Text>
          </View>
        </View>

        <View className="flex-row justify-between">
          <View className="flex-row items-center">
            <Icon name="people-outline" size={14} color="#6D6D6D" />
            <Text className="text-[12px] text-[#6D6D6D] ml-1">
              {item.participantCount || 0}명 참여
            </Text>
          </View>
          
          <View className="flex-row items-center">
            <Icon name="heart-outline" size={14} color="#6D6D6D" />
            <Text className="text-[12px] text-[#6D6D6D] ml-1">
              {item.likes || 0}
            </Text>
          </View>
          
          <View className="flex-row items-center">
            <Icon name="alert-circle-outline" size={14} color="#6D6D6D" />
            <Text className="text-[12px] text-[#6D6D6D] ml-1">
              난이도: {item.difficulty || '중간'}
            </Text>
          </View>
        </View>
      </TouchableOpacity>
    );
  };

  const renderEmptyState = () => (
    <View className="items-center justify-center py-10">
      <Icon name="search-outline" size={48} color="#D9D9D9" />
      <Text className="text-[16px] text-[#6D6D6D] text-center mt-4">
        {activeFilter === 'ALL' 
          ? `${categoryName} 카테고리에 챌린지가 없습니다.` 
          : `${activeFilter === 'ONGOING' ? '진행중인' : activeFilter === 'UPCOMING' ? '예정된' : '완료된'} 챌린지가 없습니다.`
        }
      </Text>
    </View>
  );

  return (
    <View className="flex-1 bg-Fineed-background">
      <Header />
      <View className="px-4 py-2">
        <View className="flex-row items-center mb-4">
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Icon name="arrow-back" size={24} color="#000" />
          </TouchableOpacity>
          <Text className="text-xl font-bold ml-2">{categoryName} 챌린지</Text>
        </View>
        
        {renderFilterTabs()}
        
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
