// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.MessagingAnalytics;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;
import java.util.ArrayDeque;
import java.util.Queue;


public class FlutterFirebaseMessagingService extends FirebaseMessagingService {
    private static final Queue<String> recentlyReceivedMessageIds = new ArrayDeque(10);
    
    @Override
    public void onNewToken(@NonNull String token) {
      Intent onMessageIntent = new Intent(FlutterFirebaseMessagingUtils.ACTION_TOKEN);
      onMessageIntent.putExtra(FlutterFirebaseMessagingUtils.EXTRA_TOKEN, token);
      LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(onMessageIntent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
      // Added for commenting purposes;
      // We don't handle the message here as we already handle it in the receiver and don't want to duplicate.
    }

    public void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (!"com.google.android.c2dm.intent.RECEIVE".equals(action) && !"com.google.firebase.messaging.RECEIVE_DIRECT_BOOT".equals(action)) {
            if ("com.google.firebase.messaging.NEW_TOKEN".equals(action)) {
                this.onNewToken(intent.getStringExtra("token"));
            } else {
                Log.d("FirebaseMessaging", "Unknown intent action: " + intent.getAction());
            }
        } else {
            this.handleMessageIntent(intent);
        }

    }

    private void handleMessageIntent(Intent intent) {
        String messageId = intent.getStringExtra("google.message_id");
        if (!this.alreadyReceivedMessage(messageId)) {
            this.passMessageIntentToSdk(intent);
        }

    }

    private void passMessageIntentToSdk(Intent intent) {
        String messageType = intent.getStringExtra("message_type");
        if (messageType == null) {
            messageType = "gcm";
        }

        byte var4 = -1;
        switch(messageType.hashCode()) {
            case -2062414158:
                if (messageType.equals("deleted_messages")) {
                    var4 = 1;
                }
                break;
            case 102161:
                if (messageType.equals("gcm")) {
                    var4 = 0;
                }
                break;
            case 814694033:
                if (messageType.equals("send_error")) {
                    var4 = 3;
                }
                break;
            case 814800675:
                if (messageType.equals("send_event")) {
                    var4 = 2;
                }
        }

        switch(var4) {
            case 0:
                MessagingAnalytics.logNotificationReceived(intent);
                this.dispatchMessage(intent);
                break;
            case 1:
                this.onDeletedMessages();
                break;
            case 2:
                this.onMessageSent(intent.getStringExtra("google.message_id"));
                break;
            default:
                Log.w("FirebaseMessaging", "Received message with unknown type: " + messageType);
        }

    }

    private void dispatchMessage(Intent intent) {
        Bundle data = intent.getExtras();
        if (data == null) {
            data = new Bundle();
        }

        data.remove("androidx.content.wakelockid");
        this.onMessageReceived(new RemoteMessage(data));
    }

    private boolean alreadyReceivedMessage(String messageId) {
        if (TextUtils.isEmpty(messageId)) {
            return false;
        } else if (recentlyReceivedMessageIds.contains(messageId)) {
            Log.d("FirebaseMessaging", "Received duplicate message: " + messageId);
            return true;
        } else {
            if (recentlyReceivedMessageIds.size() >= 10) {
                recentlyReceivedMessageIds.remove();
            }

            recentlyReceivedMessageIds.add(messageId);
            return false;
        }
    }

    private String getMessageId(Intent intent) {
        String messageId = intent.getStringExtra("google.message_id");
        if (messageId == null) {
            messageId = intent.getStringExtra("message_id");
        }

        return messageId;
    }
}
