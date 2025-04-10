import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import PostCard from '../../components/common/PostCard';

const DUMMY_POSTS = [
  {
    id: 1,
    title: '리액트네이티브 개어려워요..',
    content: '리액트네이티브 개어려워요..',
    author: '예성짱짱맨',
    likes: 128
  },

];

export default function HomeScreen() {
  return (
    <View className="flex-1 bg-fineed-background">
      {/* Header */}
      <View className="bg-fineed-background p-4 pt-12">
        <Text style={{ fontFamily: 'Almarai-ExtraBold' }} className="text-fineed-green text-2xl">FINEED</Text>
      </View>

      {/* Category Tabs */}
      <View className="flex-row p-4 bg-fineed-background">

        <TouchableOpacity className="px-4 py-2 bg-gray-200 rounded-full">
          <Text className="text-gray-700">저축</Text>
        </TouchableOpacity>
      </View>

      {/* Posts List */}
      <ScrollView className="flex-1 px-4 pt-4">
        {DUMMY_POSTS.map(post => (
          <PostCard key={post.id} {...post} />
        ))}
      </ScrollView>

      {/* Floating Action Button */}
      <TouchableOpacity
        className="absolute bottom-6 right-6 w-14 h-14 bg-blue-500 rounded-full items-center justify-center shadow-lg"
      >
        <Text className="text-white text-3xl">+</Text>
      </TouchableOpacity>
    </View>
  );
}
