import { View, ScrollView, Text, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import { useNavigation } from '@react-navigation/native';
import DepositProduct from './DepositProduct';


export default function ProductsHomeScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const bigCardWidth = width - 100;
    const cardWidth = (width - 100) / 2 - 8;

    const navigateToYouthProduct = () => {
        navigation.navigate('YouthProduct');
    };
    const navigateToDepositProduct = () => {
        navigation.navigate('DepositProduct');
    };
    const navigateToCardProduct = () => {
        navigation.navigate('CardProduct');
    };
    const navigateToLoanProduct = () => {
        navigation.navigate('LoanProduct');
    };
    const navigateToInsuranceProduct = () => {
        navigation.navigate('InsuranceProduct');
    };
    const navigateToETFProduct = () => {
        navigation.navigate('ETFProduct');
    };
    const navigateToInvestProduct = () => {
        navigation.navigate('InvestProduct');
    };

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">

                {/* 카테고리 탭 */}
                <View className="flex-row justify-center mb-4">
                    <View className="bg-[#014029] px-4 py-2 rounded-full w-full max-w-[200px] self-center">
                        <Text className="text-white text-center text-sm font-semibold">금융 상품 보러가기</Text>
                    </View>
                </View>

                {/* 게시판 카드 리스트 */}
                <ScrollView
                    contentContainerClassName="justify-center items-center py-5"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24
                    }}>
                    <TouchableOpacity
                        style={{ width: bigCardWidth, height: bigCardWidth / 4 }}
                        className="bg-[#FEF0D3] rounded-[20px] shadow-md p-5 items-center justify-center mb-5"
                        onPress={navigateToYouthProduct}>
                        <Text className="text-black text-center text-lg font-bold">청년 우대 상품</Text>
                    </TouchableOpacity>
                    <View className="border-t border-gray-200 w-full mb-5" />
                    <View className="flex-row justify-between gap-4 mb-4">
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToDepositProduct}>
                            <Text className="text-black text-center text-m font-semibold">예·적금</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToCardProduct}>
                            <Text className="text-black text-center text-m font-semibold">카드</Text>
                        </TouchableOpacity>
                    </View>
                    <View className="flex-row justify-between gap-4 mb-4">
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToLoanProduct}>
                            <Text className="text-black text-center text-m font-semibold">대출</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToInsuranceProduct}>
                            <Text className="text-black text-center text-m font-semibold">보험</Text>
                        </TouchableOpacity>
                    </View>
                    <View className="flex-row justify-between gap-4 mb-4">
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToETFProduct}>
                            <Text className="text-black text-center text-m font-semibold">ETF</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={{ width: cardWidth, height: bigCardWidth / 5 }}
                            className="bg-white rounded-[20px] shadow-md p-5 items-center justify-center"
                            onPress={navigateToInvestProduct}>
                            <Text className="text-black text-center text-m font-semibold">투자</Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>

                {/* 메인으로 돌아가기 링크 */}
                <TouchableOpacity className="mb-8 items-center"
                    onPress={() => navigation.navigate('Home')}>
                    <Text className="text-sm text-[#6D6D6D] underline">메인으로 돌아가기</Text>
                </TouchableOpacity>
            </View>
        </>
    );
}