import { View, Text, TouchableOpacity, ScrollView, ActivityIndicator, Image, Alert } from 'react-native';
import { useState, useEffect } from 'react';
import axios from 'axios';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { useLoading } from '../hooks/useLoading';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function Quiz() {
    const [quiz, setQuiz] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedAnswer, setSelectedAnswer] = useState(null);
    const [submitted, setSubmitted] = useState(false);
    const [isCorrect, setIsCorrect] = useState(false);
    const navigation = useNavigation();
    const { showLoading, hideLoading } = useLoading();

    useEffect(() => {
        fetchRandomQuiz();
    }, []);

    // 랜덤 퀴즈 가져오기 (사용자 유형에 맞는)
    const fetchRandomQuiz = async () => {
        try {
            setLoading(true);

            // AsyncStorage에서 사용자 정보 가져오기
            const userDataStr = await AsyncStorage.getItem('userData');
            const userData = JSON.parse(userDataStr);

            // 사용자 유형 가져오기
            const userType = userData.userType;
            console.log('현재 사용자 유형:', userType);

            const response = await axios.get(`${API_BASE_URL}/api/quizzes/user-type/${userType}`);

            if (response.data && response.data.length > 0) {
                // 랜덤으로 퀴즈 선택
                const randomIndex = Math.floor(Math.random() * response.data.length);
                setQuiz(response.data[randomIndex]);
            } else {
                Alert.alert('알림', '사용 가능한 퀴즈가 없습니다.');
            }
        } catch (error) {
            console.error('퀴즈를 불러오는데 실패했습니다:', error);
            Alert.alert('오류', '퀴즈를 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    // 퀴즈 답변 제출
    const submitAnswer = async (answer) => {
        setSelectedAnswer(answer);

        if (quiz) {
            try {
                showLoading();

                // AsyncStorage에서 토큰 가져오기
                const token = await AsyncStorage.getItem('userToken');

                console.log('토큰 확인:', token);
                console.log('퀴즈 ID:', quiz.id);
                console.log('선택한 답변:', answer);

                // 선택한 답을 서버에 제출 (API 문서에 맞게 형식 변경)
                const response = await axios.post(
                    `${API_BASE_URL}/api/quiz-results/${quiz.id}/submit`,
                    {
                        quizId: quiz.id.toString(),
                        selectedAnswer: answer
                    },
                    {
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        }
                    }
                );

                console.log('응답 결과:', response.data);

                setIsCorrect(response.data.isCorrect);
                setSubmitted(true);
            } catch (error) {
                console.error('답변 제출에 실패했습니다:', error);
                if (error.response) {
                    console.error('에러 상태:', error.response.status);
                    console.error('에러 데이터:', error.response.data);
                }
                Alert.alert('오류', '답변 제출에 실패했습니다.');
            } finally {
                hideLoading();
            }
        }
    };

    // 다음 퀴즈 가져오기
    const loadNextQuiz = () => {
        setSelectedAnswer(null);
        setSubmitted(false);
        setIsCorrect(false);
        fetchRandomQuiz();
    };

    // 메인 화면으로 돌아가기
    const goToHome = () => {
        navigation.navigate('Home');
    };

    // OX 퀴즈 렌더링
    const renderOXQuiz = () => {
        return (
            <View className="p-6 bg-white rounded-[15px] my-4">
                <Text className="text-Fineed-green font-bold text-xl text-center">투자 퀴즈</Text>
                <Text className="text-gray-500 text-center mb-4">O/X를 골라주세요</Text>

                <Text className="text-base font-medium mb-4 text-center">
                    {quiz.question}
                </Text>

                <Text className="text-sm text-gray-500 mb-6 text-center">
                    이 문제를 맞추시면 포인트를 드려요!
                </Text>

                <View className="flex-row justify-between mt-2">
                    <TouchableOpacity
                        className={`flex-1 mr-2 py-3 rounded-[15px] items-center justify-center ${selectedAnswer === 'O' ? 'bg-Fineed-green' : 'bg-white border border-Fineed-green'}`}
                        onPress={() => !submitted && submitAnswer('O')}
                        disabled={submitted}
                    >
                        <Text className={`text-xl font-bold ${selectedAnswer === 'O' ? 'text-white' : 'text-Fineed-green'}`}>O</Text>
                    </TouchableOpacity>

                    <TouchableOpacity
                        className={`flex-1 ml-2 py-3 rounded-[15px] items-center justify-center ${selectedAnswer === 'X' ? 'bg-Fineed-green' : 'bg-white border border-Fineed-green'}`}
                        onPress={() => !submitted && submitAnswer('X')}
                        disabled={submitted}
                    >
                        <Text className={`text-xl font-bold ${selectedAnswer === 'X' ? 'text-white' : 'text-Fineed-green'}`}>X</Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    };

    // 4지선다 퀴즈 렌더링
    const renderMultipleChoiceQuiz = () => {
        let options = [];

        try {
            // 백엔드에서 온 options가 JSON 문자열인지 확인
            if (quiz.options && quiz.options.startsWith('[') && quiz.options.endsWith(']')) {
                // JSON 형식이면 파싱
                options = JSON.parse(quiz.options || '[]');
            } else if (quiz.options) {
                // 일반 문자열이면 세미콜론이나 쉼표로 분리
                if (quiz.options.includes(';')) {
                    options = quiz.options.split(';');
                } else if (quiz.options.includes(',')) {
                    options = quiz.options.split(',');
                } else {
                    // 구분자가 없으면 그냥 단일 옵션으로 처리
                    options = [quiz.options];
                }
            }

            // 옵션이 없으면 빈 배열 기본값
            if (!options || options.length === 0) {
                options = ['선택지1', '선택지2', '선택지3', '선택지4'];
            }

            console.log('파싱된 옵션:', options);
        } catch (e) {
            console.error('옵션 파싱 오류:', e);
            // 에러 발생 시 기본 옵션 제공
            options = ['선택지1', '선택지2', '선택지3', '선택지4'];
        }

        return (
            <View className="p-6 bg-white rounded-[15px] my-4">
                <Text className="text-Fineed-green font-bold text-xl text-center">분야명 퀴즈</Text>
                <Text className="text-gray-500 text-center mb-4">4가지 답 중 정답을 골라주세요</Text>

                <Text className="text-base font-medium mb-6 text-center">
                    {quiz.question}
                </Text>

                <View className="space-y-3">
                    {options.map((option, index) => (
                        <TouchableOpacity
                            key={index}
                            className={`py-3 rounded-[15px] items-center justify-center border ${selectedAnswer === String(index + 1) ? 'bg-Fineed-green border-Fineed-green' : 'bg-white border-Fineed-green'}`}
                            onPress={() => !submitted && submitAnswer(String(index + 1))}
                            disabled={submitted}
                        >
                            <Text className={`text-base font-medium ${selectedAnswer === String(index + 1) ? 'text-white' : 'text-black'}`}>
                                {option}
                            </Text>
                        </TouchableOpacity>
                    ))}
                </View>
            </View>
        );
    };

    // 결과 피드백 렌더링
    const renderFeedback = () => {
        return (
            <View className="p-6 bg-gray-200 rounded-[15px] my-4">
                <View className="flex-row justify-center mb-2">

                </View>

                <Text className="text-xl font-bold text-center mb-2">
                    {isCorrect ? '정답입니다!' : '오답입니다...'}
                </Text>

                <Text className="text-base text-center mb-4">
                    {quiz.explanation}
                </Text>

                <TouchableOpacity
                    className="bg-Fineed-green py-3 rounded-[15px] items-center justify-center mt-2"
                    onPress={loadNextQuiz}
                >
                    <Text className="text-white font-medium">다음 문제</Text>
                </TouchableOpacity>

                <TouchableOpacity
                    className="py-2 items-center justify-center mt-2"
                    onPress={goToHome}
                >
                    <Text className="text-gray-600">메인으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        );
    };

    return (
        <SafeAreaView className="flex-1 bg-Fineed-background">
            <ScrollView className="flex-1 px-4">
                {loading ? (
                    <View className="flex-1 justify-center items-center p-10">
                        <ActivityIndicator size="large" color="#014029" />
                        <Text className="mt-4 text-gray-600">퀴즈를 불러오는 중...</Text>
                    </View>
                ) : quiz ? (
                    <>
                        {submitted ? (
                            renderFeedback()
                        ) : (
                            quiz.quizType === 'OX' ? renderOXQuiz() : renderMultipleChoiceQuiz()
                        )}
                    </>
                ) : (
                    <View className="flex-1 justify-center items-center p-10">
                        <Text className="text-gray-600">퀴즈를 불러올 수 없습니다.</Text>
                        <TouchableOpacity
                            className="bg-Fineed-green py-3 px-6 rounded-[15px] items-center justify-center mt-4"
                            onPress={fetchRandomQuiz}
                        >
                            <Text className="text-white font-medium">다시 시도</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </ScrollView>
        </SafeAreaView>
    );
}