// src/screens/NewsDetailScreen.js
import React, { useEffect, useState } from 'react'
import { View, Text, ScrollView, TouchableOpacity, Linking, useWindowDimensions, StatusBar } from 'react-native'
import { SafeAreaView } from 'react-native-safe-area-context'
import { useRoute, useNavigation } from '@react-navigation/native'
import axios from 'axios'
import { format } from 'date-fns'
import { useLoading } from '../hooks/useLoading'
import { BookmarkIcon } from 'react-native-heroicons/outline'
import { BookmarkIcon as BookmarkSolidIcon } from 'react-native-heroicons/solid'
import AsyncStorage from '@react-native-async-storage/async-storage'
import Header from '../components/layout/Header';

const API_BASE_URL = 'http://52.78.59.11:8080'

export default function NewsDetailScreen() {
    const { params } = useRoute()
    const navigation = useNavigation()
    const { articleId } = params || {}
    const [article, setArticle] = useState(null)
    const [tab, setTab] = useState('summary')
    const [isBookmarked, setIsBookmarked] = useState(false)
    const { showLoading, hideLoading } = useLoading()
    const { width } = useWindowDimensions()

    useEffect(() => {
        console.log('NewsDetail params:', params)
        console.log('articleId:', articleId)
        if (!articleId) {
            console.error('articleId가 없습니다.')
            navigation.goBack()
            return
        }
        fetchDetail()
        checkBookmarkStatus()
    }, [articleId])

    const fetchDetail = async () => {
        try {
            showLoading()
            const url = `${API_BASE_URL}/rss/${articleId}`
            console.log('기사 상세 조회 요청:', url)
            const res = await axios.get(url)
            console.log('기사 상세 응답:', res.data)
            
            if (!res.data) {
                console.error('기사 데이터가 없습니다.')
                navigation.goBack()
                return
            }
            
            setArticle(res.data)
        } catch (e) {
            console.error('상세조회 실패:', e)
            if (e.response) {
                console.error('에러 상태:', e.response.status)
                console.error('에러 데이터:', e.response.data)
            }
            navigation.goBack()
        } finally {
            hideLoading()
        }
    }

    const checkBookmarkStatus = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken')
            if (!token) return

            const url = `${API_BASE_URL}/rss/scrapped`
            console.log('스크랩 상태 확인 요청:', url)
            const response = await axios.get(url, {
                headers: { Authorization: `Bearer ${token}` }
            })
            console.log('스크랩 목록 응답:', response.data)

            // 현재 기사가 스크랩 목록에 있는지 확인 (id 또는 articleId로 비교)
            const isScrapped = response.data.some(scrap => {
                console.log('스크랩 항목:', scrap)
                return scrap.article_id === articleId || scrap.id === articleId
            })
            console.log('스크랩 상태:', isScrapped)
            setIsBookmarked(isScrapped)
        } catch (error) {
            console.error('스크랩 상태 확인 실패:', error)
            if (error.response) {
                console.error('에러 상태:', error.response.status)
                console.error('에러 데이터:', error.response.data)
            }
        }
    }

    const toggleBookmark = async () => {
        try {
            const token = await AsyncStorage.getItem('userToken')
            if (!token) {
                // TODO: 로그인 필요 알림
                return
            }

            const headers = { Authorization: `Bearer ${token}` }

            if (isBookmarked) {
                // 스크랩 취소
                await axios.delete(`${API_BASE_URL}/rss/${articleId}/scrap`, { headers })
            } else {
                // 스크랩 추가
                await axios.post(`${API_BASE_URL}/rss/${articleId}/scrap`, {}, { headers })
            }

            setIsBookmarked(!isBookmarked)
        } catch (error) {
            console.error('스크랩 토글 실패:', error)
            if (error.response) {
                console.error('에러 상태:', error.response.status)
                console.error('에러 데이터:', error.response.data)
            }
            // TODO: 에러 처리 (예: 토스트 메시지)
        }
    }

    if (!article) return null

    return (
        <View className="flex-1 bg-[#EFEFEF]">
            <StatusBar barStyle="dark-content" />
            <SafeAreaView className="flex-1">
                <Header />

                <ScrollView contentContainerStyle={{ paddingBottom: 40 }}>
                    {/* 신문사 */}
                    <Text className="text-[10px] text-[#6D6D6D] text-center mt-6">{article.newspaperName || '전자신문'}</Text>

                    {/* 제목 */}
                    <Text className="text-[24px] font-extrabold text-[#014029] text-center px-[60px] mt-2 leading-8">
                        {article.title}
                    </Text>

                    {/* 날짜 */}
                    <Text className="text-[10px] text-[#6D6D6D] text-center mt-4">
                        {format(new Date(article.publishDate), 'yyyy-MM-dd HH:mm')}
                    </Text>

                    {/* 탭과 스크랩 섹션 */}
                    <View className="flex-row justify-between mt-6">
                        <View className="ml-6">
                            <View className="flex-row">
                                <View className="mr-2">
                                    <TouchableOpacity
                                        onPress={() => setTab('summary')}
                                        className={`w-[54px] h-[33px] rounded-[20px] justify-center items-center ${tab === 'summary' ? 'bg-[#014029]' : 'bg-[#F9F9F9]'} shadow-sm`}
                                    >
                                        <Text className={`text-[14px] font-semibold ${tab === 'summary' ? 'text-white' : 'text-black'}`}>
                                            요약
                                        </Text>
                                    </TouchableOpacity>
                                </View>

                                <View className="mr-2">
                                    <TouchableOpacity
                                        onPress={() => setTab('explanation')}
                                        className={`w-[54px] h-[33px] rounded-[20px] justify-center items-center ${tab === 'explanation' ? 'bg-[#014029]' : 'bg-[#F9F9F9]'} shadow-sm`}
                                    >
                                        <Text className={`text-[14px] font-semibold ${tab === 'explanation' ? 'text-white' : 'text-black'}`}>
                                            설명
                                        </Text>
                                    </TouchableOpacity>
                                </View>

                                <View>
                                    <TouchableOpacity
                                        onPress={() => setTab('keywords')}
                                        className={`w-[59px] h-[33px] rounded-[20px] justify-center items-center ${tab === 'keywords' ? 'bg-[#014029]' : 'bg-[#F9F9F9]'} shadow-sm`}
                                    >
                                        <Text className={`text-[14px] font-semibold ${tab === 'keywords' ? 'text-white' : 'text-black'}`}>
                                            키워드
                                        </Text>
                                    </TouchableOpacity>
                                </View>
                            </View>
                        </View>

                        {/* 스크랩 버튼 */}
                        <TouchableOpacity
                            onPress={toggleBookmark}
                            className="mr-6"
                        >
                            {isBookmarked ? (
                                <View className="items-center">
                                    <BookmarkSolidIcon color="#014029" size={20} />
                                    <Text className="text-[10px] text-[#6D6D6D] mt-1">스크랩</Text>
                                </View>
                            ) : (
                                <View className="items-center">
                                    <BookmarkIcon color="#014029" size={20} />
                                    <Text className="text-[10px] text-[#6D6D6D] mt-1">스크랩</Text>
                                </View>
                            )}
                        </TouchableOpacity>
                    </View>

                    {/* 내용 카드 */}
                    <View className="mx-6 mt-4">
                        <View className="bg-[#F9F9F9] rounded-[20px] p-6 shadow-sm w-full">
                            {tab === 'summary' && (
                                <View>
                                    <Text className="text-[20px] font-bold mb-4">📝 기사 요약</Text>
                                    <Text className="text-[15px] leading-[22px] text-black font-normal">{article.summary}</Text>
                                </View>
                            )}

                            {tab === 'explanation' && (
                                <View>
                                    <Text className="text-[20px] font-bold mb-4">🔍 기사 설명</Text>
                                    <Text className="text-[15px] leading-[22px] text-black font-normal">{article.explanation}</Text>
                                </View>
                            )}

                            {tab === 'keywords' && (
                                <View>
                                    <Text className="text-[20px] font-bold mb-4">🏷️ 키워드 정리</Text>

                                    {article.termExplanations && (
                                        (() => {
                                            try {
                                                // 문자열로 전달된 경우 JSON 파싱
                                                const termData = typeof article.termExplanations === 'string'
                                                    ? JSON.parse(article.termExplanations)
                                                    : article.termExplanations;

                                                if (Array.isArray(termData) && termData.length > 0) {
                                                    return termData.map((item, idx) => (
                                                        <View key={idx} className="mb-6">
                                                            <Text className="text-[14px] font-semibold text-[#014029] mb-1">
                                                                • {item.term}
                                                            </Text>
                                                            <Text className="text-[14px] leading-[22px] text-black pl-3">
                                                                {item.explanation}
                                                            </Text>
                                                        </View>
                                                    ));
                                                } else {
                                                    return <Text className="text-[#6D6D6D]">키워드가 없습니다.</Text>;
                                                }
                                            } catch (e) {
                                                console.error('키워드 파싱 오류:', e);
                                                return <Text className="text-[#6D6D6D]">키워드 데이터 형식 오류</Text>;
                                            }
                                        })()
                                    ) || (
                                            <Text className="text-[#6D6D6D]">키워드가 없습니다.</Text>
                                        )}
                                </View>
                            )}
                        </View>
                    </View>

                    {/* 기사 원문 보러 가기 */}
                    <View className="items-center mt-6">
                        <TouchableOpacity
                            onPress={() => Linking.openURL(article.sourceUrl)}
                            className="bg-[#014029] rounded-[20px] w-[175px] h-[43px] shadow-sm justify-center"
                        >
                            <Text className="text-[14px] font-semibold text-[#F9F9F9] text-center">
                                기사 원문 보러 가기
                            </Text>
                        </TouchableOpacity>
                    </View>

                    {/* 메인으로 돌아가기 */}
                    <TouchableOpacity
                        onPress={() => navigation.navigate('Home')}
                        className="items-center mt-4"
                    >
                        <Text className="text-[10px] text-[#6D6D6D]">메인으로 돌아가기</Text>
                    </TouchableOpacity>
                </ScrollView>
            </SafeAreaView>
        </View>
    )
}
