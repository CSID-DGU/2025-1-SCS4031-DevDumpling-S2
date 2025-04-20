import React from 'react';
import { ActivityIndicator, StyleSheet, View, Dimensions } from 'react-native';
import { useLoadingContext } from '../../contexts/LoadingContext';

const { width, height } = Dimensions.get('window');

const Loading = () => {
    const { isLoading } = useLoadingContext();

    if (!isLoading) return null;

    return (
        <View style={styles.container}>
            <View style={styles.overlay}>
                <ActivityIndicator size="large" color="#014029" />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        position: 'absolute',
        left: 0,
        top: 0,
        width: width,
        height: height,
        zIndex: 10000,
        elevation: 10, // Android에서 zIndex와 함께 사용
    },
    overlay: {
        flex: 1,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        alignItems: 'center',
        justifyContent: 'center',
    },
});

export default Loading;