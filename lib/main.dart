import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: UssdServiceScreen(),
    );
  }
}

class UssdServiceScreen extends StatefulWidget {
  @override
  _UssdServiceScreenState createState() => _UssdServiceScreenState();
}

class _UssdServiceScreenState extends State<UssdServiceScreen> {
  static const platform = MethodChannel('com.example.ussd_access');
  String _status = "Waiting for USSD Response...";
  final TextEditingController _ussdCodeController = TextEditingController(text: "*247#");

  void openAccessibilitySettings() async {
    try {
      await platform.invokeMethod('openAccessibilitySettings');
    } catch (e) {
      print("Failed to open accessibility settings: $e");
    }
  }

  void startUssdSession() async {
    final ussdCode = _ussdCodeController.text.trim();
    try {
      setState(() {
        _status = "Starting USSD session for: $ussdCode";
      });

      final result = await platform.invokeMethod('startUssdSession', {"ussdCode": ussdCode});
      setState(() {
        _status = "USSD Session Result: $result";
      });
    } catch (e) {
      setState(() {
        _status = "Failed to start USSD session: $e";
      });
    }
  }


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('USSD Multi-Session Service')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            ElevatedButton(
              onPressed: openAccessibilitySettings,
              child: Text("Enable Accessibility Service"),
            ),
            SizedBox(height: 20),
            TextField(
              controller: _ussdCodeController,
              decoration: InputDecoration(labelText: "Enter USSD Code"),
            ),
            SizedBox(height: 20),
            ElevatedButton(
              onPressed: startUssdSession,
              child: Text("Start USSD"),
            ),
            SizedBox(height: 20),
            Text(_status),
          ],
        ),
      ),
    );
  }
}
