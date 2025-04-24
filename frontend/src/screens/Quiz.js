import { View, Text, TouchableOpacity, ScrollView, ActivityIndicator, Image, Alert } from 'react-native';
import { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { useLoading } from '../hooks/useLoading';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Header from '../components/layout/Header';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function Quiz() {
    // 상태 관리
    const [quiz, setQuiz] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedAnswer, setSelectedAnswer] = useState(null);
    const [submitted, setSubmitted] = useState(false);
    const [isCorrect, setIsCorrect] = useState(false);
    const [options, setOptions] = useState([]);

    const navigation = useNavigation();
    const { showLoading, hideLoading } = useLoading();

    // 컴포넌트 마운트 시 퀴즈 로드
    useEffect(() => {
        fetchRandomQuiz();
    }, []);

    // 퀴즈가 변경될 때마다 옵션 파싱
    useEffect(() => {
        if (quiz && quiz.options) {
            parseQuizOptions();
        }
    }, [quiz]);

    // 옵션 파싱 함수
    const parseQuizOptions = () => {
        try {
            // 백엔드에서 전달되는 형식: {"option1":"내용1","option2":"내용2","option3":"내용3","option4":"내용4"}
            const optionsObj = JSON.parse(quiz.options);

            // option1~4 값만 추출하여 배열로 만들기
            setOptions([
                optionsObj.option1,
                optionsObj.option2,
                optionsObj.option3,
                optionsObj.option4
            ]);
        } catch (e) {
            console.error('옵션 파싱 오류:', e);
            // 에러 발생 시 기본 옵션 제공
            setOptions(['선택지1', '선택지2', '선택지3', '선택지4']);
        }
    };

    // 랜덤 퀴즈 가져오기 (사용자 유형에 맞는)
    const fetchRandomQuiz = async () => {
        try {
            setLoading(true);

            // AsyncStorage에서 사용자 정보 가져오기
            const userDataStr = await AsyncStorage.getItem('userData');
            const userData = JSON.parse(userDataStr);

            // 사용자 유형 가져오기
            const userType = userData.userType;

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

                // 선택한 답을 서버에 제출
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

    // 4지선다 퀴즈 UI 렌더링
    const renderQuiz = () => (
        <>
            {/* 문제 부분 */}
            <View className="bg-white rounded-[20px] p-6 mb-4 mx-6">
                <Text className="text-Fineed-green font-bold text-xl text-center">분야명 퀴즈</Text>
                <Text className="text-gray-500 text-center mb-6">4가지 답 중 정답을 골라주세요</Text>

                <Text className="text-base font-medium text-center mb-2">
                    {quiz.question || "문제 내용"}
                </Text>

                <Text className="text-sm text-gray-500 text-center mt-4">
                    이 문제를 맞추면 포인트를 받아요!
                </Text>
            </View>

            {/* 선택지 부분 - 별도 컨테이너로 분리 */}
            <View className="space-y-3 mx-6">
                {options.map((option, index) => (
                    <TouchableOpacity
                        key={index}
                        className={`mt-5 bg-white py-4 px-6 rounded-[30px] text-xl ${selectedAnswer === String(index + 1) ? 'bg-Fineed-green' : ''}`}
                        onPress={() => !submitted && submitAnswer(String(index + 1))}
                        disabled={submitted}
                    >
                        <View className="flex-row">
                            <Text className={`text-base font-medium ${selectedAnswer === String(index + 1) ? 'text-white' : 'text-black'}`}>
                                {`${index + 1}. `}
                            </Text>
                            <Text className={`text-base font-medium flex-1 ${selectedAnswer === String(index + 1) ? 'text-white' : 'text-black'}`}>
                                {option}
                            </Text>
                        </View>
                    </TouchableOpacity>
                ))}
            </View>

            <TouchableOpacity
                className="py-2 items-center justify-center mt-6"
                onPress={goToHome}
            >
                <Text className="text-gray-600">메인으로 돌아가기</Text>
            </TouchableOpacity>
        </>
    );

    // 퀴즈 결과 피드백 UI 렌더링
    const renderFeedback = () => (
        <View className="p-6 bg-gray-200 rounded-[15px] my-4">
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

    // 로딩 UI 렌더링
    const renderLoading = () => (
        <View className="flex-1 justify-center items-center p-10">
            <ActivityIndicator size="large" color="#014029" />
            <Text className="mt-4 text-gray-600">퀴즈를 불러오는 중...</Text>
        </View>
    );

    // 에러 UI 렌더링
    const renderError = () => (
        <View className="flex-1 justify-center items-center p-10">
            <Text className="text-gray-600">퀴즈를 불러올 수 없습니다.</Text>
            <TouchableOpacity
                className="bg-Fineed-green py-3 px-6 rounded-[15px] items-center justify-center mt-4"
                onPress={fetchRandomQuiz}
            >
                <Text className="text-white font-medium">다시 시도</Text>
            </TouchableOpacity>
        </View>
    );

    // 메인 컨텐츠 결정
    const renderContent = () => {
        if (loading) return renderLoading();
        if (!quiz) return renderError();
        return submitted ? renderFeedback() : renderQuiz();
    };

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-Fineed-background">
                <ScrollView className="flex-1 px-4">
                    {renderContent()}
                </ScrollView>
            </SafeAreaView>
        </>
    );
}