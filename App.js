import { StatusBar } from 'expo-status-bar';
import { useEffect } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, NativeModules } from 'react-native';
const { TestModule } = NativeModules;


export default function App() {

	useEffect(() => {
	}, [])

	const initCamera = async () => {
		try {
			let s = await TestModule.initInsta360();
			console.log(s, "hasil java init")
		} catch (e) {
			console.warn(e)
		}

	}

	const connectCamera = async (type) => {
		try {
			if (type == "wifi") {
				TestModule.connectByWifi();
			} else {
				TestModule.connectByUSB();
			}
		} catch (e) {
			console.warn(e)
		}

	}

	return (
		<View style={styles.container}>
			<Text>Insta 360 Connect</Text>
			<View style={{ gap: 10, flexDirection: "row", marginTop: 20 }}>
				<TouchableOpacity style={{ padding: 20, backgroundColor: "gray" }} onPress={() => initCamera()}>
					<Text>INIT</Text>
				</TouchableOpacity>
				<TouchableOpacity style={{ padding: 20, backgroundColor: "gray" }} onPress={() => connectCamera("wifi")}>
					<Text>WIFI</Text>
				</TouchableOpacity>
				<TouchableOpacity style={{ padding: 20, backgroundColor: "gray" }} onPress={() => connectCamera("usb")}>
					<Text>USB</Text>
				</TouchableOpacity>
			</View>
			{/* <TouchableOpacity style={{ padding: 20, backgroundColor: "gray" }} onPress={async () => {
				try {
					// let s = TestModule.getValue();
					// console.log(s, "hasil java")
					// TestModule.createTestEvent('testName', 'testLocation');
					// TestModule.createAlert("Lundara hellow")

					let g = TestModule.getStringValue("Speed");
					console.log("ggg", g)
					// TestModule.getStringValue("Power").then((result) => {
					// 	console.log("OK", result);
					// }).catch((error) => {
					// 	console.error(error);
					// });
				} catch (e) {
					console.warn(e)
				}
			}}>
				<Text>Test</Text>
			</TouchableOpacity> */}
			<StatusBar style="auto" />
		</View >
	);
}

const styles = StyleSheet.create({
	container: {
		flex: 1,
		backgroundColor: '#fff',
		alignItems: 'center',
		justifyContent: 'center',
	},
});
