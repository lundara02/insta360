import { StatusBar } from 'expo-status-bar';
import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, NativeModules, Alert } from 'react-native';
import LoadingScreen from './src/components/loading-screen/loading-screen';

const { SupportModule } = NativeModules;

export default function App() {
	const [surfaceViewId, setSurfaceViewId] = useState(null);

	const [intervalActive, setIntervalActive] = useState(false);
	const [isConnected, setIsConnected] = useState(false);
	const [isLoading, setIsLoading] = useState(false);
	const [loadingCaption, setLoadingCaption] = useState('');

	useEffect(() => {
		initSdkCamera();
	}, []);

	const initSdkCamera = async () => {
		try {
			const response = await SupportModule.initSdk();
		} catch (e) { }
	}

	const startCameraPreview = async () => {
		try {
			const response = await SupportModule.startCameraPreview(surfaceViewId, {});
			Alert.alert('Success', 'Camera preview started: ' + response);
		} catch (error) {
			Alert.alert('Error', 'Error starting camera preview: ' + error);
		}
	}

	const openCameraWifi = async () => {
		try {
			setIsLoading(true);
			setLoadingCaption('Connecting to camera...');
			const response = await SupportModule.openCameraWifi();
			checkCameraConnectionInterval()
		} catch (error) {
			Alert.alert('Error', 'Failed to open camera via WiFi');
		}
	}

	const checkCameraConnectionInterval = () => {
		setIntervalActive(true);
		const timeout = setTimeout(() => {
			if (!intervalActive) {
				Alert.alert("Error", "Failed to connect camera: timeout")

				setIsLoading(false);
				setLoadingCaption('');
				clearInterval(interval);
			}
		}, 12000);
		const interval = setInterval(async () => {
			const check = await SupportModule.isCameraConnected();
			if (check === true) {
				clearTimeout(timeout);
				setIntervalActive(false);
				setIsConnected(true);
				setIsLoading(false);
				setLoadingCaption('');
				clearInterval(interval);
				Alert.alert("Success", "Camera connected")
			}
		}, 1000);
	}

	const openCameraUSB = async () => {
		try {
			const response = await SupportModule.openCameraUSB();
			Alert.alert(response);
		} catch (e) { }
	}

	const startCapture = async () => {
		try {
			const result = await SupportModule.startNormalCapture(true);
			Alert.alert('Capture Started', result);
		} catch (error) {
			Alert.alert('Error', 'Failed to start capture');
		}
	}

	const startCaptureHDR = async () => {
		try {
			const result = await SupportModule.startHDRCapture(true);
			Alert.alert('Capture HDR Started', result);
		} catch (error) {
			Alert.alert('Error', 'Failed to start capture');
		}
	}

	const getCameraConnectedType = async () => {
		try {
			const type = await SupportModule.getCameraConnectedType();
			let cameraType = type == 2 ? "WIFI" : "USB";
			alert("Camera connected type: " + cameraType);
		} catch (e) { }
	}

	const isCameraConnected = async () => {
		try {
			const isConnected = await SupportModule.isCameraConnected();
		} catch (e) { }
	}

	const closeCamera = async () => {
		try {
			const response = await SupportModule.closeCamera();
		} catch (e) { }
	}

	const registerCameraChangedCallback = async () => {
		try {
			const response = await SupportModule.registerCameraChangedCallback();
		} catch (e) { }
	}

	const unregisterCameraChangedCallback = async () => {
		try {
			const response = await SupportModule.unregisterCameraChangedCallback();
		} catch (e) { }
	}

	const calibrateGyro = async () => {
		try {
			const response = await SupportModule.calibrateGyro();
			Alert.alert(response);
		} catch (e) {
			Alert.alert('Error', 'Failed to calibrate gyro');
		}
	}

	const formatStorage = async () => {
		try {
			const response = await SupportModule.formatStorage();
			Alert.alert(response);
		} catch (e) {
			Alert.alert('Error', 'Failed to format storage');
		}
	}

	// Camera status
	const callStatusCamera = () => {
		SupportModule.onCameraStatusChanged(true);
		SupportModule.onCameraConnectError(123);
		SupportModule.onCameraSDCardStateChanged(true);
		// SupportModule.onCameraStorageChanged(1024, 2048);
		SupportModule.onCameraBatteryLow();
		SupportModule.onCameraBatteryUpdate(50, false);
	}

	const getPhotos = () => {
		try {
			SupportModule.getPhotos();
			setTimeout(() => {
				SupportModule.setData();
			}, 5000)
		} catch (e) {

		}
	}

	return (
		<View style={styles.container}>
			<LoadingScreen show={isLoading} caption={loadingCaption} />
			<Text>Please connect to camera WIFI first</Text>
			<TouchableOpacity style={styles.button} onPress={openCameraWifi}>
				<Text>Connect using Wifi</Text>
			</TouchableOpacity>

			<TouchableOpacity style={styles.button} onPress={getCameraConnectedType}>
				<Text>Check connected type</Text>
			</TouchableOpacity>

			<View style={{ marginTop: 20, height: 2, width: "100%", backgroundColor: "#ededed" }}></View>
			<TouchableOpacity
				style={[styles.button, {
					backgroundColor: isConnected ? 'lightblue' : '#ededed'
				}]}
				onPress={startCaptureHDR}
			>
				<Text>Capture</Text>
			</TouchableOpacity>
			{/* <TouchableOpacity
				style={[styles.button, {
					backgroundColor: 'lightblue'
				}]}
				onPress={getPhotos}
			>
				<Text>List Photos</Text>
			</TouchableOpacity> */}
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
		backgroundColor: 'lightblue',
		marginTop: 20,
		padding: 10,
		borderRadius: 5,
	},
});
