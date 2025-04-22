import { View, Text, TouchableOpacity, Image, useWindowDimensions, Linking } from 'react-native';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { useLoading } from '../../hooks/useLoading';
import { useNavigation } from '@react-navigation/native';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function QuizAndNews() {
    const { width } = useWindowDimensions();
    const cardWidth = (width - 40) / 2 - 8;
    const [article, setArticle] = useState(null);
    const [loading, setLoading] = useState(true);
    const { showLoading, hideLoading } = useLoading();
    const navigation = useNavigation();

    useEffect(() => {
        fetchTodayArticle();
    }, []);

    const fetchTodayArticle = async () => {
        try {
            setLoading(true);
            const response = await axios.get(`${API_BASE_URL}/rss/list`);
            const articles = response.data;

            if (articles && articles.length > 0) {
                const latestArticle = articles
                    .filter(article => article.status === 'COMPLETED')
                    .sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate))[0];

                setArticle(latestArticle);
            }
        } catch (error) {
            console.error('기사를 불러오는데 실패했습니다:', error);
        } finally {
            setLoading(false);
        }
    };

    const formatTodayDate = () => {
        const today = new Date();
        return format(today, 'MM월 dd일 오늘의 기사', { locale: ko });
    };

    const openArticleUrl = async (url) => {
        if (url) {
            showLoading();
            try {
                await Linking.openURL(url);
            } catch (err) {
                console.error('링크를 열 수 없습니다:', err);
            } finally {
                hideLoading();
            }
        }
    };

    // 퀴즈 화면으로 이동하는 함수
    const navigateToQuiz = () => {
        navigation.navigate('Quiz');
    };

    return (
        <View className="flex-row justify-between mt-3">
            {/* 퀴즈 섹션 */}
            <View style={{ width: cardWidth }}>
                <Text className="text-[20px] font-bold text-Fineed-green mb-3">맞춤 퀴즈</Text>
                <TouchableOpacity
                    style={{ height: cardWidth * 1.15 }}
                    className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                    onPress={navigateToQuiz}
                >
                    <Image
                        source={require('../../../assets/images/test.png')}
                        style={{ width: width > 380 ? 80 : 70, height: width > 380 ? 80 : 70 }}
                        resizeMode="contain"
                    />
                    <Text className="text-[18px] font-bold text-black text-center mt-4">금융 퀴즈</Text>
                    <Text className="text-[18px] font-bold text-black text-center">풀러 가기</Text>
                </TouchableOpacity>
            </View>

            {/* 추천 기사 섹션 */}
            <View style={{ width: cardWidth }}>
                <Text className="text-[20px] font-bold text-Fineed-green mb-3">추천 기사</Text>
                <View
                    style={{ height: cardWidth * 1.15 }}
                    className="bg-white rounded-[20px] shadow-md p-5"
                >
                    {loading ? (
                        <Text className="text-[12px] text-[#6D6D6D] text-center">로딩 중...</Text>
                    ) : article ? (
                        <>
                            <View className="mb-2">
                                <Text className="text-[10px] text-[#6D6D6D]">
                                    {formatTodayDate()}
                                </Text>
                            </View>
                            <TouchableOpacity onPress={() => openArticleUrl(article.sourceUrl)}>
                                <Text className="text-[16px] font-bold text-black" numberOfLines={3}>
                                    {article.title}
                                </Text>
                            </TouchableOpacity>
                            <Text className="text-[10px] text-[#6D6D6D] mt-2" numberOfLines={2}>
                                {article.summary}
                            </Text>

                            <TouchableOpacity
                                onPress={() => navigation.navigate('NewsList')}
                                className="flex-row justify-center mt-4"
                            >
                                <Text className="text-[12px] text-Fineed-green">금융 기사 더 보러 가기 {'>'}</Text>
                            </TouchableOpacity>
                        </>
                    ) : (
                        <Text className="text-[10px] text-[#6D6D6D] text-center mt-10">
                            오늘의 추천 기사를 불러올 수 없습니다.
                        </Text>
                    )}
                </View>
            </View>
        </View>
    );
}
