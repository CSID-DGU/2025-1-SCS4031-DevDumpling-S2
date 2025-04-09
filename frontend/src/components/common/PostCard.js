import { View, Text, TouchableOpacity } from 'react-native';

export default function PostCard({ title, content, author, likes }) {
  return (
    <TouchableOpacity className="bg-white p-4 rounded-xl mb-3 shadow-sm">
      <Text className="text-lg font-bold text-gray-800 mb-2">{title}</Text>
      <Text className="text-gray-600 mb-3" numberOfLines={2}>{content}</Text>
      <View className="flex-row justify-between items-center">
        <Text className="text-gray-500 text-sm">by {author}</Text>
        <View className="flex-row items-center">
        </View>
      </View>
    </TouchableOpacity>
  );
}
