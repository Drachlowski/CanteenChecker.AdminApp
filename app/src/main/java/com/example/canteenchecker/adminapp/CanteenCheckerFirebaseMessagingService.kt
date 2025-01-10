package com.example.canteenchecker.adminapp

import android.util.Log
import com.example.canteenchecker.adminapp.core.sendCanteenChangedBroadcast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val CANTEEN_CHANGED_INTENT_ACTION = "CanteenChanged"

class CanteenCheckerFirebaseMessagingService : FirebaseMessagingService() {


    companion object{
        private const val REMOTE_MESSAGE_CANTEEN_ID_KEY = "canteenId"
    }

    override fun onNewToken(token: String) {
        // nothing to do since app does not use token based messaging
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[REMOTE_MESSAGE_CANTEEN_ID_KEY]?.let {
            sendCanteenChangedBroadcast(it)
        }
    }
}