import { View, Text, TouchableOpacity, ScrollView, Modal, useWindowDimensions, TextInput, KeyboardAvoidingView, Platform } from 'react-native';
import Header from '../../components/layout/Header';
import { useState, useRef } from 'react';
import Icon from 'react-native-vector-icons/Ionicons'; // 아이콘 사용

export default function CommunityPostsScreen({ navigation }) {
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 360 ? 16 : 12;

    const [menuVisible, setMenuVisible] = useState(false); // 글 메뉴
    const [commentMenuVisible, setCommentMenuVisible] = useState(null); // 댓글 메뉴 (몇 번째 댓글인지 인덱스로 구분)

    const [liked, setLiked] = useState(false);
    const [scrapped, setScrapped] = useState(false);
    const [likes, setLikes] = useState(1); // 좋아요 수
    const [scraps, setScraps] = useState(0); // 스크랩 수

    const [comments, setComments] = useState([
        { id: 1, content: "댓글1" },
        { id: 2, content: "댓글2" }
    ]); // 댓글 리스트
    const [newComment, setNewComment] = useState('');

    const toggleMenu = () => setMenuVisible(!menuVisible);
    const openCommentMenu = (commentId, event) => {
            setCommentMenuVisible(commentId);        
    };
    const closeCommentMenu = () => setCommentMenuVisible(null);

    const toggleLike = () => {
        setLikes(prev => liked ? prev - 1 : prev + 1);
        setLiked(prev => !prev);
    };

    const toggleScrap = () => {
        setScraps(prev => scrapped ? prev - 1 : prev + 1);
        setScrapped(prev => !prev);
    }

    const handleAddComment = () => {
        if (newComment.trim()) {
            setComments([...comments, { id: Date.now(), content: newComment }]);
            setNewComment('');
        }
    }

    return (
        <>
            <Header />
            <KeyboardAvoidingView
                className="flex-1 bg-[#EFEFEF]"
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
                keyboardVerticalOffset={Platform.OS === 'ios' ? 80 : 0}
            >
                <View className="flex-1 pt-12 px-4">
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
                        <TouchableOpacity onPress={toggleMenu} style={{marginRight: 15}}>
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
                            <View className="flex-row items-center ml-5 mt-5 mb-3">
                                <View className="w-10 h-10 rounded-full bg-gray-300 mr-3" />
                                <View>
                                    <Text className="text-m font-semibold mb-0.5">알락꼬리꼬마도요</Text>
                                    <Text className="text-xs text-gray-500">25/04/17 19:49</Text>
                                </View>
                            </View>

                            {/* 제목 */}
                            <Text className="text-black font-bold text-base mb-2 ml-5">
                                아무도 안 물어봤지만 내 최애 재테크 방법
                            </Text>

                            {/* 내용 */}
                            <Text className="text-gray-700 text-m mb-10 ml-5">
                                난 솔직히 월세 받는 게 최고라고 생각함
                            </Text>

                            {/* 좋아요, 댓글, 스크랩 */}
                            <View className="flex-row justify-around border-t border-b border-gray-200 pt-3 pb-3">
                                <TouchableOpacity onPress={toggleLike} className="flex-row items-center">
                                    <Icon
                                        name={liked ? "heart" : "heart-outline"}
                                        size={16}
                                        color={liked ? "#E63946" : "#666"}
                                    />
                                    <Text className="text-xs text-gray-600 ml-1">좋아요 {likes}</Text>
                                </TouchableOpacity>

                                <Text className="text-xs text-gray-600">💬 댓글 {comments.length}</Text>

                                <TouchableOpacity onPress={toggleScrap} className="flex-row items-center">
                                    <Icon
                                        name={scrapped ? "bookmark" : "bookmark-outline"}
                                        size={16}
                                        color={scrapped ? "#FEE500" : "#666"}
                                    />
                                    <Text className="text-xs text-gray-600 ml-1">스크랩 {scraps}</Text>
                                </TouchableOpacity>
                            </View>

                            {/* 댓글 목록 */}
                            <View className="mt-5">
                                {comments.map((comment) => (
                                    <View key={comment.id} className="flex-row items-start mb-7">
                                        <View className="w-8 h-8 rounded-full bg-gray-300 ml-5 mr-3 mt-1" />
                                        <View className="flex-1 bg-[#F9F9F9] mr-5 rounded-xl">
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
                            <View
                                className="bg-white rounded-lg p-2 shadow w-32"
                                
                            >
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
                            <View
                                className="bg-white rounded-lg p-2 shadow w-32"
                                
                            >
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
            </KeyboardAvoidingView>
        </>
    );
}
