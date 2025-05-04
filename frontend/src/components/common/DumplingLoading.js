import React, { useEffect, useRef } from 'react';
import { View, Text, Image, Animated, StyleSheet, SafeAreaView } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import Header from '../layout/Header';

const steamConfigs = [
    { delay: 0, offsetX: -20 },
    { delay: 400, offsetX: -10 },
    { delay: 800, offsetX: 0 },
    { delay: 1200, offsetX: 10 },
    { delay: 1600, offsetX: 20 },
];

const DumplingLoading = ({ message, onLoadingComplete }) => {
    const navigation = useNavigation();

    const dumplingAnim = useRef(new Animated.Value(0)).current;
    const steamAnims = useRef(steamConfigs.map(() => new Animated.Value(0))).current;

    useEffect(() => {
        const bounce = () => {
            Animated.sequence([
                Animated.timing(dumplingAnim, { toValue: -10, duration: 500, useNativeDriver: true }),
                Animated.timing(dumplingAnim, { toValue: 0, duration: 500, useNativeDriver: true }),
            ]).start(() => bounce());
        };
        bounce();
        return () => dumplingAnim.stopAnimation();
    }, [dumplingAnim]);

    useEffect(() => {
        steamAnims.forEach((anim, i) => {
            Animated.loop(
                Animated.sequence([
                    Animated.delay(steamConfigs[i].delay),
                    Animated.timing(anim, { toValue: 1, duration: 1800, useNativeDriver: true }),
                    Animated.timing(anim, { toValue: 0, duration: 0, useNativeDriver: true }),
                ])
            ).start();
        });

        const timer = setTimeout(() => onLoadingComplete && onLoadingComplete(), 5000);
        return () => {
            clearTimeout(timer);
            steamAnims.forEach(a => a.stopAnimation());
        };
    }, [steamAnims, onLoadingComplete]);

    const renderSteam = (steamAnim, offsetX) => (
        <Animated.View
            key={offsetX}
            style={[
                styles.steam,
                {
                    left: '50%',
                    opacity: steamAnim.interpolate({ inputRange: [0, 0.6, 1], outputRange: [0, 1, 0] }),
                    transform: [
                        { translateX: offsetX },
                        { translateY: steamAnim.interpolate({ inputRange: [0, 1], outputRange: [0, -60] }) },
                        { translateX: steamAnim.interpolate({ inputRange: [0, 0.5, 1], outputRange: [0, 5, -5] }) },
                        { rotate: steamAnim.interpolate({ inputRange: [0, 0.5, 1], outputRange: ['0deg', '6deg', '-6deg'] }) },
                    ],
                },
            ]}
        >
            <View style={styles.steamSegmentLarge} />
            <View style={styles.steamSegmentMedium} />
            <View style={styles.steamSegmentSmall} />
        </Animated.View>
    );

    return (
        <>
            <Header />
            <SafeAreaView style={styles.screen}>
                <View style={styles.center}>
                    <View style={styles.container}>
                        {steamAnims.map((anim, i) => renderSteam(anim, steamConfigs[i].offsetX))}
                        <Animated.View style={{ transform: [{ translateY: dumplingAnim }] }}>
                            <Image
                                source={require('../../../assets/images/doman.png')}
                                style={{ width: 120, height: 120 }}
                                resizeMode="contain"
                            />
                        </Animated.View>
                    </View>
                    <View style={styles.bubble}>
                        {Array.isArray(message)
                            ? message.map((line, i) => <Text key={i} style={styles.bubbleText}>{line}</Text>)
                            : <Text style={styles.bubbleText}>{message}</Text>
                        }
                    </View>
                </View>
            </SafeAreaView>
        </>
    );
};

const styles = StyleSheet.create({
    screen: { flex: 1, backgroundColor: '#F5F5F5' },
    center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
    container: { position: 'relative', width: 150, height: 150, justifyContent: 'center', alignItems: 'center' },
    steam: { position: 'absolute', top: 0, alignItems: 'center', justifyContent: 'center' },
    steamSegmentLarge: { width: 12, height: 12, borderRadius: 6, backgroundColor: 'rgba(255,255,255,0.8)', marginBottom: 4 },
    steamSegmentMedium: { width: 8, height: 8, borderRadius: 4, backgroundColor: 'rgba(255,255,255,0.7)', marginBottom: 3 },
    steamSegmentSmall: { width: 6, height: 6, borderRadius: 3, backgroundColor: 'rgba(255,255,255,0.6)' },
    bubble: { backgroundColor: 'white', padding: 16, borderRadius: 24, marginHorizontal: 24, marginTop: 24, shadowColor: '#000', shadowOpacity: 0.1, shadowRadius: 8, elevation: 4 },
    bubbleText: { textAlign: 'center', fontSize: 18, fontWeight: 'bold', color: '#2A9D8F' },
});

export default DumplingLoading;
