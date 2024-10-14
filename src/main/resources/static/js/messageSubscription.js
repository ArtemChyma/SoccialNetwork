'use strict';

let usernamePage = document.querySelector('#username-page');
let chatPage = document.querySelector('#chat-page');
let usernameForm = document.querySelector('#usernameForm');
let messageForm = document.querySelector('#messageForm');
let messageInput = document.querySelector('#message');
let messageArea = document.querySelector('#messageArea');
let stompClient = null;
let username = null;

// Подключение к WebSocket
function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        let socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

// Подключено к серверу
function onConnected() {
    // Подписка на тему
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Отправка на сервер информации о новом пользователе
    stompClient.send("/app/addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
}

// Ошибка подключения
function onError(error) {
    let connectingElement = document.createElement('div');
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh the page!';
    document.body.appendChild(connectingElement);
}

// Отправка сообщения
function sendMessage(event) {
    let messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

// Получение сообщений
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    let messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.textContent = message.sender + ' joined the chat!';
    } else if (message.type === 'LEAVE') {
        messageElement.textContent = message.sender + ' left the chat!';
    } else {
        messageElement.textContent = message.sender + ': ' + message.content;
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
