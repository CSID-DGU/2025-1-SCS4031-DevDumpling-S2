import React, { useEffect, useRef } from 'react';
import { View, Text, Image, Animated, StyleSheet, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Header from '../layout/Header';

const DumplingLoading = ({ message, onLoadingComplete }) => {
    const navigation = useNavigation();
    const dumplingAnim = useRef(new Animated.Value(0)).current;
    const steamAnim1 = useRef(new Animated.Value(0)).current;
    const steamAnim2 = useRef(new Animated.Value(0)).current;
    const steamAnim3 = useRef(new Animated.Value(0)).current;

    // 만두 통통 튀는 애니메이션
    useEffect(() => {
        const bounceDumpling = () => {
            Animated.sequence([
                Animated.timing(dumplingAnim, {
                    toValue: -10,
                    duration: 500,
                    useNativeDriver: true,
                }),
                Animated.timing(dumplingAnim, {
                    toValue: 0,
                    duration: 500,
                    useNativeDriver: true,
                }),
            ]).start(() => bounceDumpling());
        };

        bounceDumpling();

        return () => {
            dumplingAnim.stopAnimation();
        };
    }, []);

    // 김 올라가는 애니메이션
    useEffect(() => {
        const animateSteam = (steamAnim, delay) => {
            Animated.loop(
                Animated.sequence([
                    Animated.delay(delay),
                    Animated.timing(steamAnim, {
                        toValue: 1,
                        duration: 1500,
                        useNativeDriver: true,
                    }),
                    Animated.timing(steamAnim, {
                        toValue: 0,
                        duration: 0,
                        useNativeDriver: true,
                    }),
                ])
            ).start();
        };

        animateSteam(steamAnim1, 0);
        animateSteam(steamAnim2, 400);
        animateSteam(steamAnim3, 800);

        const timer = setTimeout(() => {
            if (onLoadingComplete) {
                onLoadingComplete();
            }
        }, 5000);

        return () => {
            steamAnim1.stopAnimation();
            steamAnim2.stopAnimation();
            steamAnim3.stopAnimation();
            clearTimeout(timer);
        };
    }, []);

    const renderSteam = (steamAnim, offsetX = 0) => (
        <Animated.View
            style={[
                styles.steam,
                {
                    left: offsetX,
                    transform: [
                        {
                            translateY: steamAnim.interpolate({
                                inputRange: [0, 1],
                                outputRange: [0, -80],
                            }),
                        },
                        {
                            translateX: steamAnim.interpolate({
                                inputRange: [0, 0.25, 0.5, 0.75, 1],
                                outputRange: [0, -15, 15, -10, 10],
                            }),
                        },
                        {
                            rotate: steamAnim.interpolate({
                                inputRange: [0, 0.5, 1],
                                outputRange: ['0deg', '8deg', '-8deg'],
                            }),
                        },
                    ],
                    opacity: steamAnim.interpolate({
                        inputRange: [0, 0.7, 1],
                        outputRange: [0, 1, 0],
                    }),
                },
            ]}
        >
            {/* 김을 작은 조각 여러개로 표현 */}
            <View style={styles.steamSegmentLarge} />
            <View style={styles.steamSegmentMedium} />
            <View style={styles.steamSegmentSmall} />
        </Animated.View>
    );




    return (
        <SafeAreaView className="flex-1 bg-Fineed-background">
            <Header />
            <View className="flex-1 justify-center items-center">
                {/* 김 애니메이션 */}
                <View className="relative">
                    {renderSteam(steamAnim1)}
                    {renderSteam(steamAnim2, 10)}
                    {renderSteam(steamAnim3, -10)}

                    {/* 만두 애니메이션 */}
                    <Animated.View style={{ transform: [{ translateY: dumplingAnim }] }}>
                        <Image
                            source={require('../../../assets/images/doman.png')}
                            style={{ width: 120, height: 120 }}
                            resizeMode="contain"
                        />
                    </Animated.View>
                </View>

                {/* 메시지 말풍선 */}
                <View className="bg-white p-4 rounded-2xl mx-6 mt-6 shadow-md">
                    {Array.isArray(message) ? (
                        message.map((line, index) => (
                            <Text key={index} className="text-center text-lg font-bold text-Fineed-green">
                                {line}
                            </Text>
                        ))
                    ) : (
                        <Text className="text-center text-lg font-bold text-Fineed-green">
                            {message}
                        </Text>
                    )}
                </View>
            </View>
        </SafeAreaView>
    );
};

const styles = StyleSheet.create({
    steam: {
        position: 'absolute',
        alignItems: 'center',
        justifyContent: 'center',
        top: -20,
        left: 0,
        right: 0,
    },
    steamLine: {
        width: 3,          // 더 얇게
        height: 35,        // 더 길게
        backgroundColor: 'white',
        borderRadius: 20,
        opacity: 0.6,      // 더 투명하게
    },
    steamSegmentLarge: {
        width: 8,
        height: 16,
        backgroundColor: 'white',
        borderRadius: 20,
        marginBottom: 4,
        opacity: 0.6,
    },
    steamSegmentMedium: {
        width: 6,
        height: 12,
        backgroundColor: 'white',
        borderRadius: 20,
        marginBottom: 4,
        opacity: 0.5,
    },
    steamSegmentSmall: {
        width: 4,
        height: 8,
        backgroundColor: 'white',
        borderRadius: 20,
        opacity: 0.4,
    },



});

export default DumplingLoading;
