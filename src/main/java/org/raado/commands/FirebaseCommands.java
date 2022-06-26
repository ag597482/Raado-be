//package org.raado.commands;
//
//import com.google.firebase.FirebaseOptions;
//import com.google.inject.spi.Message;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class FirebaseCommands {
//
//
//
//    // This registration token comes from the client FCM SDKs.
//    String registrationToken = "YOUR_REGISTRATION_TOKEN";
//
//    // See documentation on defining a message payload.
//    Message message = Message.builder()
//            .putData("score", "850")
//            .putData("time", "2:45")
//            .setToken(registrationToken)
//            .build();
//
//    // Send a message to the device corresponding to the provided
//// registration token.
//    String response = FirebaseMessaging.getInstance().send(message);
//    System.out.println("Successfully sent message: " + response);FirebaseMessagingSnippets.java
//
//
//
//}
