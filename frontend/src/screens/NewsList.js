import React, { useState, useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Linking, useWindowDimensions } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import axios from 'axios';
import { format } from 'date-fns';
import { useLoading } from '../hooks/useLoading';
import Header from '../components/layout/Header';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function NewsListScreen({ navigation }) {
    const [articles, setArticles] = useState([]);
    const { width } = useWindowDimensions();
    const { showLoading, hideLoading } = useLoading();

    useEffect(() => {
        fetchArticles();
    }, []);

    const fetchArticles = async () => {
        try {
            showLoading();
            const response = await axios.get(`${API_BASE_URL}/rss/list`);
            if (response.data && response.data.length > 0) {
                // 상태가 COMPLETED인 기사들만 필터링하고 날짜순 정렬
                const completedArticles = response.data
                    .filter(article => article.status === 'COMPLETED')
                    .sort((a, b) => new Date(b.publishDate) - new Date(a.publishDate));
                setArticles(completedArticles);
            }
        } catch (error) {
            console.error('기사를 불러오는데 실패했습니다:', error);
        } finally {
            hideLoading();
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return format(date, 'yyyy-MM-dd HH:mm');
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

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-[#EFEFEF]">
                <View className="flex-1 px-4 ">
                    {/* 주의 추천 기사 헤더 */}
                    <View className="flex-row items-center mb-4">
                        <Text className="text-2xl font-bold mr-2">✍️ 이번 주의 추천 기사</Text>
                    </View>

                    {/* 추천 기사 설명 */}
                    <Text className="text-gray-500 text-sm mb-1">* 일주일 간의 기사를 보여드려요!</Text>
                    <Text className="text-gray-500 text-sm mb-4">스크랩한 기사는 일주일이 지나도 볼 수 있어요 🤗</Text>

                    {/* 기사 목록 */}
                    <ScrollView
                        className="flex-1"
                        contentContainerStyle={{
                            paddingBottom: 24
                        }}
                    >
                        {articles.map((article, index) => (
                            <TouchableOpacity
                                key={index}
                                onPress={() => openArticleUrl(article.sourceUrl)}
                                className="bg-white rounded-3xl p-4 mb-4 shadow-sm"
                            >
                                <Text className="text-xs text-gray-500 mb-1">{article.newspaperName || '오류'}</Text>
                                <Text className="text-base font-bold mb-2" numberOfLines={2}>
                                    {article.title}
                                </Text>
                                <Text className="text-xs text-gray-500">
                                    {formatDate(article.publishDate)}
                                </Text>
                            </TouchableOpacity>
                        ))}

                        {/* 메인으로 돌아가기 버튼 */}
                        <TouchableOpacity
                            className="items-center mt-4 mb-8"
                            onPress={() => navigation.navigate('Home')}
                        >
                            <Text className="text-sm text-gray-500">메인으로 돌아가기</Text>
                        </TouchableOpacity>
                    </ScrollView>
                </View>
            </SafeAreaView>
        </>
    );
}