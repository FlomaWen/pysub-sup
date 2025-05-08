'use strict';

let stompClient = null;
let username = null;

function connect(event) {
    username = document.querySelector('#name').value.trim();
    
    if (username) {
        document.querySelector('#username-page').classList.add('d-none');
        document.querySelector('#chat-page').classList.remove('d-none');
        
        const socket = new SockJS('/chat-websocket');
        stompClient = Stomp.over(socket);
        
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/common', onMessageReceived);
    
    // Subscribe to user-specific topic
    stompClient.subscribe('/topic/user/' + username, onMessageReceived);
    
    // Tell your username to the server
    stompClient.send('/app/chat.addUser',
        {},
        JSON.stringify({
            category: 'JOIN',
            target: 'COMMON',
            source: username,
            timestamp: '',
            payload: username + ' joined!'
        })
    );
}

function onError(error) {
    console.log('Could not connect to WebSocket server. Please refresh this page to try again!');
    console.error(error);
}

function sendMessage(event) {
    const messageInput = document.querySelector('#message');
    const messageContent = messageInput.value.trim();
    
    if (messageContent && stompClient) {
        const chatMessage = {
            category: 'CHAT',
            target: 'COMMON',
            source: username,
            timestamp: '',
            payload: messageContent
        };
        
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    
    const messageElement = document.createElement('li');
    messageElement.classList.add('list-group-item', 'message');
    
    if (message.category === 'SYSTEM') {
        messageElement.classList.add('system');
    } else if (message.source === username) {
        messageElement.classList.add('self');
    }
    
    const avatarElement = document.createElement('div');
    avatarElement.classList.add('message-username');
    avatarElement.textContent = message.source;
    messageElement.appendChild(avatarElement);
    
    const textElement = document.createElement('div');
    textElement.classList.add('message-content');
    textElement.textContent = message.payload;
    messageElement.appendChild(textElement);
    
    const timestampElement = document.createElement('div');
    timestampElement.classList.add('message-timestamp');
    timestampElement.textContent = message.timestamp;
    messageElement.appendChild(timestampElement);
    
    document.querySelector('#messageArea').appendChild(messageElement);
    document.querySelector('.message-list-container').scrollTop = document.querySelector('.message-list-container').scrollHeight;
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('#usernameForm').addEventListener('submit', connect, true);
    document.querySelector('#messageForm').addEventListener('submit', sendMessage, true);
});