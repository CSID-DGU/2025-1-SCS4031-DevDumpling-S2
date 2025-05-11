import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    SafeAreaView,
    TouchableOpacity,
    FlatList,
    ActivityIndicator,
    Image,
    ScrollView,
} from 'react-native';
import Header from '../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from 'axios';

const API_BASE_URL = 'http://52.78.59.11:8080';

const UserAccountList = () => {
    const navigation = useNavigation();
    const [accounts, setAccounts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('bank');
    const [selectedAccounts, setSelectedAccounts] = useState({});
    const [imageErrors, setImageErrors] = useState({}); // 이미지 에러 상태를 객체로 관리

    useEffect(() => {
        fetchData();
    }, [activeTab]);

    const fetchData = async () => {
        setLoading(true);
        try {
            const token = await AsyncStorage.getItem('userToken');
            console.log('사용 중인 토큰:', token);

            const headers = token ? { Authorization: `Bearer ${token}` } : {};

            let apiEndpoint;
            switch (activeTab) {
                case 'loan': apiEndpoint = '/api/dummy/loans'; break;
                case 'investment': apiEndpoint = '/api/dummy/investments'; break;
                default: apiEndpoint = '/api/dummy/bank';
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

    const renderTabs = () => (
        <View className="flex-row mb-5">
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'bank' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('bank')}
            >
                <Text className={`${activeTab === 'bank' ? 'text-white' : 'text-gray-600'} font-medium`}>
                    은행
                </Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full mr-2 ${activeTab === 'loan' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('loan')}
            >
                <Text className={`${activeTab === 'loan' ? 'text-white' : 'text-gray-600'} font-medium`}>
                    대출
                </Text>
            </TouchableOpacity>
            <TouchableOpacity
                className={`px-4 py-2 rounded-full ${activeTab === 'investment' ? 'bg-Fineed-green' : 'bg-white'}`}
                onPress={() => setActiveTab('investment')}
            >
                <Text className={`${activeTab === 'investment' ? 'text-white' : 'text-gray-600'} font-medium`}>
                    투자
                </Text>
            </TouchableOpacity>
        </View>
    );

    const toggleSelection = (id) => {
        setSelectedAccounts(prev => ({
            ...prev,
            [id]: !prev[id]
        }));
    };

    const handleConnect = async () => {
        const selectedIds = Object.keys(selectedAccounts).filter(id => selectedAccounts[id]);

        if (selectedIds.length === 0) {
            alert('하나 이상의 항목을 선택해주세요.');
            return;
        }

        try {
            // 선택된 자산 정보를 백엔드로 전송
            const token = await AsyncStorage.getItem('userToken');
            const headers = token ? { Authorization: `Bearer ${token}` } : {};

            await axios.post(
                `${API_BASE_URL}/api/mydata/connect`,
                { accountIds: selectedIds },
                { headers }
            );

            // 완료 화면으로 이동
            navigation.reset({
                index: 0,
                routes: [{ name: 'MyDataComplete' }]
            });
        } catch (error) {
            console.error('자산 연결 실패:', error);
            alert('자산 연결에 실패했습니다. 다시 시도해주세요.');
        }
    };

    const handleImageError = (id) => {
        setImageErrors(prev => ({
            ...prev,
            [id]: true
        }));
    };

    const renderAccountItem = ({ item }) => {
        // 각 탭별로 다른 id 필드를 사용
        let id;
        if (activeTab === 'bank') {
            id = item.accountNumber || item.id || (item.bankName + Math.random());
        } else if (activeTab === 'loan') {
            id = item.loanNumber || item.id || (item.bankName + Math.random());
        } else { // investment
            id = item.accountNumber || item.id || (item.accountName + Math.random());
        }

        const isSelected = selectedAccounts[id] || false;
        const hasImageError = imageErrors[id] || false;

        // 항목 이름 가져오기 (각 탭별로 다른 필드 사용)
        const getItemName = () => {
            if (activeTab === 'bank') {
                return item.bankName || '-';
            } else if (activeTab === 'loan') {
                return item.bankName || '-';
            } else { // investment
                return item.accountName || '-';
            }
        };

        // 항목 부가 정보 가져오기 (대출 상품명 또는 투자 유형)
        const getItemSubInfo = () => {
            if (activeTab === 'loan') {
                return item.productName || '';
            } else if (activeTab === 'investment') {
                return item.investmentType || '';
            }
            return '';
        };

        // 항목 첫 글자 가져오기 (이미지가 없을 경우 대체용)
        const getItemInitial = () => {
            const name = getItemName();
            return name ? name[0] : '-';
        };

        // 이미지 URL 가져오기
        const getImageUrl = () => {
            // bankImage 필드에서 이미지 URL을 가져옵니다
            const url = item.bankImage || '';
            console.log(`계정 ID: ${id}, 이미지 URL:`, url);
            // URL이 상대 경로인 경우 기본 URL을 붙여줍니다
            if (url && !url.startsWith('http')) {
                return `${API_BASE_URL}${url}`;
            }
            return url;
        };

        return (
            <TouchableOpacity
                className="flex-row items-center p-3"
                onPress={() => toggleSelection(id)}
            >
                {/* 은행/금융기관 아이콘 - 이미지가 있으면 이미지, 없으면 빈 원에 첫 글자 */}
                {getImageUrl() && !hasImageError ? (
                    <Image
                        source={{ uri: getImageUrl() }}
                        className="w-8 h-8 rounded-full"
                        style={{ backgroundColor: '#f0f0f0', borderWidth: 1, borderColor: '#ddd' }}
                        resizeMode="cover"
                        onError={() => {
                            console.log(`이미지 로드 오류: ${getImageUrl()}`);
                            handleImageError(id);
                        }}
                    />
                ) : (
                    <View className="w-8 h-8 rounded-full bg-gray-200 items-center justify-center">
                        <Text className="text-gray-500 font-bold text-sm">{getItemInitial()}</Text>
                    </View>
                )}

                {/* 은행/금융기관명 및 부가 정보 */}
                <View className="flex-1 ml-3">
                    <Text className="font-medium text-base">{getItemName()}</Text>
                    {getItemSubInfo() ? (
                        <Text className="text-sm text-gray-500">{getItemSubInfo()}</Text>
                    ) : null}
                </View>

                {/* 체크박스 */}
                <View className={`w-6 h-6 rounded-full items-center justify-center ${isSelected ? 'bg-Fineed-green' : 'border border-gray-300'}`}>
                    <Text className={`text-xs font-bold ${isSelected ? 'text-white' : 'text-gray-300'}`}>✓</Text>
                </View>
            </TouchableOpacity>
        );
    };

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

    return (
        <>
            <Header />
            <SafeAreaView className="flex-1 bg-Fineed-background">
                <View className="px-10 pt-10 pb-2">
                    <Text className="text-2xl font-bold mb-2">FINEED와</Text>
                    <Text className="text-2xl font-bold mb-6">
                        연결할 자산을 선택해주세요!
                    </Text>
                    {renderTabs()}
                </View>

                <View className="mx-10 mb-5 bg-white rounded-3xl shadow-md overflow-hidden">
                    {accounts.length > 0 ? (
                        <FlatList
                            data={accounts}
                            renderItem={renderAccountItem}
                            keyExtractor={(item, idx) => item.id?.toString() || item.accountNumber || idx.toString()}
                            contentContainerStyle={{ paddingVertical: 4 }}
                        />
                    ) : (
                        <View className="py-10 items-center justify-center">
                            <Text className="text-lg text-gray-500 text-center">
                                {activeTab === 'bank'
                                    ? '연결할 수 있는 은행이 없습니다.'
                                    : activeTab === 'loan'
                                        ? '연결할 수 있는 대출 상품이 없습니다.'
                                        : '연결할 수 있는 투자 상품이 없습니다.'}
                            </Text>
                        </View>
                    )}
                </View>

                <View className="absolute bottom-0 left-0 right-0 p-5">
                    {/* 하나라도 선택되었는지 확인하여 버튼 스타일 결정 */}
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
