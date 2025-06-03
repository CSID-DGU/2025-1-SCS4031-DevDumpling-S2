import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { format } from 'date-fns';

const API_BASE_URL = 'http://52.78.59.11:8080';

const QuizHistory = () => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const navigation = useNavigation();
    const [userData, setUserData] = useState(null);
    const [quizHistory, setQuizHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState({ totalAnswers: 0, correctAnswers: 0, correctRate: 0 });

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const storedUserData = await AsyncStorage.getItem('userData');
                if (storedUserData) {
                    setUserData(JSON.parse(storedUserData));
                }
            } catch (error) {
                console.error('사용자 데이터 불러오기 실패:', error);
            }
        };
        fetchUserData();
    }, []);

    useEffect(() => {
        const fetchQuizHistory = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                console.log('userToken: ', token);
                if (!token) return;
                const response = await axios.get(`${API_BASE_URL}/api/quiz-results/results`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                console.log('퀴즈 기록 응답: ', response.data);
                setQuizHistory(response.data);
            } catch (error) {
                console.error('퀴즈 기록 불러오기 실패:', error);
                if (error.response) {
                    console.error('에러 상태: ', error.response.status);
                    console.error('에러 메시지: ', error.response.data);
                }
                setQuizHistory([]);
            } finally {
                setLoading(false);
            }
        };
        fetchQuizHistory();
    }, []);

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const token = await AsyncStorage.getItem('userToken');
                if (!token) return;
                const response = await axios.get(`${API_BASE_URL}/api/quiz-results/stats`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setStats(response.data);
            } catch (error) {
                console.error('퀴즈 통계 불러오기 실패:', error);
                setStats({ totalAnswers: 0, correctAnswers: 0, correctRate: 0 });
            }
        };
        fetchStats();
    }, []);

    // 보기 번호를 실제 텍스트로 변환
    const getOptionText = (quiz, answerNum) => {
        if (!quiz || !quiz.options) return '';
        
        // answerNum이 문자열일 수 있으니 숫자로 바꿔야 함
        const idx = parseInt(answerNum, 10) - 1;
    
        if (Array.isArray(quiz.options)) {
            return quiz.options[idx] || `보기 ${answerNum}`;
        }
    
        try {
            const optionsObj = typeof quiz.options === 'string' ? JSON.parse(quiz.options) : quiz.options;
            return optionsObj[`option${answerNum}`] || `보기 ${answerNum}`;
        } catch {
            return `보기 ${answerNum}`;
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
                    <Text className="text-2xl text-[#014029] font-bold mb-8">
                        {userData ? `${userData.nickname}` : '닉네임 정보 없음'}님의 퀴즈 기록
                    </Text>

                    <View className="flex-row justify-between px-10 py-4 rounded-2xl bg-white shadow-md mb-8">
                        <View className="flex-col items-center justify-center">
                            <Text className="text-sm text-black font-bold mb-2">
                                정답률
                            </Text>
                            <Text className="text-xl text-black font-bold mb-2">
                                {stats.correctRate}%
                            </Text>
                        </View>
                        <View className="flex-col items-center justify-between">
                            <Text className="text-sm text-black font-bold mb-2">
                                제출한 퀴즈 수
                            </Text>
                            <Text className="text-xl text-black font-bold mb-2">
                                {stats.totalAnswers}
                            </Text>
                        </View>
                        <View className="flex-col items-center justify-between">
                            <Text className="text-sm text-black font-bold mb-2">
                                정답 퀴즈 수
                            </Text>
                            <Text className="text-xl text-black font-bold mb-2">
                                {stats.correctAnswers}
                            </Text>
                        </View>
                    </View>

                    {loading ? (
                        <Text className="text-center text-gray-500">로딩 중...</Text>
                    ) : quizHistory.length === 0 ? (
                        <Text className="text-center text-gray-500">퀴즈 기록이 없습니다.</Text>
                    ) : (
                        quizHistory.map((item, idx) => {
                            const { quiz, selectedAnswer, isCorrect, createdAt } = item;
                            const answerText = getOptionText(quiz, quiz.answer);
                            const selectedText = getOptionText(quiz, selectedAnswer);

                            return (
                                <View
                                    key={item.id || idx}
                                    className={`bg-[#F9F9F9] p-4 rounded-2xl shadow-md p-5 mb-5`}
                                >
                                    <Text className="text-xs text-gray-500 mb-1">
                                        {format(new Date(createdAt), 'yyyy-MM-dd')}
                                    </Text>
                                    <View className="flex-row items-center justify-between gap-3">
                                        <Text className={`text-5xl font-bold mb-1 ml-2 mr-4 ${isCorrect ? 'text-Fineed-green' : 'text-red-500'}`}>
                                            {isCorrect ? 'O' : 'X'}
                                        </Text>
                                        <View className="flex-1 flex-col justify-center">
                                            <Text className="text-m text-black mb-2" style={{ flexShrink: 1 }}>
                                                Q. {quiz.question}
                                            </Text>
                                            {isCorrect ? (
                                                <Text className="text-m text-[#014029] font-bold mb-1">
                                                    A. {answerText}
                                                </Text>
                                            ) : (
                                                <>
                                                    <Text
                                                        className="text-m text-red-500 font-bold mb-1"
                                                        style={{ textDecorationLine: 'line-through' }}
                                                    >
                                                        A. {selectedText}
                                                    </Text>
                                                    <Text className="text-m text-[#014029] font-bold mb-1">
                                                        A. {answerText}
                                                    </Text>
                                                </>
                                            )}
                                        </View>
                                    </View>
                                </View>
                            );
                        })
                    )}
                </ScrollView>
            </View>
        </>
    );
};

export default QuizHistory;
