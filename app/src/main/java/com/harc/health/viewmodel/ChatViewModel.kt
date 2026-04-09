package com.harc.health.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harc.health.logic.EncryptionManager
import com.harc.health.logic.LocalChatManager
import com.harc.health.logic.SessionManager
import com.harc.health.model.Chat
import com.harc.health.model.Comment
import com.harc.health.model.Message
import com.harc.health.model.LocalMessage
import com.harc.health.model.User
import com.harc.health.repository.LocalRepository
import com.harc.health.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val localRepository = LocalRepository(application)
    private val sessionManager = SessionManager(application)
    private val encryptionManager = EncryptionManager(application)
    private val localChatManager = LocalChatManager(application)
    private val chatRepository = ChatRepository()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _messages = MutableStateFlow<List<LocalMessage>>(emptyList())
    val messages: StateFlow<List<LocalMessage>> = _messages.asStateFlow()

    private val _feedMessages = MutableStateFlow<List<Message>>(emptyList())
    val feedMessages: StateFlow<List<Message>> = _feedMessages.asStateFlow()

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

    private val _blockedUsers = MutableStateFlow<List<String>>(emptyList())
    val blockedUsers: StateFlow<List<String>> = _blockedUsers.asStateFlow()

    private val _searchResult = MutableStateFlow<User?>(null)
    val searchResult: StateFlow<User?> = _searchResult.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)
    private var messagesJob: Job? = null
    
    private val _selectedChat = MutableStateFlow<Chat?>(null)
    val selectedChat: StateFlow<Chat?> = _selectedChat.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: StateFlow<User?> = _currentUserProfile.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    val discoveredPeers = localChatManager.discoveredPeers
    val connectedPeers = localChatManager.connectedPeers

    val currentUserId: String get() = sessionManager.getDeviceId() ?: "anonymous"
    val currentUserName: String get() = _currentUserProfile.value?.name ?: "User"
    val currentUserUsername: String get() = _currentUserProfile.value?.username ?: ""

    init {
        observeCurrentUserProfile()
        observeFeed()
        observeBlockedUsers()
        checkAdminStatus()
    }

    private fun observeCurrentUserProfile() {
        viewModelScope.launch {
            localRepository.getUserProfileFlow(currentUserId)
                .catch { Log.e("ChatViewModel", "Error observing user profile", it) }
                .collect { profile ->
                    _currentUserProfile.value = profile
                    profile?.let {
                        localChatManager.startP2P(it.name, it.id)
                        // Sync profile to ChatRepository for searching
                        chatRepository.saveUserProfile(it, encryptionManager.getPublicKey())
                    }
                }
        }
    }

    private fun observeFeed() {
        viewModelScope.launch {
            chatRepository.getFeedMessages().collect {
                _feedMessages.value = it
            }
        }
    }

    private fun observeBlockedUsers() {
        viewModelScope.launch {
            chatRepository.getBlockedUsers(currentUserId).collect {
                _blockedUsers.value = it
            }
        }
    }

    private fun checkAdminStatus() {
        viewModelScope.launch {
            _isAdmin.value = chatRepository.isUserAdmin(currentUserId)
        }
    }

    fun refreshAllData() {
        observeFeed()
        observeBlockedUsers()
        checkAdminStatus()
    }

    fun findUser(query: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _searchResult.value = chatRepository.findUserByQuery(query)
            _isSearching.value = false
        }
    }

    fun startPrivateChat(otherId: String, otherName: String) {
        viewModelScope.launch {
            try {
                val otherPublicKey = chatRepository.getUserPublicKey(otherId) ?: ""
                val myPublicKey = encryptionManager.getPublicKey()
                val chatId = chatRepository.getOrCreatePrivateChat(
                    currentUserId, currentUserName, myPublicKey,
                    otherId, otherName, otherPublicKey
                )
                selectChat(chatId)
            } catch (e: Exception) {
                _error.value = "Failed to start chat: ${e.message}"
            }
        }
    }

    fun selectChat(chatId: String, chat: Chat? = null) {
        _currentChatId.value = chatId
        _messages.value = emptyList()
        messagesJob?.cancel()
        
        messagesJob = viewModelScope.launch {
            localRepository.getLocalMessagesFlow(chatId).collect { _messages.value = it }
        }
        
        _selectedChat.value = chat ?: _chats.value.find { it.id == chatId }
    }

    fun sendFeedMessage(content: String) {
        viewModelScope.launch {
            try {
                chatRepository.sendFeedMessage(currentUserId, currentUserName, content)
            } catch (e: Exception) {
                _error.value = "Failed to post: ${e.message}"
            }
        }
    }

    fun toggleLike(collectionPath: String, messageId: String) {
        viewModelScope.launch {
            try {
                chatRepository.toggleLike(collectionPath, messageId, currentUserId)
            } catch (e: Exception) {
                _error.value = "Failed to like: ${e.message}"
            }
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            chatRepository.getComments(postId).collect { commentList ->
                val currentComments = _comments.value.toMutableMap()
                currentComments[postId] = commentList
                _comments.value = currentComments
            }
        }
    }

    fun addComment(postId: String, collectionPath: String, content: String) {
        viewModelScope.launch {
            try {
                chatRepository.addComment(postId, collectionPath, currentUserId, currentUserName, content)
            } catch (e: Exception) {
                _error.value = "Failed to comment: ${e.message}"
            }
        }
    }

    fun blockUser(targetId: String) {
        viewModelScope.launch {
            try {
                chatRepository.blockUser(currentUserId, targetId)
            } catch (e: Exception) {
                _error.value = "Failed to block: ${e.message}"
            }
        }
    }
    
    fun connectToPeer(endpointId: String) {
        localChatManager.connectToPeer(endpointId)
    }

    fun sendMessage(content: String, targetEndpointId: String? = null) {
        val endpointId = targetEndpointId ?: _currentChatId.value ?: return
        localChatManager.sendMessage(content, endpointId)
    }

    fun clearSelectedChat() {
        _selectedChat.value = null
        _currentChatId.value = null
        messagesJob?.cancel()
        _messages.value = emptyList()
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        localChatManager.stopAll()
    }
}
