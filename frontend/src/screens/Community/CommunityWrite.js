import { View, ScrollView, Text, TextInput, useWindowDimensions, TouchableOpacity, KeyboardAvoidingView, Platform, } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import { useState } from 'react';

export default function CommunityWriteScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [category, setCategory] = useState('자유게시판');
    const [isDropdownOpen, setDropdownOpen] = useState(false);

    const categories = ['HOT게시판', '투자게시판', '챌린지게시판', '퀴즈게시판', '자유게시판']

    const handleSubmit = () => {
        // TODO: 게시글 등록 로직 구현
        console.log('카테고리:', category)
        console.log('제목:', title);
        console.log('내용:', content);
        navigation.goBack();
    };

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">

                {/* 카테고리 탭 */}
                <View
                    className="flex-row items-center justify-between bg-[#014029] rounded-2xl mb-4"
                    style={{
                        paddingVertical: 12,
                        paddingHorizontal: horizontalPadding,
                    }}>
                    {/* 중앙 타이틀 */}
                    <Text className="text-white text-sm font-semibold text-center">커뮤니티 글쓰기</Text>
                </View>

                {/* 카테고리 드롭다운 버튼 */}
                <View className="mb-4">
                    <TouchableOpacity
                        onPress={() => setDropdownOpen(!isDropdownOpen)}
                        className="bg-white px-4 py-2 rounded-full self-start">
                            <Text className="text-sm text-black">{category} ⌄</Text>
                        </TouchableOpacity>

                        {/* 드롭다운 리스트 */}
                        {isDropdownOpen && (
                            <View className="bg-white mt-2 rounded-xl shadow p-2 w-40">
                                {categories.map((item) => (
                                    <TouchableOpacity
                                        key={item}
                                        onPress={() => {
                                            setCategory(item);
                                            setDropdownOpen(false);
                                        }}
                                        className="py-2"
                                    >
                                        <Text className="text-sm text-black text-center">{item}</Text>
                                    </TouchableOpacity>
                                ))}
                            </View>
                        )}
                </View>

                {/* 게시글 작성 */}
                <ScrollView
                    className="flex-1"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 100,
                    }}
                >
                    {/* 제목 입력 */}
                    <TextInput
                        placeholder="제목을 입력하세요"
                        value={title}
                        onChangeText={setTitle}
                        className="bg-[#F9F9F9] p-4 rounded-xl text-sm text-black mb-3"
                    />
                    {/* 내용 입력 */}
                    <TextInput
                        placeholder="내용을 입력하세요"
                        value={content}
                        onChangeText={setContent}
                        multiline
                        textAlignVertical="top"
                        className="bg-[#F9F9F9] p-4 rounded-xl text-sm text-black h-80 mb-3"
                    />

                    {/* 하단 버튼들 */}
                    <View className="flex-row justify-end gap-2 px-4 pb-6">
                    {/* 취소 버튼 */}
                        <TouchableOpacity
                            onPress={() => navigation.goBack()}
                            className="bg-[#D9D9D9] px-4 py-2 rounded-full"
                        >
                            <Text className="text-black text-xs">취소</Text>
                        </TouchableOpacity>
                        {/* 등록 버튼 */}
                        <TouchableOpacity
                            onPress={handleSubmit}
                            className="bg-[#014029] px-4 py-2 rounded-full"
                        >
                            <Text className="text-white text-sm font-medium">등록</Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
                
            </View>
        </>
    );
}