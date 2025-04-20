import { View, Text, TouchableOpacity, useWindowDimensions } from 'react-native';
import Icon from 'react-native-vector-icons/Ionicons';

export default function BoardHeader({ 
    navigation, 
    title, 
    showSearch = true, 
    showWrite = true,
    onSearchPress,
    onWritePress
}) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    return (
        <View
            className="flex-row items-center justify-between bg-[#014029] rounded-2xl mb-4"
            style={{
                paddingVertical: 12,
                marginHorizontal: horizontalPadding,
            }}>
            {/* 뒤로 가기 버튼 */}
            <TouchableOpacity onPress={() => navigation.goBack()} className="ml-4">
                <Icon name="arrow-back-outline" size={24} color="#EFEFEF" />
            </TouchableOpacity>
            {/* 중앙 타이틀 */}
            <Text className="text-white text-sm font-semibold">{title}</Text>
            {/* 오른쪽 아이콘들 */}
            <View className="flex-row mr-4">
                {showSearch && (
                    <TouchableOpacity
                        onPress={onSearchPress}
                        style={{ marginRight: 15 }}>
                        <Icon name="search-outline" size={22} color="#EFEFEF" />
                    </TouchableOpacity>
                )}
                {showWrite && (
                    <TouchableOpacity onPress={onWritePress}>
                        <Icon name="create-outline" size={22} color="#EFEFEF" />
                    </TouchableOpacity>
                )}
            </View>
        </View>
    );
} 