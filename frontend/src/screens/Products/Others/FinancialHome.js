// ─────────────────────────────────────────────────────────────────────────────
// 파일명: Products/Others/FinancialHome.js
// 설명:
//  • “예·적금”, “카드”, “대출”, “보험”, “ETF”, “투자” 탭 전체 코드
//  • 예·적금/대출 탭에서 `/api/finance-images/{fin_co_no}`로 로고 URL을 받아와 표시
//  • 탭 아래에 표시되던 회색 선(테두리)을 제거했습니다.
//  • 키 중복을 방지하기 위해 “고유ID-인덱스” 형태로 key 생성
// ─────────────────────────────────────────────────────────────────────────────

import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    ScrollView,
    TouchableOpacity,
    useWindowDimensions,
    TextInput,
    ActivityIndicator,
    Image,
} from 'react-native';
import Header from '../../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import axios from 'axios';

const categories = ['예·적금', '카드', '대출', '보험', 'ETF', '투자'];
const API_BASE_URL = 'http://52.78.59.11:8080';
const DEFAULT_ICON = 'https://myapp-logos.s3.amazonaws.com/bank-logos/default.png';

// 한글 은행명을 영어로 변환하는 매핑
const bankNameMapping = {
    '우리은행': 'wooribank',
    '카카오뱅크': 'kakaobank',
    '한국스탠더드차타드은행': 'standardcharteredbank',
    '신한은행': 'shinhanbank',
    '하나은행': 'hanabank',
    'KB국민은행': 'kbbank',
    '농협은행': 'nhbank',
    'IBK기업은행': 'ibkbank',
    'SC제일은행': 'scbank',
    '새마을금고': 'saemaul',
    '신한카드': 'shinhancard',
    'KB국민카드': 'kbcard',
    '삼성카드': 'samsungcard',
    '하나카드': 'hanacard',
    '롯데카드': 'lottecard',
    '우리카드': 'wooricard',
    '삼성화재': 'samsungfire',
    '메리츠화재': 'meritzfire',
    '한화생명': 'hanwhalife',
    '삼성생명': 'samsunglife',
    '교보생명': 'kyobolife',
    'NH투자증권': 'nhinvestment',
    '삼성증권': 'samsungsecurities',
    '미래에셋대우': 'miraeasset',
    '한국투자증권': 'koreainvestment',
    '신한투자증권': 'shinhaninvestment',
    '유진투자증권': 'eugenesecurities',
    '아이엠뱅크': 'iambank',
};

const FinancialHome = ({ route, navigation }) => {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const { category } = route.params;
    const [selectedCategory, setSelectedCategory] = useState(category);

    const [searchText, setSearchText] = useState('');
    const [products, setProducts] = useState([]);      // API에서 받아온 상품 목록
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // 이미지 로드 에러 상태 관리
    const [imageErrors, setImageErrors] = useState({});

    // 오늘 날짜를 "YYYY-MM-DD" 형식으로 반환 (ETF/투자 탭에서 사용)
    const getTodayString = () => {
        const today = new Date();
        const yy = today.getFullYear();
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const dd = String(today.getDate()).padStart(2, '0');
        return `${yy}-${mm}-${dd}`;
    };

    // 카테고리가 바뀔 때마다 데이터 재로드
    useEffect(() => {
        setSelectedCategory(category);
        fetchProductsByCategory(category);
    }, [category]);

    // ─────────────────────────────────────────────────────────────────────────
    // 한글 은행명에서 영어 이름으로 변환하는 함수
    // ─────────────────────────────────────────────────────────────────────────
    const getBankEnglishName = (koreanName) => {
        if (!koreanName) return '';

        // 정확한 이름 매칭
        if (bankNameMapping[koreanName]) {
            return bankNameMapping[koreanName];
        }

        // 부분 매칭 (예: "주식회사 카카오뱅크" -> "kakaobank")
        for (const [key, value] of Object.entries(bankNameMapping)) {
            if (koreanName.includes(key)) {
                return value;
            }
        }

        // 매칭되는 것이 없으면 소문자로 변환하고 공백 제거
        return koreanName
            .toLowerCase()
            .replace(/\s+/g, '')
            .replace(/[^a-z0-9]/g, '');
    };

    // ─────────────────────────────────────────────────────────────────────────
    // 이미지 URL 생성 함수
    // ─────────────────────────────────────────────────────────────────────────
    const getImageUrl = (product) => {
        let imageUrl = '';

        if (selectedCategory === '예·적금' || selectedCategory === '대출') {
            // 예·적금, 대출의 경우
            imageUrl = product.bankImage || '';

            // 이미지가 없으면 은행명을 이용해 이미지 URL 생성
            if (!imageUrl && product.kor_co_nm) {
                const bankEnglishName = getBankEnglishName(product.kor_co_nm);
                if (bankEnglishName) {
                    return `https://myapp-logos.s3.amazonaws.com/bank-logos/${bankEnglishName}.png`;
                }
            }
        } else if (selectedCategory === '카드') {
            // 카드의 경우
            imageUrl = product.cardImage || '';

            // 이미지가 없으면 카드사명을 이용해 이미지 URL 생성
            if (!imageUrl && product.company) {
                const cardCompanyName = getBankEnglishName(product.company);
                if (cardCompanyName) {
                    return `https://myapp-logos.s3.amazonaws.com/bank-logos/${cardCompanyName}.png`;
                }
            }
        } else if (selectedCategory === '보험') {
            // 보험의 경우
            imageUrl = product.bankImage || '';

            // 이미지가 없으면 보험사명을 이용해 이미지 URL 생성
            if (!imageUrl && product.cmpyNm) {
                const insuranceCompanyName = getBankEnglishName(product.cmpyNm);
                if (insuranceCompanyName) {
                    return `https://myapp-logos.s3.amazonaws.com/bank-logos/${insuranceCompanyName}.png`;
                }
            }
        } else if (selectedCategory === 'ETF' || selectedCategory === '투자') {
            // ETF, 투자의 경우
            imageUrl = product.bankImage || '';

            // 이미지가 없으면 증권사명을 이용해 이미지 URL 생성
            if (!imageUrl) {
                let companyName = '';
                if (selectedCategory === 'ETF' && product.etf_name) {
                    companyName = product.etf_name;
                } else if (selectedCategory === '투자' && product.stock_name) {
                    companyName = product.stock_name;
                }
                if (companyName) {
                    const investmentCompanyName = getBankEnglishName(companyName);
                    if (investmentCompanyName) {
                        return `https://myapp-logos.s3.amazonaws.com/bank-logos/${investmentCompanyName}.png`;
                    }
                }
            }
        }

        // 이미지 URL이 있으면 처리
        if (imageUrl) {
            // 이미 완전한 URL이면 그대로 반환
            if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
                return imageUrl;
            }
            // 상대 경로면 API_BASE_URL 추가
            return `${API_BASE_URL}${imageUrl}`;
        }

        // 없다면 기본 아이콘으로
        return DEFAULT_ICON;
    };

    // 이미지 로드 실패 시(itemId) 기록 → Placeholder로 전환
    const handleImageError = (itemId) => {
        setImageErrors((prev) => ({
            ...prev,
            [itemId]: true,
        }));
    };

    // ─────────────────────────────────────────────────────────────────────────
    // 카테고리별 API 호출 함수
    // 1) 예·적금 / 대출 → FSS 스펙 (스네이크 케이스) 그대로
    // 2) 카드 / 보험 / ETF / 투자 → REST API 호출
    // ─────────────────────────────────────────────────────────────────────────
    const fetchProductsByCategory = async (category) => {
        setLoading(true);
        setError(null);
        console.log(`[FinancialHome] [fetchProductsByCategory] 선택 카테고리: "${category}"`);

        const api = axios.create({
            baseURL: API_BASE_URL,
            timeout: 30000,
        });

        try {
            let data = [];

            switch (category) {
                // ───────────────────────────────────────────────────────────────────
                // 예·적금 탭: 스네이크 케이스 그대로 (result.baseList 합치기)
                // ───────────────────────────────────────────────────────────────────
                case '예·적금': {
                    console.log('[FinancialHome] 예·적금 API 호출 시작');
                    const depositRes = await api.get('/api/fss/deposit');
                    console.log(
                        '[FinancialHome] depositRes.data 샘플 →',
                        JSON.stringify(depositRes.data).substr(0, 120) + '…'
                    );

                    const savingRes = await api.get('/api/fss/saving');
                    console.log(
                        '[FinancialHome] savingRes.data 샘플 →',
                        JSON.stringify(savingRes.data).substr(0, 120) + '…'
                    );

                    const depositList = Array.isArray(depositRes.data?.result?.baseList)
                        ? depositRes.data.result.baseList
                        : [];
                    const savingList = Array.isArray(savingRes.data?.result?.baseList)
                        ? savingRes.data.result.baseList
                        : [];

                    data = [...depositList, ...savingList];
                    console.log(
                        `[FinancialHome] 예·적금 데이터 합계: deposit(${depositList.length}) + saving(${savingList.length}) = 총 ${data.length}개`
                    );
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                // 카드 탭: 신용카드 + 체크카드 데이터 합치기
                // ───────────────────────────────────────────────────────────────────
                case '카드': {
                    console.log('[FinancialHome] 카드 API 호출 시작 (신용카드 + 체크카드)');
                    const creditCardsRes = await api.get('/api/cards/credit');
                    console.log(
                        '[FinancialHome] creditCardsRes.data 샘플 →',
                        JSON.stringify(creditCardsRes.data).substr(0, 120) + '…'
                    );

                    const checkCardsRes = await api.get('/api/cards/check');
                    console.log(
                        '[FinancialHome] checkCardsRes.data 샘플 →',
                        JSON.stringify(checkCardsRes.data).substr(0, 120) + '…'
                    );

                    const creditArr = Array.isArray(creditCardsRes.data) ? creditCardsRes.data : [];
                    const checkArr = Array.isArray(checkCardsRes.data) ? checkCardsRes.data : [];

                    data = [...creditArr, ...checkArr];
                    console.log(
                        `[FinancialHome] 카드 데이터 합계: credit(${creditArr.length}) + check(${checkArr.length}) = 총 ${data.length}개`
                    );
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                // 대출 탭: 스네이크 케이스 그대로 (result.baseList 합치기)
                // ───────────────────────────────────────────────────────────────────
                case '대출': {
                    console.log('[FinancialHome] 대출 API 호출 시작 (전세 + 신용)');
                    const rentRes = await api.get('/api/fss/rentloan');
                    console.log(
                        '[FinancialHome] rentRes.data 샘플 →',
                        JSON.stringify(rentRes.data).substr(0, 120) + '…'
                    );

                    const creditLoanRes = await api.get('/api/fss/creditloan');
                    console.log(
                        '[FinancialHome] creditLoanRes.data 샘플 →',
                        JSON.stringify(creditLoanRes.data).substr(0, 120) + '…'
                    );

                    const rentList = Array.isArray(rentRes.data?.result?.baseList)
                        ? rentRes.data.result.baseList
                        : [];
                    const creditLoanList = Array.isArray(creditLoanRes.data?.result?.baseList)
                        ? creditLoanRes.data.result.baseList
                        : [];

                    data = [...rentList, ...creditLoanList];
                    console.log(
                        `[FinancialHome] 대출 데이터 합계: rent(${rentList.length}) + credit(${creditLoanList.length}) = 총 ${data.length}개`
                    );
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                // 보험 탭: 실제 내려오는 필드명 그대로 사용 (cmpyNm, prdNm, mlInsRt, fmlInsRt)
                // ───────────────────────────────────────────────────────────────────
                case '보험': {
                    console.log('[FinancialHome] 보험 API 호출 시작');
                    const insListRes = await api.get('/api/insurance/list');
                    console.log(
                        '[FinancialHome] insListRes.data[0] 샘플 →',
                        JSON.stringify(insListRes.data[0]).substr(0, 120) + '…'
                    );

                    data = Array.isArray(insListRes.data) ? insListRes.data : [];
                    console.log(`[FinancialHome] 보험 데이터 총 ${data.length}개 로드`);
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                // ETF 탭: GET → 404 시 POST/update → 재GET
                // ───────────────────────────────────────────────────────────────────
                case 'ETF': {
                    console.log('[FinancialHome] ETF 데이터 조회 시도');
                    const etfDate = getTodayString();
                    try {
                        const etfRes = await api.get(`/api/etfs/daily/${etfDate}`);
                        console.log('[FinancialHome] etfRes.length=', etfRes.data.length);

                        if (Array.isArray(etfRes.data) && etfRes.data.length > 0) {
                            console.log(
                                '[FinancialHome] 첫 번째 ETF 객체 샘플 →',
                                JSON.stringify(etfRes.data[0], null, 2)
                            );
                        }
                        data = Array.isArray(etfRes.data) ? etfRes.data : [];
                        console.log(`[FinancialHome] ETF 조회 성공: ${data.length}개`);
                    } catch (getErr) {
                        if (getErr.response?.status === 404) {
                            console.warn('[FinancialHome] ETF 데이터 없음 → update 후 재조회');
                            await api.post('/api/etfs/update');
                            const etfRes2 = await api.get(`/api/etfs/daily/${etfDate}`);
                            console.log('[FinancialHome] etfRes2.length=', etfRes2.data.length);

                            if (Array.isArray(etfRes2.data) && etfRes2.data.length > 0) {
                                console.log(
                                    '[FinancialHome] 첫 번째 ETF 객체 샘플 (재조회) →',
                                    JSON.stringify(etfRes2.data[0], null, 2)
                                );
                            }
                            data = Array.isArray(etfRes2.data) ? etfRes2.data : [];
                            console.log(`[FinancialHome] ETF 재조회 성공: ${data.length}개`);
                        } else {
                            throw getErr;
                        }
                    }
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                // 투자 탭: GET → 404 시 POST/fetch → 재GET
                // ───────────────────────────────────────────────────────────────────
                case '투자': {
                    console.log('[FinancialHome] 주식 데이터 조회 시도');
                    const stockDate = getTodayString();
                    try {
                        const stockRes = await api.get(`/api/stocks/daily/${stockDate}`);
                        console.log('[FinancialHome] stockRes.length=', stockRes.data.length);
                        data = Array.isArray(stockRes.data) ? stockRes.data : [];
                        console.log(`[FinancialHome] 주식 조회 성공: ${data.length}개`);
                    } catch (getErr) {
                        if (getErr.response?.status === 404) {
                            console.warn('[FinancialHome] 주식 데이터 없음 → fetch 후 재조회');
                            await api.post(`/api/stocks/fetch/${stockDate}`);
                            const stockRes2 = await api.get(`/api/stocks/daily/${stockDate}`);
                            console.log('[FinancialHome] stockRes2.length=', stockRes2.data.length);
                            data = Array.isArray(stockRes2.data) ? stockRes2.data : [];
                            console.log(`[FinancialHome] 주식 재조회 성공: ${data.length}개`);
                        } else {
                            throw getErr;
                        }
                    }
                    break;
                }

                // ───────────────────────────────────────────────────────────────────
                default: {
                    console.warn('[FinancialHome] 알 수 없는 카테고리:', category);
                    data = [];
                    break;
                }
            }

            // setProducts 전에 첫 번째 요소 샘플을 찍어서, fin_co_no 등이 제대로 내려오는지 확인
            if (Array.isArray(data) && data.length > 0) {
                console.log(
                    `[FinancialHome] [DEBUG] data[0] sample for category="${category}":`,
                    JSON.stringify(data[0], null, 2)
                );
            } else {
                console.log(`[FinancialHome] [DEBUG] data 배열 길이: ${data.length}`);
            }

            setProducts(data);
        } catch (err) {
            console.error(`[FinancialHome] ${category} API 최종 오류 →`, err);
            if (err.code === 'ECONNABORTED') {
                setError('요청 시간이 초과되었습니다. 서버 상태를 확인해주세요.');
            } else if (err.response?.status === 404) {
                setError('데이터가 존재하지 않습니다.');
            } else {
                setError(`${category} 상품 정보를 불러오는데 실패했습니다. (${err.message})`);
            }
        } finally {
            setLoading(false);
        }
    };

    // ─────────────────────────────────────────────────────────────────────────
    // 검색 처리: searchText가 있을 때 JSON.stringify(product)에 포함 여부 검사
    // ─────────────────────────────────────────────────────────────────────────
    const handleSearch = () => {
        if (searchText.trim() === '') return products;
        const lowerSearch = searchText.trim().toLowerCase();
        return products.filter((product) =>
            JSON.stringify(product).toLowerCase().includes(lowerSearch)
        );
    };
    const displayProducts = handleSearch();

    return (
        <>
            <Header />
            <View className="flex-1 bg-Fineed-background pt-2 px-4 pb-12">
                <ScrollView
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding + 5,
                        paddingTop: 16,
                        paddingBottom: 24,
                        justifyContent: 'center',
                    }}
                >
                    {/* ─────────────────────────────────────────────────────────────
             1) 카테고리 탭 (회색 선을 제거했습니다)
           ───────────────────────────────────────────────────────────── */}
                    <View className="mb-5 overflow-hidden bg-Fineed-background">
                        <ScrollView
                            horizontal
                            showsHorizontalScrollIndicator={false}
                            contentContainerStyle={{ gap: 8, paddingRight: 40 }}
                            className="pb-1"
                            style={{ overflow: 'visible' }}
                        >
                            {categories.map((cat) => (
                                <TouchableOpacity
                                    key={cat}
                                    onPress={() => navigation.replace('FinancialHome', { category: cat })}
                                    className={`px-4 py-2 rounded-full shadow-md ${selectedCategory === cat ? 'bg-[#014029]' : 'bg-[#F9F9F9]'
                                        }`}
                                >
                                    <Text
                                        className={`text-sm font-semibold ${selectedCategory === cat ? 'text-white' : 'text-black'
                                            }`}
                                    >
                                        {cat}
                                    </Text>
                                </TouchableOpacity>
                            ))}
                        </ScrollView>
                    </View>

                    {/* (회색 선 View를 완전히 삭제했습니다) */}

                    {/* ─────────────────────────────────────────────────────────────
             2) 검색창 (기존 레이아웃 유지)
           ───────────────────────────────────────────────────────────── */}
                    <View
                        className="flex-row items-center bg-white rounded-full px-5 py-0.5 shadow-md mb-5"
                        style={{
                            paddingVertical: 12,
                            paddingHorizontal: horizontalPadding,
                        }}
                    >
                        <TouchableOpacity onPress={() => navigation.goBack()} className="mr-2">
                            <Icon name="arrow-back" size={22} color="#014029" />
                        </TouchableOpacity>
                        <TextInput
                            className="flex-1 text-sm text-black"
                            placeholder="검색할 상품을 입력하세요"
                            value={searchText}
                            onChangeText={setSearchText}
                        />
                        <Icon name="search" size={20} color="#014029" />
                    </View>

                    {/* ─────────────────────────────────────────────────────────────
             3) 상품 리스트: 로딩 / 에러 / 데이터 출력
           ───────────────────────────────────────────────────────────── */}
                    <View className="flex-col justify-center gap-4 mb-8">
                        {loading ? (
                            <View className="flex items-center justify-center py-10">
                                <ActivityIndicator size="large" color="#014029" />
                                <Text className="mt-2 text-[#014029]">
                                    {selectedCategory} 상품 정보를 불러오는 중…
                                </Text>
                            </View>
                        ) : error ? (
                            <View className="flex items-center justify-center py-10">
                                <Text className="text-red-500">{error}</Text>
                            </View>
                        ) : displayProducts.length > 0 ? (
                            displayProducts.map((product, index) => {
                                // ───────────────────────────────────────────────────────────
                                // key 중복 방지를 위해 “고유ID-인덱스” 조합
                                // ───────────────────────────────────────────────────────────
                                let baseId;
                                if (selectedCategory === '예·적금' || selectedCategory === '대출')
                                    baseId = product.fin_prdt_cd;
                                else if (selectedCategory === '카드')
                                    baseId = product.cardId || product.id;
                                else if (selectedCategory === '보험')
                                    baseId = product.insuranceId || product.id;
                                else if (selectedCategory === 'ETF') baseId = product.isuCd;
                                else if (selectedCategory === '투자') baseId = product.stock_symbol;
                                else baseId = product.id?.toString() || '';

                                const itemId = `${baseId}-${index}`;

                                // ───────────────────────────────────────────────────────────
                                // 이미지 URL 가져오기
                                // ───────────────────────────────────────────────────────────
                                const imageUrl = getImageUrl(product);
                                const hasImageError = imageErrors[itemId] || false;

                                return (
                                    <TouchableOpacity
                                        key={itemId}
                                        className="bg-[#F9F9F9] px-6 py-4 rounded-2xl shadow-md flex-row items-center"
                                        onPress={() => {
                                            if (selectedCategory === '예·적금') {
                                                navigation.navigate('DepositProduct', {
                                                    productId: product.fin_prdt_cd,
                                                    finCoNo: product.fin_co_no,
                                                });
                                            } else {
                                                console.log('상품 상세 정보:', product);
                                            }
                                        }}
                                    >
                                        {/* ─────────────────────────────────────────────────────
                        왼쪽: 이미지(로고) 로딩 또는 Placeholder(첫 글자)
                       ───────────────────────────────────────────────────── */}
                                        {!hasImageError ? (
                                            <Image
                                                source={{ uri: imageUrl }}
                                                className="w-16 h-16 rounded-full mr-5"
                                                style={{
                                                    backgroundColor: '#f0f0f0',
                                                    borderWidth: 1,
                                                    borderColor: '#ddd',
                                                }}
                                                resizeMode="cover"
                                                onError={() => {
                                                    console.log(`[ERROR] 이미지 로드 실패: ${imageUrl}`);
                                                    handleImageError(itemId);
                                                }}
                                            />
                                        ) : (
                                            <View className="w-16 h-16 bg-[#D3DFD9] rounded-full mr-5 items-center justify-center">
                                                <Text className="text-[#014029] text-xl font-bold">
                                                    {(() => {
                                                        if (
                                                            (selectedCategory === '예·적금' ||
                                                                selectedCategory === '대출') &&
                                                            product.kor_co_nm
                                                        ) {
                                                            return product.kor_co_nm[0];
                                                        }
                                                        if (selectedCategory === '보험' && product.cmpyNm) {
                                                            return product.cmpyNm[0];
                                                        }
                                                        if (selectedCategory === '카드' && product.company) {
                                                            return product.company[0];
                                                        }
                                                        if (selectedCategory === 'ETF' && product.isuNm) {
                                                            return product.isuNm[0];
                                                        }
                                                        if (selectedCategory === '투자' && product.stock_name) {
                                                            return product.stock_name[0];
                                                        }
                                                        return '?';
                                                    })()}
                                                </Text>
                                            </View>
                                        )}

                                        {/* ─────────────────────────────────────────────────────
                        각 카테고리별 렌더링 분기
                       ───────────────────────────────────────────────────── */}
                                        {selectedCategory === '예·적금' ? (
                                            <View className="flex-col flex-1">
                                                <Text className="text-xs text-[#6D6D6D]">
                                                    {product.kor_co_nm || '금융사'}
                                                </Text>
                                                <Text className="text-xl font-bold text-[#014029] mb-1">
                                                    {product.fin_prdt_nm || '상품명'}
                                                </Text>
                                                {product.mtrt_int && (
                                                    <Text className="text-xs text-[#014029]">
                                                        만기 후 이자율: {product.mtrt_int}%
                                                    </Text>
                                                )}
                                                {Array.isArray(product.options) &&
                                                    product.options.length > 0 &&
                                                    product.options[0].intr_rate && (
                                                        <Text className="text-xs text-[#014029]">
                                                            기준 금리: {product.options[0].intr_rate}%
                                                        </Text>
                                                    )}
                                            </View>
                                        ) : selectedCategory === '대출' ? (
                                            <View className="flex-col flex-1">
                                                <Text className="text-xs text-[#6D6D6D]">
                                                    {product.kor_co_nm || '금융사'}
                                                </Text>
                                                <Text className="text-xl font-bold text-[#014029] mb-1">
                                                    {product.fin_prdt_nm || '상품명'}
                                                </Text>
                                                {product.dly_rate && (
                                                    <Text className="text-xs text-[#014029]">
                                                        연 이자율: {product.dly_rate}%
                                                    </Text>
                                                )}
                                            </View>
                                        ) : selectedCategory === '보험' ? (
                                            <View className="flex-col flex-1">
                                                <Text className="text-xs text-[#6D6D6D]">
                                                    {product.cmpyNm || '보험사'}
                                                </Text>
                                                <Text className="text-xl font-bold text-[#014029] mb-1">
                                                    {product.prdNm || '보험명'}
                                                </Text>
                                                {product.mlInsRt && (
                                                    <Text className="text-xs text-[#014029]">
                                                        남성 보험료:{' '}
                                                        {Number(product.mlInsRt).toLocaleString()}원
                                                    </Text>
                                                )}
                                                {product.fmlInsRt && (
                                                    <Text className="text-xs text-[#014029]">
                                                        여성 보험료:{' '}
                                                        {Number(product.fmlInsRt).toLocaleString()}원
                                                    </Text>
                                                )}
                                            </View>
                                        ) : selectedCategory === '카드' ? (
                                            <View className="flex-col flex-1">
                                                <Text className="text-xs text-[#6D6D6D]">
                                                    {product.company || '카드사'}
                                                </Text>
                                                <Text className="text-xl font-bold text-[#014029] mb-1">
                                                    {product.cardName || '카드명'}
                                                </Text>
                                                {product.benefit && (
                                                    <Text className="text-xs text-[#014029]">
                                                        혜택: {product.benefit}
                                                    </Text>
                                                )}
                                            </View>
                                        ) : selectedCategory === 'ETF' ? (
                                            <View className="flex-col flex-1">
                                                {/* ETF 이름 */}
                                                <Text className="text-sm text-[#6D6D6D] mb-1">
                                                    {product.isuNm || 'ETF명 없음'}
                                                </Text>

                                                {/* 현재가 + 등락률 */}
                                                <View className="flex-row items-center">
                                                    {product.tddClsprc != null ? (
                                                        <Text className="text-lg font-bold text-[#212121]">
                                                            {Number(product.tddClsprc).toLocaleString()}원
                                                        </Text>
                                                    ) : (
                                                        <Text className="text-lg font-bold text-[#212121]">
                                                            -원
                                                        </Text>
                                                    )}

                                                    {product.flucRt != null && (
                                                        <Text
                                                            className={`ml-2 text-sm font-semibold ${product.flucRt > 0
                                                                    ? 'text-red-500'
                                                                    : product.flucRt < 0
                                                                        ? 'text-blue-500'
                                                                        : 'text-[#333333]'
                                                                }`}
                                                        >
                                                            {product.flucRt > 0
                                                                ? '▲ '
                                                                : product.flucRt < 0
                                                                    ? '▼ '
                                                                    : ''}
                                                            {Math.abs(product.flucRt).toFixed(2)}%
                                                        </Text>
                                                    )}
                                                </View>
                                            </View>
                                        ) : (
                                            // 투자(주식) 탭
                                            <View className="flex-col flex-1">
                                                <Text className="text-xs text-[#6D6D6D]">
                                                    {product.stock_name || '종목명'}
                                                </Text>
                                                <Text className="text-xl font-bold text-[#014029] mb-1">
                                                    {product.stock_symbol || '심볼'}
                                                </Text>
                                                {product.stock_price !== undefined && (
                                                    <Text className="text-xs text-[#014029]">
                                                        현재가:{' '}
                                                        {Number(product.stock_price).toLocaleString()}원
                                                    </Text>
                                                )}
                                            </View>
                                        )}
                                    </TouchableOpacity>
                                );
                            })
                        ) : (
                            <View className="items-center py-4">
                                <Text className="text-gray-500">검색 결과가 없습니다.</Text>
                            </View>
                        )}
                    </View>
                </ScrollView>
            </View>
        </>
    );
};

export default FinancialHome;
