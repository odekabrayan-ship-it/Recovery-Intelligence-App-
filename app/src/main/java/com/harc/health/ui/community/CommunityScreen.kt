@file:OptIn(ExperimentalMaterial3Api::class)

package com.harc.health.ui.community

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harc.health.R
import com.harc.health.model.Chat
import com.harc.health.model.Comment
import com.harc.health.model.Message
import com.harc.health.model.LocalMessage
import com.harc.health.model.User
import com.harc.health.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommunityScreen(viewModel: ChatViewModel = viewModel()) {
    val selectedChat by viewModel.selectedChat.collectAsState()
    val currentUserUsername = viewModel.currentUserUsername
    var selectedMainTab by remember { mutableIntStateOf(0) }
    val mainTabs = listOf(
        stringResource(R.string.community_tab_feed),
        stringResource(R.string.community_tab_inbox)
    )
    
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showSearchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshAllData()
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (selectedChat != null) {
            ChatDetailScreen(
                chat = selectedChat!!,
                viewModel = viewModel,
                onBack = { viewModel.clearSelectedChat() }
            )
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                        CenterAlignedTopAppBar(
                            title = { 
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(stringResource(R.string.community_hub), 
                                        fontWeight = FontWeight.Black, 
                                        letterSpacing = 2.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (currentUserUsername.isNotEmpty()) {
                                        Text(
                                            stringResource(R.string.community_my_username, currentUserUsername),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = { showSearchDialog = true }) {
                                    Icon(Icons.Default.PersonSearch, contentDescription = "Find Peers")
                                }
                            }
                        )
                        TabRow(
                            selectedTabIndex = selectedMainTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            divider = {},
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                                    color = MaterialTheme.colorScheme.primary,
                                    height = 3.dp
                                )
                            }
                        ) {
                            mainTabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedMainTab == index,
                                    onClick = { selectedMainTab = index },
                                    text = { 
                                        Text(title, 
                                            fontSize = 11.sp, 
                                            fontWeight = if(selectedMainTab == index) FontWeight.Black else FontWeight.Medium,
                                            letterSpacing = 0.5.sp
                                        ) 
                                    }
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                    when (selectedMainTab) {
                        0 -> FeedSection(viewModel)
                        1 -> InboxSection(viewModel) { chat ->
                            viewModel.selectChat(chat.id, chat)
                        }
                    }
                }
            }
        }

        if (showSearchDialog) {
            SearchPeersDialog(
                viewModel = viewModel,
                onDismiss = { showSearchDialog = false },
                onStartChat = { user ->
                    viewModel.startPrivateChat(user.id, user.name)
                    showSearchDialog = false
                }
            )
        }
    }
}

@Composable
fun SearchPeersDialog(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit,
    onStartChat: (User) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val result by viewModel.searchResult.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Find Peer", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Enter the exact username or email address of the person you want to chat with.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("username or email...") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.findUser(query) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else if (result != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onStartChat(result!!) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        ListItem(
                            headlineContent = { Text(result?.name ?: "", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text(result?.username?.ifEmpty { "Member" } ?: "Member") },
                            leadingContent = {
                                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(result?.name?.take(1)?.uppercase() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            },
                            trailingContent = {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.primary)
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun FeedSection(viewModel: ChatViewModel) {
    val messages by viewModel.feedMessages.collectAsState()
    val blockedUsers by viewModel.blockedUsers.collectAsState()
    var postText by remember { mutableStateOf("") }

    val filteredMessages = messages.filter { it.senderId !in blockedUsers }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 2.dp, 
            modifier = Modifier.fillMaxWidth().padding(bottom = 1.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(viewModel.currentUserName.take(1).uppercase(), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    TextField(
                        value = postText,
                        onValueChange = { postText = it },
                        placeholder = { Text(stringResource(R.string.community_post_placeholder), style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(28.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = {
                            if (postText.isNotBlank()) {
                                viewModel.sendFeedMessage(postText)
                                postText = ""
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        enabled = postText.isNotBlank()
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, 
                            contentDescription = "Post", 
                            tint = if(postText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredMessages) { message ->
                FeedItem(message = message, viewModel = viewModel)
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun FeedItem(message: Message, viewModel: ChatViewModel) {
    var showComments by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val currentUserId = viewModel.currentUserId
    val isLiked = message.likes.contains(currentUserId)

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Text(message.senderName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(message.senderName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(formatTimestamp(message.timestamp?.toDate() ?: Date()), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", modifier = Modifier.size(18.dp), tint = Color.Gray)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Block User") },
                        onClick = {
                            viewModel.blockUser(message.senderId)
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Report Content") },
                        onClick = { showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(message.content, style = MaterialTheme.typography.bodyLarge, lineHeight = 22.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.toggleLike("feed", message.id) }, modifier = Modifier.size(24.dp)) {
                Icon(
                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text("${message.likes.size}", modifier = Modifier.padding(start = 4.dp), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            
            Spacer(modifier = Modifier.width(24.dp))
            
            IconButton(onClick = { showComments = !showComments }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.AutoMirrored.Outlined.Comment, contentDescription = "Comment", tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            Text("${message.commentCount}", modifier = Modifier.padding(start = 4.dp), style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }

        AnimatedVisibility(visible = showComments) {
            CommentSection(postId = message.id, viewModel = viewModel)
        }
    }
}

@Composable
fun CommentSection(postId: String, viewModel: ChatViewModel) {
    val commentsMap by viewModel.comments.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.loadComments(postId)
    }

    Column(modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) {
        comments.forEach { comment ->
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(comment.senderName.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(8.dp)) {
                    Text(comment.senderName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    Text(comment.content, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Write a comment...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                textStyle = MaterialTheme.typography.bodySmall
            )
            IconButton(onClick = {
                if (commentText.isNotBlank()) {
                    viewModel.addComment(postId, "feed", commentText)
                    commentText = ""
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun InboxSection(viewModel: ChatViewModel, onChatClick: (Chat) -> Unit) {
    val chats by viewModel.chats.collectAsState()
    val currentUserId = viewModel.currentUserId

    if (chats.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No private messages yet.", color = Color.Gray)
                Text("Search for a peer's username or email to start a conversation.", style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(chats) { chat ->
                val otherName = if (chat.isOfficial) {
                    "HARC Announcements"
                } else if (chat.isGroup) {
                    chat.groupName ?: "Group"
                } else {
                    chat.participantNames.filterKeys { it != currentUserId }.values.firstOrNull() ?: "Peer"
                }

                ListItem(
                    headlineContent = { Text(otherName, fontWeight = if(chat.isOfficial) FontWeight.Black else FontWeight.Bold) },
                    supportingContent = { 
                        Text(
                            chat.lastMessage?.ifEmpty { "Start a conversation" } ?: "Start a conversation",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    },
                    leadingContent = {
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = if(chat.isOfficial) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (chat.isOfficial) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.White)
                                } else {
                                    Text(otherName.take(1).uppercase(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                    },
                    trailingContent = {
                        Text(formatTimestamp(chat.lastMessageTimestamp?.toDate() ?: Date()), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    },
                    modifier = Modifier.clickable { onChatClick(chat) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
fun ChatDetailScreen(chat: Chat, viewModel: ChatViewModel, onBack: () -> Unit) {
    val messages by viewModel.messages.collectAsState()
    val currentUserId = viewModel.currentUserId
    var messageText by remember { mutableStateOf("") }
    
    val otherName = if (chat.isOfficial) {
        "HARC Announcements"
    } else if (chat.isGroup) {
        chat.groupName ?: "Group"
    } else {
        chat.participantNames.filterKeys { it != currentUserId }.values.firstOrNull() ?: "Peer"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(otherName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (chat.isOfficial) {
                            Text("Official Channel", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    val isMe = message.senderId == currentUserId
                    MessageBubble(message, isMe)
                }
            }

            val isAdmin by viewModel.isAdmin.collectAsState()
            if (!chat.isOfficial || isAdmin) {
                Surface(tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Message...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp)) {
                    Text(
                        "Only admins can post in this channel.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: LocalMessage, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatTimestamp(message.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

fun formatTimestamp(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}
