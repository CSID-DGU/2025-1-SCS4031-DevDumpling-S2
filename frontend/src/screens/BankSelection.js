import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    SafeAreaView,
    TouchableOpacity,
    FlatList,
    ActivityIndicator,
    Image,
} from 'react-native';
import Header from '../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://52.78.59.11:8080';

// 보험용 기본 아이콘 URL (프로젝트에 맞춰 실제 주소로 교체하세요)
const DEFAULT_INSURANCE_ICON = 'https://myapp-logos.s3.amazonaws.com/bank-logos/default-insurance.png';

const UserAccountList = () => {
    const navigation = useNavigation();
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('bank'); // 'bank', 'loan', 'investment', 'card', 'insurance'
    const [selectedAccounts, setSelectedAccounts] = useState({});
    const [imageErrors, setImageErrors] = useState({});

    useEffect(() => {
        fetchData();
    }, [activeTab]);

    // 각 탭별 아이템 고유 ID 반환
    const getItemId = (item) => {
        switch (activeTab) {
            case 'bank':
                return item.accountNumber || item.id || `${item.bankName}-${Math.random()}`;
            case 'loan':
                return item.loanNumber || item.id || `${item.bankName}-${Math.random()}`;
            case 'investment':
                return item.accountNumber || item.id || `${item.accountName}-${Math.random()}`;
            case 'card':
                return item.cardId || item.id || `${item.cardName}-${Math.random()}`;
            case 'insurance':
                return item.insuranceId || item.id || `${item.productName}-${Math.random()}`;
            default:
                return item.id?.toString() || `${Math.random()}`;
        }
    };

    // 탭이 바뀔 때마다 해당 API에서 데이터를 불러오는 함수
    const fetchData = async () => {
        setLoading(true);
        try {
            const token = await AsyncStorage.getItem('userToken');
            const headers = token ? { Authorization: `Bearer ${token}` } : {};

            let apiEndpoint = '';
            switch (activeTab) {
                case 'loan':
                    apiEndpoint = '/api/dummy/loans';
                    break;
                case 'investment':
                    apiEndpoint = '/api/dummy/investments';
                    break;
                case 'card':
                    apiEndpoint = '/api/dummy/cards';
                    break;
                case 'insurance':
                    apiEndpoint = '/api/dummy/insurances';
                    break;
                default: // 'bank'
                    apiEndpoint = '/api/dummy/bank';
            }

            const response = await axios.get(
                `${API_BASE_URL}${apiEndpoint}`,
                { headers, timeout: 10000 }
            );
            const data = Array.isArray(response.data)
                ? response.data
                : response.data.data || [];

            console.log('받아온 데이터 확인:', JSON.stringify(data, null, 2));
            setAccounts(data);
        } catch (err) {
            console.error('fetchData error:', err.code, err.config?.url, err.message);
            setAccounts([]);
            if (__DEV__) {
                alert(`데이터 로딩 중 오류가 발생했습니다: ${err.message}`);
            }
        } finally {
            setLoading(false);
        }
    };

    // 체크박스 선택/해제 토글
    const toggleSelection = (id) => {
        setSelectedAccounts(prev => ({
            ...prev,
            [id]: !prev[id],
        }));
    };

    // “동의하기” 버튼 누를 때 호출되는 함수: 선택된 ID 목록을 적절한 키 이름으로 묶어서 POST
    const handleConnect = async () => {
        // 1) 선택된 ID만 배열로 뽑기
        const selectedIds = Object.keys(selectedAccounts).filter(id => selectedAccounts[id]);
        if (selectedIds.length === 0) {
            alert('하나 이상의 항목을 선택해주세요.');
            return;
        }

        const token = await AsyncStorage.getItem('userToken');
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        let consentEndpoint = '';
        let requestBody = {};

        switch (activeTab) {
            case 'bank': {
                // 은행 탭: 계좌번호 배열 → selectedAccountNumbers
                consentEndpoint = '/api/dummy/bank/consent/add';
                requestBody = { selectedAccountNumbers: selectedIds };
                break;
            }
            case 'loan': {
                // 대출 탭: 대출번호 배열 → selectedLoanNumbers
                consentEndpoint = '/api/dummy/loans/consent/add';
                requestBody = { selectedLoanNumbers: selectedIds };
                break;
            }
            case 'investment': {
                // 투자 탭: 투자상품 ID 배열 → selectedInvestmentIds
                consentEndpoint = '/api/dummy/investments/consent/add';
                requestBody = { selectedInvestmentIds: selectedIds };
                break;
            }
            case 'card': {
                // 카드 탭: 카드번호 배열 → selectedCardNumbers
                consentEndpoint = '/api/dummy/card/consent/add';
                requestBody = { selectedCardNumbers: selectedIds };
                break;
            }
            case 'insurance': {
                // 보험 탭: 보험상품 ID 배열 → selectedInsuranceIds
                consentEndpoint = '/api/dummy/insurances/consent/add';
                requestBody = { selectedInsuranceIds: selectedIds };
                break;
            }
            default:
                // 만약 탭이 정의되지 않은 값이면 은행으로 간주
                consentEndpoint = '/api/dummy/bank/consent/add';
                requestBody = { selectedAccountNumbers: selectedIds };
        }

        console.log('보낼 요청:', requestBody);
        console.log('요청 URL:', `${API_BASE_URL}${consentEndpoint}`);

        try {
            await axios.post(
                `${API_BASE_URL}${consentEndpoint}`,
                requestBody,
                { headers }
            );
            // 성공 시 MyDataComplete 화면으로 리셋 이동
            navigation.reset({
                index: 0,
                routes: [{ name: 'MyDataComplete' }],
            });
        } catch (error) {
            console.error('자산 연결 실패:', error.response?.data || error.message);
            alert(`자산 연결에 실패했습니다.\n${error.response?.data?.message || error.message}`);
        }
    };

    // 이미지 로드 실패 시 ID를 기준으로 표시할 placeholder를 활성화
    const handleImageError = (id) => {
        setImageErrors(prev => ({
            ...prev,
            [id]: true,
        }));
    };

    // 실제 리스트 항목을 렌더링하는 컴포넌트
    const renderAccountItem = ({ item }) => {
        const id = getItemId(item);
        const isSelected = selectedAccounts[id] || false;
        const hasImageError = imageErrors[id] || false;

        // ── 탭별 아이템 이름을 가져오는 함수 ──
        const getItemName = () => {
            switch (activeTab) {
                case 'bank':
                case 'loan':
                    return item.bankName || '-';
                case 'investment':
                    return item.accountName || '-';
                case 'card':
                    return item.cardName || '-';
                case 'insurance':
                    return item.productName || '-';
                default:
                    return '-';
            }
        };
        // ──────────────────────────────────────

        // ── 탭별 부가 정보를 가져오는 함수 ──
        const getItemSubInfo = () => {
            switch (activeTab) {
                case 'loan':
                    return item.productName || '';
                case 'investment':
                    return item.investmentType || '';
                case 'card':
                    return item.cardType || '';
                case 'insurance':
                    return item.insuranceType || '';
                default:
                    return '';
            }
        };
        // ──────────────────────────────────────

        // ── 이름이 없을 때 보여줄 최초 문자(placeholder) ──
        const getItemInitial = () => {
            const name = getItemName();
            return name ? name[0] : '-';
        };
        // ──────────────────────────────────────

        // ── 탭별 이미지 URL을 결정하는 함수 ──
        const getImageUrl = () => {
            // 1) rawName (“주식회사 ” 접두어 제거용)
            const rawName = getItemName();
            if (!rawName || rawName === '-') return '';

            // 2) “주식회사 ” 접두어 제거 (대출/은행 이름이 긴 경우가 많음)
            const cleaned = rawName.replace(/^주식회사\s*/, '');

            // 3) 은행 탭: item.bankImage 그대로 사용
            if (activeTab === 'bank') {
                const url = item.bankImage || '';
                return url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
            }
            // 4) 대출 탭: item.bankImage 그대로 사용
            if (activeTab === 'loan') {
                const url = item.bankImage || '';
                return url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
            }
            // 5) 투자 탭: item.bankImage 그대로 사용
            if (activeTab === 'investment') {
                const url = item.bankImage || '';
                return url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
            }
            // 6) 카드 탭: item.cardImage 그대로 사용
            if (activeTab === 'card') {
                const url = item.cardImage || '';
                return url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
            }
            // 7) 보험 탭: 기본 보험 아이콘 사용
            if (activeTab === 'insurance') {
                return DEFAULT_INSURANCE_ICON;
            }
            return '';
        };
        // ──────────────────────────────────────

        const imageUrl = getImageUrl();

        return (
            <TouchableOpacity
                className="flex-row items-center p-3"
                onPress={() => toggleSelection(id)}
            >
                {imageUrl && !hasImageError ? (
                    <Image
                        source={{ uri: imageUrl }}
                        className="w-8 h-8 rounded-full"
                        style={{ backgroundColor: '#f0f0f0', borderWidth: 1, borderColor: '#ddd' }}
                        resizeMode="cover"
                        onError={() => {
                            console.log(`[ERROR] 이미지 로드 실패: ${imageUrl}`);
                            handleImageError(id);
                        }}
                    />
                ) : (
                    <View className="w-8 h-8 rounded-full bg-gray-200 items-center justify-center">
                        <Text className="text-gray-500 font-bold text-sm">{getItemInitial()}</Text>
                    </View>
                )}

                {/* 이름 및 부가 정보 */}
                <View className="flex-1 ml-3">
                    <Text className="font-medium text-base">{getItemName()}</Text>
                    {getItemSubInfo() ? (
                        <Text className="text-sm text-gray-500">{getItemSubInfo()}</Text>
                    ) : null}
                </View>

                {/* 선택 체크박스 */}
                <View className={`w-6 h-6 rounded-full items-center justify-center ${isSelected ? 'bg-Fineed-green' : 'border border-gray-300'}`}>
                    <Text className={`text-xs font-bold ${isSelected ? 'text-white' : 'text-gray-300'}`}>✓</Text>
                </View>
            </TouchableOpacity>
        );
    };

    // 로딩 중일 때
    if (loading) {
        return (
            <>
                <Header />
                <SafeAreaView className="flex-1 justify-center items-center bg-white">
                    <ActivityIndicator size="large" color="#2A9D8F" className="mb-2" />
                    <Text>데이터를 불러오는 중입니다...</Text>
                </SafeAreaView>
            </>
        );
    }

    // 탭 렌더링
    const renderTabs = () => (
        <View className="flex-row mb-5 justify-evenly">
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'bank' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('bank')}
            >
                <Text className={`${activeTab === 'bank' ? 'text-white' : 'text-gray-600'} font-medium`}>은행</Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'loan' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('loan')}
            >
                <Text className={`${activeTab === 'loan' ? 'text-white' : 'text-gray-600'} font-medium`}>대출</Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'investment' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('investment')}
            >
                <Text className={`${activeTab === 'investment' ? 'text-white' : 'text-gray-600'} font-medium`}>투자</Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'card' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('card')}
            >
                <Text className={`${activeTab === 'card' ? 'text-white' : 'text-gray-600'} font-medium`}>카드</Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full ${activeTab === 'insurance' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('insurance')}
            >
                <Text className={`${activeTab === 'insurance' ? 'text-white' : 'text-gray-600'} font-medium`}>보험</Text>
            </TouchableOpacity>
        </View>
    );

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-Fineed-background">
                <View className="px-10 pt-10 pb-2">
                    <Text className="text-2xl font-bold mb-2">FINEED와</Text>
                    <Text className="text-2xl font-bold mb-6">연결할 자산을 선택해주세요!</Text>
                    {renderTabs()}
                </View>

                <View className="mx-10 mb-5 bg-white rounded-3xl shadow-md overflow-hidden">
                    {accounts.length > 0 ? (
                        <FlatList
                            data={accounts}
                            renderItem={renderAccountItem}
                            keyExtractor={(item) => getItemId(item)}
                            contentContainerStyle={{ paddingVertical: 4 }}
                        />
                    ) : (
                        <View className="py-10 items-center justify-center">
                            <Text className="text-lg text-gray-500 text-center">
                                {activeTab === 'bank'
                                    ? '연결할 수 있는 은행이 없습니다.'
                                    : activeTab === 'loan'
                                        ? '연결할 수 있는 대출 상품이 없습니다.'
                                        : activeTab === 'investment'
                                            ? '연결할 수 있는 투자 상품이 없습니다.'
                                            : activeTab === 'card'
                                                ? '연결할 수 있는 카드가 없습니다.'
                                                : '연결할 수 있는 보험이 없습니다.'}
                            </Text>
                        </View>
                    )}
                </View>

                <View className="absolute bottom-0 left-0 right-0 p-5">
                    {(() => {
                        const hasSelected = Object.values(selectedAccounts).some(v => v);
                        return (
                            <TouchableOpacity
                                className={`py-3.5 rounded-full items-center justify-center ${hasSelected ? 'bg-Fineed-green' : 'bg-gray-300'}`}
                                onPress={handleConnect}
                                disabled={!hasSelected}
                            >
                                <Text className="text-white text-center font-bold text-base">연결하기</Text>
                            </TouchableOpacity>
                        );
                    })()}
                </View>
            </SafeAreaView>
        </>
    );
};

export default UserAccountList;
