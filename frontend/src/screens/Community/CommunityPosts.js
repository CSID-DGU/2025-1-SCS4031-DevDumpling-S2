import {
    View, Text, TouchableOpacity, ScrollView, useWindowDimensions,
    TextInput, KeyboardAvoidingView, ActivityIndicator, Platform, Modal
} from 'react-native';
import Header from '../../components/layout/Header';
import React, { useState, useEffect } from 'react';
import Icon from 'react-native-vector-icons/Ionicons';
import { deleteBoardPost, deleteComment } from './CommunityApi';

// 단순화된 버전의 CommunityPosts 컴포넌트
export default function CommunityPostsScreen({ navigation, route }) {
    // 고정 데이터 사용 (navigation이나 route가 없어도 동작)
    const postId = route?.params?.postId || 1;
    const boardType = route?.params?.boardType || 'FREE';
    const { width } = useWindowDimensions();
    const horizontalPadding = width > 360 ? 16 : 12;

    const [menuVisible, setMenuVisible] = useState(false);
    const [commentMenuVisible, setCommentMenuVisible] = useState(null);
    const [loading, setLoading] = useState(true);
    const [post, setPost] = useState(null);

    const [liked, setLiked] = useState(false);
    const [scrapped, setScrapped] = useState(false);
    const [likes, setLikes] = useState(0);
    const [scraps, setScraps] = useState(0);

    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');

    useEffect(() => {
        // 하드코딩된 게시글 데이터
        const postData = {
            id: postId,
            title: postId === 1 ? "요즘 재테크 어떻게들 하세요?" :
                postId === 2 ? "신용카드 추천 부탁드립니다" :
                    postId === 3 ? "월급 관리 꿀팁 공유" :
                        postId === 4 ? "주식 입문자 조언 구합니다" :
                            postId === 5 ? "적금 vs 펀드 어떤게 좋을까요?" :
                                "생활비 절약 꿀팁",
            content: postId === 1 ? "요즘 은행 금리도 낮아지고 주식도 불안정한데 다들 어떻게 재테크하시나요? 저는 요즘 부동산에 눈을 돌리고 있는데 아직 시기상조인지 고민이 됩니다. 여러분들의 지혜를 나눠주세요!" :
                postId === 2 ? "처음으로 신용카드를 만들려고 하는데 초보자에게 좋은 카드 추천해주세요! 주로 식비와 생활비 위주로 사용할 예정입니다. 포인트 혜택이 좋은 카드가 좋을 것 같아요." :
                    postId === 3 ? "월급날만 되면 통장이 텅텅 비는 분들을 위한 관리 팁 공유합니다~ 1. 월급 들어오자마자 저금액을 먼저 떼어놓기 2. 고정비용(월세, 공과금 등)을 별도 계좌로 관리하기 3. 식비는 1주일 단위로 현금화해서 사용하기" :
                        postId === 4 ? "주식 처음 시작하는데 어떤 종목부터 공부해야 할까요? 추천 좀 부탁드립니다. 기본적인 투자 원칙부터 시작해서 종목 선정 방법까지 조언 부탁드립니다." :
                            postId === 5 ? "목돈 마련을 위해 적금과 펀드 중 고민중인데 경험자분들 조언 부탁드려요. 안정성을 생각하면 적금이 좋을 것 같은데, 수익률은 펀드가 좋겠죠? 3년 정도 운영할 생각입니다." :
                                "매달 생활비 절약하는 나만의 방법들 공유합니다! 식비 절약이 핵심이에요~ 1. 주 1회 장보기 (계획 구매하기) 2. 외식 횟수 줄이기 3. 대중교통 이용하기 4. 불필요한 구독서비스 정리하기",
            author: postId === 1 ? "파이낸셜마스터" :
                postId === 2 ? "카드초보자" :
                    postId === 3 ? "절약왕" :
                        postId === 4 ? "주식초보" :
                            postId === 5 ? "저축러" :
                                "알뜰살뜰",
            likes: postId * 10 + 5,
            scraps: postId * 3 + 2,
            createdAt: "2025-06-07T10:30:00",
            views: postId * 30 + 15
        };

        // 하드코딩된 댓글 데이터
        const commentsData = [
            {
                id: 100,
                content: "정말 좋은 정보네요! 감사합니다.",
                author: "경제통",
                createdAt: "2025-06-07T11:30:00",
                likes: 7,
                liked: false
            },
            {
                id: 101,
                content: "저도 같은 고민 하고 있었는데 도움이 많이 됐어요.",
                author: "금융초보",
                createdAt: "2025-06-07T12:15:00",
                likes: 5,
                liked: false
            },
            {
                id: 102,
                content: "추가로 이런 방법도 있어요. [추가 정보 공유]",
                author: "재테크고수",
                createdAt: "2025-06-07T13:45:00",
                likes: 12,
                liked: false
            },
            {
                id: 103,
                content: "질문이 있는데요. 더 자세히 설명해주실 수 있나요?",
                author: "궁금이",
                createdAt: "2025-06-07T14:30:00",
                likes: 2,
                liked: false
            },
            {
                id: 104,
                content: "저는 이렇게 하고 있어요. 도움이 되셨으면 좋겠네요~",
                author: "공유왕",
                createdAt: "2025-06-07T15:20:00",
                likes: 9,
                liked: false
            }
        ];

        setPost(postData);
        setLikes(postData.likes);
        setScraps(postData.scraps);
        setComments(commentsData);
        setLoading(false);
    }, [boardType, postId]);

    const toggleMenu = () => setMenuVisible(!menuVisible);
    const openCommentMenu = (commentId) => setCommentMenuVisible(commentId);
    const closeCommentMenu = () => setCommentMenuVisible(null);

    const toggleLike = () => {
        setLikes(prev => liked ? prev - 1 : prev + 1);
        setLiked(prev => !prev);
    };

    const toggleScrap = () => {
        setScraps(prev => scrapped ? prev - 1 : prev + 1);
        setScrapped(prev => !prev);
    };

    const toggleCommentLike = (id) => {
        setComments(prev =>
            prev.map(comment =>
                comment.id === id
                    ? {
                        ...comment,
                        liked: !comment.liked,
                        likes: comment.liked ? comment.likes - 1 : comment.likes + 1
                    }
                    : comment
            )
        );
    };

    const handleAddComment = () => {
        if (newComment.trim()) {
            setComments([
                ...comments,
                { id: Date.now(), content: newComment, liked: false, likes: 0 }
            ]);
            setNewComment('');
        }
    };

    const handleDeletePost = async () => {
        try {
            await deleteBoardPost(boardType, postId);
            alert('게시글이 삭제되었습니다.');
            navigation.goBack();
        } catch (e) {
            if (e?.response?.status === 401 || e?.response?.status === 403) {
                alert('로그인 후 이용 가능한 기능입니다.');
            } else {
                alert('게시글 삭제에 실패했습니다.');
            }
        }
    };

    const handleDeleteComment = async (commentId) => {
        try {
            await deleteComment(commentId);
            alert('댓글이 삭제되었습니다.');
            setComments(comments.filter(c => c.id !== commentId));
            closeCommentMenu();
        } catch (e) {
            if (e?.response?.status === 401 || e?.response?.status === 403) {
                alert('로그인 후 이용 가능한 기능입니다.');
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        }
    };

    return (
        <>
            <Header />
            <KeyboardAvoidingView
                className="flex-1 bg-[#EFEFEF]"
                behavior={Platform.OS === 'ios' ? 'padding' : undefined}
                keyboardVerticalOffset={Platform.OS === 'ios' ? 80 : 0}
            >
                <View className="flex-1 pt-12 px-4">
                    {/* 상단 헤더 */}
                    <View
                        className="flex-row items-center justify-between bg-[#014029] rounded-2xl mb-4"
                        style={{ paddingVertical: 12, marginHorizontal: horizontalPadding }}
                    >
                        <TouchableOpacity onPress={() => navigation.goBack()} className="ml-4">
                            <Icon name="arrow-back-outline" size={24} color="#EFEFEF" />
                        </TouchableOpacity>
                        <Text className="text-white text-sm font-semibold">{boardType === 'FREE' ? '자유게시판' :
                            boardType === 'INVESTMENT' ? '투자게시판' :
                                boardType === 'CHALLENGE' ? '챌린지게시판' : '퀴즈게시판'}</Text>
                        <TouchableOpacity onPress={toggleMenu} style={{ marginRight: 15 }}>
                            <Icon name="ellipsis-vertical" size={24} color="#EFEFEF" />
                        </TouchableOpacity>
                    </View>

                    {/* 본문 */}
                    <ScrollView
                        className="flex-1"
                        contentContainerStyle={{
                            paddingHorizontal: horizontalPadding,
                            paddingTop: 16,
                            paddingBottom: 80,
                        }}
                    >
                        {loading ? (
                            <View className="flex-1 justify-center items-center">
                                <ActivityIndicator size="large" color="#014029" />
                            </View>
                        ) : post ? (
                            <View className="bg-[#F9F9F9] rounded-2xl shadow-md">
                                {/* 작성자 */}
                                <View className="flex-row items-center ml-5 mt-5 mb-3">
                                    <View className="w-10 h-10 rounded-full bg-gray-300 mr-3" />
                                    <View>
                                        <Text className="text-m font-semibold mb-0.5">{post.author}</Text>
                                        <Text className="text-xs text-gray-500">{new Date(post.createdAt).toLocaleString()}</Text>
                                    </View>
                                </View>

                                {/* 제목, 내용 */}
                                <Text className="text-black font-bold text-base mb-2 ml-5">
                                    {post.title}
                                </Text>
                                <Text className="text-gray-700 text-m mb-10 ml-5">
                                    {post.content}
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
                                        <View key={comment.id} className="flex-row items-start mb-5">
                                            <View className="w-8 h-8 rounded-full bg-gray-300 ml-5 mr-1 mt-3" />
                                            <View className="flex-1 bg-[#F9F9F9] mr-5 rounded-xl px-3 py-2">
                                                <View className="flex-row justify-between items-center">
                                                    <Text className="text-sm font-semibold">{comment.author}</Text>
                                                    <TouchableOpacity
                                                        className="flex-row items-center mr-20"
                                                        onPress={() => toggleCommentLike(comment.id)}
                                                    >
                                                        <Icon
                                                            name={comment.liked ? 'thumbs-up' : 'thumbs-up-outline'}
                                                            size={16}
                                                            color={comment.liked ? '#014029' : '#999'}
                                                        />
                                                        <Text className="ml-1 text-xs text-gray-600">{comment.likes}</Text>
                                                    </TouchableOpacity>
                                                    <TouchableOpacity onPress={() => openCommentMenu(comment.id)}>
                                                        <Icon name="ellipsis-vertical" size={16} color="#999" />
                                                    </TouchableOpacity>
                                                </View>
                                                <Text className="text-xs text-gray-500">{new Date(comment.createdAt).toLocaleString()}</Text>
                                                <Text className="text-gray-700 text-sm mt-1">{comment.content}</Text>
                                            </View>
                                        </View>
                                    ))}
                                </View>
                            </View>
                        ) : (
                            <View className="flex-1 justify-center items-center">
                                <Text>게시글을 찾을 수 없습니다.</Text>
                            </View>
                        )}
                    </ScrollView>

                    {/* 댓글 입력창 하단 고정 */}
                    <View className="absolute bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-2">
                        <View className="flex-row items-center">
                            <TextInput
                                className="flex-1 bg-[#F0F0F0] px-4 py-2 rounded-full text-sm"
                                value={newComment}
                                onChangeText={setNewComment}
                                placeholder="댓글을 작성하세요"
                                returnKeyType="send"
                                onSubmitEditing={handleAddComment}
                            />
                            <TouchableOpacity onPress={handleAddComment} className="ml-2">
                                <Icon name="send" size={20} color="#014029" />
                            </TouchableOpacity>
                        </View>
                    </View>

                    {/* 글 메뉴 모달 */}
                    <Modal visible={menuVisible} transparent animationType="fade" onRequestClose={toggleMenu}>
                        <TouchableOpacity
                            className="flex-1 justify-start items-end pt-16 pr-4 bg-transparent"
                            onPressOut={toggleMenu}
                            activeOpacity={1}
                        >
                            <View className="bg-white rounded-lg p-2 shadow w-32">
                                <TouchableOpacity onPress={() => { }} className="py-2 px-4">
                                    <Text className="text-sm text-black">수정하기</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={handleDeletePost} className="py-2 px-4">
                                    <Text className="text-sm text-black">삭제하기</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={() => { }} className="py-2 px-4">
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
                                <TouchableOpacity onPress={() => { }} className="py-2 px-4">
                                    <Text className="text-sm text-black">댓글 수정</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={() => handleDeleteComment(commentMenuVisible)} className="py-2 px-4">
                                    <Text className="text-sm text-black">댓글 삭제</Text>
                                </TouchableOpacity>
                                <TouchableOpacity onPress={() => { }} className="py-2 px-4">
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
