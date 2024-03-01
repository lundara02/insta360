import { StatusBar } from 'expo-status-bar';
import React, {useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, NativeModules,Alert  } from 'react-native';

const { SupportModule } = NativeModules;

export default function App() {
	const [surfaceViewId, setSurfaceViewId] = useState(null);
  useEffect(() => {
    initSdkCamera();
  }, []);

  const initSdkCamera = async () => {
    try {
      const response = await SupportModule.initSdk();
      console.log(response); 
    } catch (e) {
      console.warn("Error initializing SDK:", e);
    }
  }

  const startCameraPreview = async () => {
    try {
      const response = await SupportModule.startCameraPreview(surfaceViewId,{});
      console.log("Camera preview started:", response);
    } catch (error) {
      console.error("Error starting camera preview:", error);
    }
  }

  const openCameraWifi = async () => {
	try {
		const response = await SupportModule.openCameraWifi(); 
		Alert.alert(response); 
	  } catch (error) {
		console.error(error); 
		Alert.alert('Error', 'Failed to open camera via WiFi');
	  }
  }

  const openCameraUSB = async () => {
    try {
      const response = await SupportModule.openCameraUSB();
      console.log(response); 
	  Alert.alert(response); 
    } catch (e) {
      console.warn("Error opening camera via USB:", e);
    }
  }

  const startCapture = async () => {
    try {
      const result = await SupportModule.startCapture(); //
      Alert.alert('Capture Started', result); 
    } catch (error) {
      console.error(error); 
      Alert.alert('Error', 'Failed to start capture');
    }
  }

  const getCameraConnectedType = async () => {
    try {
      const type = await SupportModule.getCameraConnectedType();
      console.log("Camera connected type:", type); 
    } catch (e) {
      console.warn("Error getting camera connected type:", e);
    }
  }

  const isCameraConnected = async () => {
    try {
      const isConnected = await SupportModule.isCameraConnected();
      console.log("Is camera connected:", isConnected); 
    } catch (e) {
      console.warn("Error checking camera connection:", e);
    }
  }

  const closeCamera = async () => {
    try {
      const response = await SupportModule.closeCamera();
      console.log(response); 
    } catch (e) {
      console.warn("Error closing camera:", e);
    }
  }

  const registerCameraChangedCallback = async () => {
    try {
      const response = await SupportModule.registerCameraChangedCallback();
      console.log(response); 
    } catch (e) {
      console.warn("Error registering camera changed callback:", e);
    }
  }

  const unregisterCameraChangedCallback = async () => {
    try {
      const response = await SupportModule.unregisterCameraChangedCallback();
      console.log(response); 
    } catch (e) {
      console.warn("Error unregistering camera changed callback:", e);
    }
  }

return (
    <View style={styles.container}>
      <Text>Go Thru</Text>
      <TouchableOpacity style={styles.button} onPress={startCameraPreview}>
        <Text>Start Camera Preview</Text>
      </TouchableOpacity>
	  <TouchableOpacity style={styles.button} onPress={openCameraWifi}>
        <Text>Open Camera Wifi</Text>
      </TouchableOpacity>
	  <TouchableOpacity style={styles.button} onPress={openCameraUSB}>
        <Text>Open Camera USB</Text>
      </TouchableOpacity>
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
	container: {
	  flex: 1,
	  backgroundColor: '#fff',
	  alignItems: 'center',
	  justifyContent: 'center',
	},
	button: {
	  marginTop: 20,
	  padding: 10,
	  backgroundColor: 'lightblue',
	  borderRadius: 5,
	},
  });
