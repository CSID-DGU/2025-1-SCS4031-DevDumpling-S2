import { View, Text, TouchableOpacity } from 'react-native';

export default function BoardPostItem({ 
    post, 
    onPress,
    isLastItem
}) {
    return (
        <View>
            <TouchableOpacity className="px-4 py-4" onPress={onPress}>
                <Text
                    className="font-semibold text-black text-sm"
                    numberOfLines={1}
                > {post.title} </Text>
                <Text
                    className="text-xs text-xs text-gray-600 mt-1"
                    numberOfLines={2}
                > {post.content} </Text>
                <Text className="text-[10px] text-gray-400 mt-1"> {post.author} </Text>
            </TouchableOpacity>

            {/* 항목 사이에만 구분선 */}
            {!isLastItem && (
                <View className="border-t border-gray-200 mx-4" />
            )}
        </View>
    );
} 