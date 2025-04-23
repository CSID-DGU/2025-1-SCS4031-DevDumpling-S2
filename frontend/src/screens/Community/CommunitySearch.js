import { View, ScrollView, Text, TextInput, useWindowDimensions, TouchableOpacity, Touchable, FlatList } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import { useState } from 'react';


export default function CommunitySearchScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;
    const [searchText, setSearchText] = useState('');
    const [searchHistory, setSearchHistory] = useState(['연금', '보험 증명서', '퇴직연금']);
    
    const handleDeleteItem = (item) => {
        setSearchHistory(searchHistory.filter(i => i !== item));
    }

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">

                {/* 검색 바 */}
                <View
                    className="flex-row items-center bg-white rounded-full px-3 py-2 shadow-md mb-4"
                    style={{
                        paddingVertical: 12,
                        paddingHorizontal: horizontalPadding,
                    }}>
                    <TouchableOpacity onPress={() => navigation.goBack()} className="mr-2">
                        <Icon name="arrow-back" size={22} color="#014029" />
                    </TouchableOpacity>
                    <TextInput
                        className="flex-1 text-sm text-black"
                        placeholder="검색할 내용을 입력하세요"
                        value={searchText}
                        onChangeText={setSearchText}
                    />
                    <Icon name="search" size={20} color="#014029" />
                </View>

                {/* 검색 기록 */}
                <Text className="text-gray-700 text-base font-semibold mb-2">검색 기록</Text>
                <FlatList
                    data={searchHistory}
                    keyExtractor={(item) => item}
                    renderItem={({ item }) => (
                        <View className="flex-row justify-between items-center py-2 border-b border-gray-200">
                            <Text className="text-base text-black">{item}</Text>
                            <TouchableOpacity onPress={() => handleDeleteItem(item)}>
                                <Icon name="close" size={18} color="gray" />
                            </TouchableOpacity>
                        </View>
                    )}>

                </FlatList>
                
            </View>
        </>
    );
}