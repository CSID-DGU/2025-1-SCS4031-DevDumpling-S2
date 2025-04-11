import { View, Text, TouchableOpacity } from 'react-native';

const Header = () => {
    return (
        <View className="flex-row justify-between items-center px-[27px] pt-[57px] pb-[12px]">
            <Text
                style={{ fontFamily: 'Pretendard-ExtraBold' }}
                className="text-[28px] leading-[36px] text-[#014029]"
            >
                FINEED
            </Text>
            <TouchableOpacity>
                <Text
                    style={{ fontFamily: 'Pretendard-Regular' }}
                    className="text-[14px] leading-[22px] text-black"
                >
                    로그인
                </Text>
            </TouchableOpacity>
        </View>
    );
};

export default Header;
