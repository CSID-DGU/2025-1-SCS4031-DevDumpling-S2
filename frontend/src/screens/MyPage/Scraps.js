import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import { useState, useEffect } from 'react';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { format } from 'date-fns';

const API_BASE_URL = 'http://52.78.59.11:8080';

const Scraps = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const navigation = useNavigation();
    const [scrappedArticles, setScrappedArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [userName, setUserName] = useState('');

    useEffect(() => {
        fetchScrappedArticles();
        fetchUserName();
    }, []);

    const fetchUserName = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken');
            if (!token) return;

            const response = await axios.get(`${API_BASE_URL}/user/info`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUserName(response.data.name);
        } catch (error) {
            console.error('사용자 정보 조회 실패:', error);
        }
    };

    const fetchScrappedArticles = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken');
            if (!token) return;

            // 1. 먼저 스크랩한 기사 ID 목록을 가져옴
            const scrappedResponse = await axios.get(`${API_BASE_URL}/rss/scrapped`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            console.log('스크랩한 기사 ID 목록:', scrappedResponse.data);

            if (!scrappedResponse.data || scrappedResponse.data.length === 0) {
                setScrappedArticles([]);
                setLoading(false);
                return;
            }

            // 2. 각 기사의 상세 정보를 가져옴
            const articlePromises = scrappedResponse.data.map(async (scrap) => {
                try {
                    console.log('스크랩 정보:', scrap);
                    // id 또는 articleId 필드 확인
                    const articleId = scrap.id || scrap.articleId;
                    if (!articleId) {
                        console.error('스크랩 정보에 id 또는 articleId가 없습니다:', scrap);
                        return null;
                    }

                    const articleResponse = await axios.get(`${API_BASE_URL}/rss/${articleId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    console.log('기사 상세 응답:', articleResponse.data);
                    return articleResponse.data;
                } catch (error) {
                    console.error(`기사 ID ${articleId} 조회 실패:`, error);
                    if (error.response) {
                        console.error('에러 상태:', error.response.status);
                        console.error('에러 데이터:', error.response.data);
                    }
                    return null;
                }
            });

            // 3. 모든 기사 정보를 병렬로 가져옴
            const articles = await Promise.all(articlePromises);
            
            // 4. null 값 제거하고 상태 업데이트
            const validArticles = articles.filter(article => article !== null);
            console.log('가져온 기사 상세 정보:', validArticles);
            setScrappedArticles(validArticles);
        } catch (error) {
            console.error('스크랩한 기사 목록 조회 중 오류 발생:', error);
            if (error.response) {
                console.error('에러 상태:', error.response.status);
                console.error('에러 데이터:', error.response.data);
            }
            setScrappedArticles([]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-5 px-5 pb-10">
                <ScrollView
                    contentContainerClassName="justify-center"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                }}>
                    <Text className="text-2xl text-[#014029] font-bold mb-8">{userName}님이 스크랩한 기사</Text>
                    {loading ? (
                        <Text className="text-center text-gray-500">로딩 중...</Text>
                    ) : scrappedArticles.length > 0 ? (
                        scrappedArticles.map((article, index) => (
                            <TouchableOpacity 
                                key={index}
                                onPress={() => navigation.navigate('NewsDetail', { articleId: article.id })}
                                className="bg-[#F9F9F9] p-4 rounded-2xl shadow-md mb-4"
                            >
                                <Text className="text-xs text-gray-500 mb-1">{article.newspaperName || '전자신문'}</Text>
                                <Text className="text-base font-bold mb-2" numberOfLines={2}>{article.title}</Text>
                                <Text className="text-xs text-gray-500">{format(new Date(article.publishDate), 'yyyy-MM-dd HH:mm')}</Text>
                            </TouchableOpacity>
                        ))
                    ) : (
                        <Text className="text-center text-gray-500">스크랩한 기사가 없습니다.</Text>
                    )}
                </ScrollView>
            </View>
        </>
    );
};

export default Scraps;
