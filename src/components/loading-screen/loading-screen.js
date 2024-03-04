import { View, Text, StyleSheet, ActivityIndicator } from 'react-native'
import React from 'react'

export default function LoadingScreen({
    show = false,
    caption = null
}) {

    if (!show) return null;

    return (
        <View style={styles.container}>
            <ActivityIndicator size="large" color="#fff" />
            <Text style={{ color: "#fff", marginTop: 10 }}>{caption}</Text>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'rgba(0,0,0,0.5)',
        alignItems: 'center',
        justifyContent: 'center',
        position: "absolute",
        zIndex: 1,
        width: "100%",
        height: "100%"
    },
});