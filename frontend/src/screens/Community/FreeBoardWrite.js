import { View, ScrollView, Text, TextInput, useWindowDimensions, TouchableOpacity } from 'react-native';
import Header from '../../components/layout/Header';
import Icon from 'react-native-vector-icons/Ionicons';
import { useState } from 'react';

export default function FreeBoardWriteScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 380 ? 16 : 12;

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [category, setCategory] = useState('자유게시판');

    const handleSubmit = () => {
        // TODO: 게시글 등록 로직 구현
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
                    className="flex-row items-center bg-[#014029] rounded-2xl mb-4"
                    style={{
                        paddingVertical: 12,
                        paddingHorizontal: horizontalPadding,
                    }}>
                    {/* 중앙 타이틀 */}
                    <Text className="text-white text-sm font-semibold">자유게시판 글쓰기</Text>
                </View>

                {/* 게시글 작성 */}
                <ScrollView
                    className="flex-1"
                    contentContainerStyle={{
                        paddingHorizontal: horizontalPadding,
                        paddingTop: 16,
                        paddingBottom: 24,
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
                        className="bg-[#F9F9F9] p-4 rounded-xl text-sm text-black h-40 mb-3"
                    />

                    {/* 하단 버튼들 */}
                    <View className="flex-row justify-between space-x-3 mt-6">
                        {/* 취소 버튼 */}
                        <TouchableOpacity
                            onPress={() => navigation.goBack()}
                            className="flex-1 bg-[#D9D9D9] py-3 rounded-xl items-center"
                        >
                            <Text className="text-black text-sm font-medium">취소소</Text>
                        </TouchableOpacity>
                        {/* 등록 버튼 */}
                        <TouchableOpacity
                            onPress={handleSubmit}
                            className="flex-1 bg-[#014029] py-3 rounded-xl items-center"
                        >
                            <Text className="text-white text-sm font-medium">등록록</Text>
                        </TouchableOpacity>
                    </View>
                </ScrollView>
                
            </View>
        </>
    );
}