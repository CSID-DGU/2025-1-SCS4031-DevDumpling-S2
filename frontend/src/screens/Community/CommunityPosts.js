import { View, Text, TouchableOpacity, ScrollView, Modal, useWindowDimensions } from 'react-native';
import Header from '../../components/layout/Header';
import { useState } from 'react';
import Icon from 'react-native-vector-icons/Ionicons'; // 아이콘 사용

export default function CommunityPostsScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 360 ? 16 : 12;
    const [menuVisible, setMenuVisible] = useState(false); // 글 메뉴
    const [commentMenuVisible, setCommentMenuVisible] = useState(null); // 댓글 메뉴 (몇 번째 댓글인지 인덱스로 구분)
    const [likes, setLikes] = useState(1); // 좋아요 수
    const [scraps, setScraps] = useState(0); // 스크랩 수
    const [comments, setComments] = useState([
        { id: 1, content: "댓글1" },
        { id: 2, content: "댓글2" }
    ]); // 댓글 리스트

    const toggleMenu = () => setMenuVisible(!menuVisible);
    const openCommentMenu = (commentId) => setCommentMenuVisible(commentId);
    const closeCommentMenu = () => setCommentMenuVisible(null);

    const handleLike = () => setLikes(prev => prev + 1);
    const handleScrap = () => setScraps(prev => prev + 1);

    return (
        <>
            <Header />
            <View className="flex-1 bg-[#EFEFEF] pt-12 px-4">
            {/* 헤더 */}
            <View
                className="flex-row items-center justify-between bg-[#014029] rounded-2xl mb-4"
                style={{
                    paddingVertical: 12,
                    marginHorizontal: horizontalPadding,
                }}>
                <TouchableOpacity onPress={() => navigation.goBack()} className="ml-4">
                    <Icon name="arrow-back-outline" size={24} color="#EFEFEF" />
                </TouchableOpacity>
                <Text className="text-white text-sm font-semibold">자유게시판</Text>
                <TouchableOpacity onPress={toggleMenu}>
                    <Icon name="ellipsis-vertical" size={24} color="#EFEFEF" />
                </TouchableOpacity>
            </View>

            {/* 본문 */}
            <ScrollView
                className="flex-1"
                contentContainerStyle={{
                    paddingHorizontal: horizontalPadding,
                    paddingTop: 16,
                    paddingBottom: 24
                }}>
                <View className="bg-[#F9F9F9] rounded-2xl shadow-md">
                    {/* 작성자 */}
                    <View className="flex-row items-center mb-2">
                        <View className="w-8 h-8 rounded-full bg-gray-300 mr-2" />
                        <View>
                            <Text className="text-sm font-semibold">알락꼬리꼬마도요</Text>
                            <Text className="text-xs text-gray-500">25/04/17 19:49</Text>
                        </View>
                    </View>

                    {/* 제목 */}
                    <Text className="text-black font-bold text-base mb-2">
                        아무도 안 물어봤지만 내 최애 재테크 방법
                    </Text>

                    {/* 내용 */}
                    <Text className="text-gray-700 text-sm mb-4">
                        난 솔직히 월세 받는 게 최고라고 생각함
                    </Text>

                    {/* 좋아요, 댓글, 스크랩 */}
                    <View className="flex-row justify-around border-t border-gray-200 pt-2">
                        <TouchableOpacity onPress={handleLike}>
                            <Text className="text-xs text-gray-600">👍 좋아요 {likes}</Text>
                        </TouchableOpacity>
                        <Text className="text-xs text-gray-600">💬 댓글 {comments.length}</Text>
                        <TouchableOpacity onPress={handleScrap}>
                            <Text className="text-xs text-gray-600">📌 스크랩 {scraps}</Text>
                        </TouchableOpacity>
                    </View>

                    {/* 댓글 목록 */}
                    <View className="mt-4">
                        {comments.map((comment) => (
                            <View key={comment.id} className="flex-row items-start mb-4">
                                <View className="w-6 h-6 rounded-full bg-gray-300 mr-2" />
                                <View className="flex-1 bg-[#F9F9F9] p-2 rounded-xl">
                                    <View className="flex-row justify-between items-center">
                                        <Text className="text-sm font-semibold">알락꼬리꼬마도요</Text>
                                        <TouchableOpacity onPress={() => openCommentMenu(comment.id)}>
                                            <Icon name="ellipsis-vertical" size={16} color="#999" />
                                        </TouchableOpacity>
                                    </View>
                                    <Text className="text-xs text-gray-500">25/04/17 19:49</Text>
                                    <Text className="text-gray-700 text-sm mt-1">{comment.content}</Text>
                                </View>
                            </View>
                        ))}
                    </View>
                </View>
            </ScrollView>

            {/* 글 메뉴 모달 */}
            <Modal
                visible={menuVisible}
                transparent
                animationType="fade"
                onRequestClose={toggleMenu}
            >
                <TouchableOpacity
                    className="flex-1 justify-start items-end pt-16 pr-4 bg-transparent"
                    onPressOut={toggleMenu}
                    activeOpacity={1}
                >
                    <View className="bg-white rounded-lg p-2 shadow w-32">
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">수정하기</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">삭제하기</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">신고하기</Text>
                        </TouchableOpacity>
                    </View>
                </TouchableOpacity>
            </Modal>

            {/* 댓글 메뉴 모달 */}
            <Modal
                visible={commentMenuVisible !== null}
                transparent
                animationType="fade"
                onRequestClose={closeCommentMenu}
            >
                <TouchableOpacity
                    className="flex-1 justify-start items-end pt-16 pr-4 bg-transparent"
                    onPressOut={closeCommentMenu}
                    activeOpacity={1}
                >
                    <View className="bg-white rounded-lg p-2 shadow w-32">
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">댓글 수정</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">댓글 삭제</Text>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={() => {}} className="py-2 px-4">
                            <Text className="text-sm text-black">댓글 신고</Text>
                        </TouchableOpacity>
                    </View>
                </TouchableOpacity>
            </Modal>
        </View>
        </>
    );
}
