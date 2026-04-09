package com.harc.health.logic

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.gson.Gson
import com.harc.health.model.LocalMessage
import com.harc.health.repository.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

/**
 * HARC Health Local-First P2P Chat Engine
 * Uses Google Nearby Connections to bypass the cloud entirely.
 */
class LocalChatManager(private val context: Context) {
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val STRATEGY = Strategy.P2P_CLUSTER // Many-to-many discovery
    private val SERVICE_ID = "com.harc.health.P2P_CHAT"
    private val gson = Gson()
    private val db = AppDatabase.getDatabase(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _discoveredPeers = MutableStateFlow<Map<String, String>>(emptyMap()) // EndpointId -> Name
    val discoveredPeers = _discoveredPeers.asStateFlow()

    private val _connectedPeers = MutableStateFlow<Set<String>>(emptySet())
    val connectedPeers = _connectedPeers.asStateFlow()

    private var myUserName = "Anonymous User"
    private var myUserId = UUID.randomUUID().toString()

    fun startP2P(userName: String, userId: String) {
        myUserName = userName
        myUserId = userId
        startAdvertising()
        startDiscovery()
    }

    private fun startAdvertising() {
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startAdvertising(myUserName, SERVICE_ID, connectionLifecycleCallback, options)
            .addOnSuccessListener { Log.d("P2P", "Advertising started...") }
            .addOnFailureListener { Log.e("P2P", "Advertising failed", it) }
    }

    private fun startDiscovery() {
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        connectionsClient.startDiscovery(SERVICE_ID, endpointDiscoveryCallback, options)
            .addOnSuccessListener { Log.d("P2P", "Discovery started...") }
            .addOnFailureListener { Log.e("P2P", "Discovery failed", it) }
    }

    fun connectToPeer(endpointId: String) {
        connectionsClient.requestConnection(myUserName, endpointId, connectionLifecycleCallback)
    }

    fun sendMessage(content: String, targetEndpointId: String) {
        val message = LocalMessage(
            id = UUID.randomUUID().toString(),
            chatId = targetEndpointId,
            senderId = myUserId,
            senderName = myUserName,
            content = content,
            timestamp = Date(),
            isMe = true
        )
        
        val payload = Payload.fromBytes(gson.toJson(message).toByteArray())
        connectionsClient.sendPayload(targetEndpointId, payload)
        
        // Save locally immediately
        scope.launch {
            db.messageDao().insertMessage(message)
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val current = _discoveredPeers.value.toMutableMap()
            current[endpointId] = info.endpointName
            _discoveredPeers.value = current
        }

        override fun onEndpointLost(endpointId: String) {
            val current = _discoveredPeers.value.toMutableMap()
            current.remove(endpointId)
            _discoveredPeers.value = current
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            // Automatically accept connections for the local "Health Circle"
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                val current = _connectedPeers.value.toMutableSet()
                current.add(endpointId)
                _connectedPeers.value = current
            }
        }

        override fun onDisconnected(endpointId: String) {
            val current = _connectedPeers.value.toMutableSet()
            current.remove(endpointId)
            _connectedPeers.value = current
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            if (payload.type == Payload.Type.BYTES) {
                val json = String(payload.asBytes()!!)
                val message = gson.fromJson(json, LocalMessage::class.java).copy(isMe = false)
                
                scope.launch {
                    db.messageDao().insertMessage(message)
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {}
    }

    fun stopAll() {
        connectionsClient.stopAllEndpoints()
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
    }
}
