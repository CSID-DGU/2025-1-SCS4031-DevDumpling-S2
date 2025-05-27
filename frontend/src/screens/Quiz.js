import { View, Text, TouchableOpacity, ScrollView, Image, Alert, ActivityIndicator, StyleSheet } from 'react-native';
import { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { useLoading } from '../hooks/useLoading';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Header from '../components/layout/Header';
import DumplingLoading from '../components/common/DumplingLoading';

const API_BASE_URL = 'http://52.78.59.11:8080';

export default function Quiz() {
    // 상태 관리
    const [quiz, setQuiz] = useState(null);
    const [loading, setLoading] = useState(true);
    const [normalLoading, setNormalLoading] = useState(false); // 일반 로딩 상태
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

    const fetchRandomQuiz = async () => {
        try {
            setLoading(true);

            const userDataStr = await AsyncStorage.getItem('userData');
            const userData = JSON.parse(userDataStr);

            const userType = userData.userType;

            await new Promise(resolve => setTimeout(resolve, 2000));

            const response = await axios.get(`${API_BASE_URL}/api/quizzes/user-type/${userType}`);

            if (response.data && response.data.length > 0) {
                const randomIndex = Math.floor(Math.random() * response.data.length);
                const quizData = response.data[randomIndex];
                console.log('백엔드에서 받은 퀴즈 데이터:', quizData);
                console.log('정답 데이터(answer):', quizData.answer);
                console.log('정답 데이터 타입:', typeof quizData.answer);
                setQuiz(quizData);
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

                // 정답 확인 - 백엔드에서 정답은 옵션 번호로 시작하는 텍스트 형태 ("2.특정 종목이나...")
                // 시작 문자를 확인해서 몇 번 선택지인지 파악

                // 옵션 번호 추출 ("2.특정 종목..." 에서 2 배출)
                const correctOptionNumber = quiz.answer && quiz.answer.match(/^(\d+)\./) ? quiz.answer.match(/^(\d+)\./)[1] : null;

                // 사용자가 선택한 옵션 번호와 정답 옵션 번호 비교
                const isAnswerCorrect = correctOptionNumber && answer === correctOptionNumber;

                console.log('정답에서 추출한 옵션 번호:', correctOptionNumber);
                console.log('사용자가 선택한 옵션 번호:', answer);
                console.log('백엔드 정답 전체:', quiz.answer);
                console.log('정답 여부:', isAnswerCorrect);

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

                // 서버 응답 대신 클라이언트에서 계산한 결과 사용
                setIsCorrect(isAnswerCorrect);
                setSubmitted(true);

                console.log('quiz.answer:', quiz.answer);
                console.log('user selected:', answer);
                console.log('isCorrect:', isAnswerCorrect);
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
    const loadNextQuiz = async () => {
        setSelectedAnswer(null);
        setSubmitted(false);
        setIsCorrect(false);
        setNormalLoading(true); // 일반 로딩 상태 활성화

        try {
            // AsyncStorage에서 사용자 정보 가져오기
            const userDataStr = await AsyncStorage.getItem('userData');
            const userData = JSON.parse(userDataStr);

            // 사용자 유형 가져오기
            const userType = userData.userType;

            // 인위적인 지연 추가 (1초)
            await new Promise(resolve => setTimeout(resolve, 1000));

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
            setNormalLoading(false);
        }
    };

    const goToHome = () => {
        navigation.navigate('Home');
    };

    // 4지선다 퀴즈 UI 렌더링
    const renderQuiz = () => (
        <>
            {/* 문제 부분 */}
            <View className="bg-white rounded-[20px] p-6 mb-4 mx-6">
                <Text className="text-Fineed-green font-bold text-xl text-center">금융 기사 퀴즈</Text>
                <Text className="text-gray-500 text-center mb-6">4가지 답 중 정답을 골라주세요</Text>

                <Text className="text-base font-medium text-center mb-2">
                    {quiz.question || "문제 내용"}
                </Text>

                <Text className="text-sm text-gray-500 text-center mt-4">
                    이 문제를 맞추면 포인트를 받아요!
                </Text>

            </View>

            <View className="space-y-3 mx-6">
                {options.map((option, index) => {
                    const optionNumber = index + 1;
                    const isCorrectOption = String(optionNumber) === String(quiz.answer.match(/^(\d+)\./)?.[1]);
                    const isSelected = selectedAnswer === String(optionNumber);

                    // 배경색 결정
                    let backgroundColor = "white";
                    if (submitted) {
                        if (isCorrectOption) {
                            // 정답은 초록
                            backgroundColor = "#014029";
                        } else if (isSelected) {
                            // 틀린 선택지만 회색
                            backgroundColor = "#6B7280";
                        }
                    } else if (isSelected) {
                        // 제출 전 선택된 답은 초록 (원래대로)
                        backgroundColor = "#014029";
                    }

                    // 글자 색 결정 (흰 배경일 땐 검정, 초록/회색일 땐 흰색)
                    const textColor = (backgroundColor === "white") ? "black" : "white";

                    return (
                        <TouchableOpacity
                            key={index}
                            style={{
                                marginTop: 20,
                                padding: 16,
                                borderRadius: 30,
                                backgroundColor
                            }}
                            onPress={() => !submitted && submitAnswer(String(optionNumber))}
                            disabled={submitted}
                        >
                            <View style={{ flexDirection: 'row' }}>
                                <Text style={{ fontWeight: '500', color: textColor }}>
                                    {`${optionNumber}. `}
                                </Text>
                                <Text style={{ fontWeight: '500', flex: 1, color: textColor }}>
                                    {option}
                                </Text>
                            </View>
                        </TouchableOpacity>
                    );
                })}

            </View>

            {/* 정답/오답 피드백 */}
            {submitted && (
                <View className="bg-gray-200 rounded-[15px] p-6 my-4 mx-6">
                    <View className="items-center justify-center mb-2">
                        <Text className="text-xl font-bold text-center">
                            {isCorrect ? '정답입니다!' : '오답입니다...'}
                        </Text>
                        <Text className="text-center text-gray-600 mt-2">
                            {quiz.explanation || ""}
                        </Text>
                    </View>

                    <View className="items-center justify-center mt-4">
                        <TouchableOpacity
                            className="bg-Fineed-green py-3 px-6 rounded-[15px] items-center justify-center w-full"
                            onPress={loadNextQuiz}
                            disabled={normalLoading}
                        >
                            {normalLoading ? (
                                <ActivityIndicator color="white" size="small" />
                            ) : (
                                <Text className="text-white font-medium">다음 문제</Text>
                            )}
                        </TouchableOpacity>
                    </View>
                </View>
            )}

            <TouchableOpacity
                className="py-2 items-center justify-center mt-6"
                onPress={goToHome}
            >
                <Text className="text-gray-600">메인으로 돌아가기</Text>
            </TouchableOpacity>
        </>
    );


    const renderLoading = () => (
        <DumplingLoading
            message="오늘의 기사는 확인해보셨나요? 함께 퀴즈 풀러 가봐요!"
            onLoadingComplete={() => { }}
        />
    );

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

    const renderContent = () => {
        if (loading) return renderLoading();
        if (!quiz) return renderError();
        return renderQuiz();
    };

    return (
        <>
            <Header />
            {loading ? (
                <DumplingLoading
                    message={["오늘의 기사는 확인해보셨나요?", "함께 퀴즈 풀러 가봐요!"]}
                    onLoadingComplete={() => { }}
                />
            ) : (
                <SafeAreaView className="flex-1 bg-Fineed-background">
                    <ScrollView className="flex-1 px-4">
                        {!quiz ? renderError() : renderQuiz()}
                    </ScrollView>
                </SafeAreaView>
            )}

            {normalLoading && (
                <View style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    justifyContent: 'center',
                    alignItems: 'center',
                    backgroundColor: 'rgba(0,0,0,0.5)',
                    zIndex: 1000
                }}>
                    <ActivityIndicator size="large" color="#014029" />
                </View>
            )}
        </>
    );
}