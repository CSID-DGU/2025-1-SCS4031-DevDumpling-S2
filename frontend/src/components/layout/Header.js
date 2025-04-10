import { View, Text, TouchableOpacity } from 'react-native';

const Header = () => {
    return (
        <View className="flex-row justify-between items-center px-4 pt-12 pb-4">
            <Text
                style={{ fontFamily: 'Pretendard-ExtraBold' }}
                className="text-fineed-green text-2xl"
            >
                FINEED
            </Text>
            <TouchableOpacity>
                <Text
                    style={{ fontFamily: 'Pretendard-Regular' }}
                    className="text-base text-gray-900"
                >
                    로그인
                </Text>
            </TouchableOpacity>
        </View>
    );
}

export default Header;