'use strict';

let stompClient = null;
let username = null;
let messageForm;
let sendInputText;
let messageList;
if (document.querySelector('.message-list')) {
    messageList = document.querySelector('.message-list');
    messageList.scrollTop = messageList.scrollHeight;
}
// Подключение к WebSocket
connect();
function connect(event) {
    username = document.querySelector('#username').textContent;
    console.log('Your username is: ' + username);
    if (username) {
        let socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
    console.log(document.title);
    if (document.title === 'Chat') {
        messageForm = document.querySelector('.send-message__form');
        sendInputText = document.querySelector('.message__input');
        messageForm.addEventListener('submit', sendMessage, true);
    }
    // event.preventDefault();
}

// Подключено к серверу
function onConnected() {
    // Подписка на тему
    stompClient.subscribe('/topic/users/' + username, onMessageReceived);
    alert('The user: ' + username + 'is listening for incoming messages');
    // Отправка на сервер информации о новом пользователе
    stompClient.send("/app/onlineUser", {}, JSON.stringify({sender: username, type: 'ONLINE'}));
}

// Ошибка подключения
function onError(error) {
    let connectingElement = document.createElement('div');
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh the page!';
    document.body.appendChild(connectingElement);
}

// Отправка сообщения
function sendMessage(event) {
    let messageContent = sendInputText.value.trim();
    let chatId = window.location.pathname.split('/');
    console.log('Message sent to chat with id: ' + chatId[chatId.length - 1]);
    if (messageContent && stompClient) {
        let chatMessage = {
            type: 'CHAT',
            content: messageContent,
            sender: username,
            chatId: chatId[chatId.length - 1],
        };

        stompClient.send("/app/sendMessage", {}, JSON.stringify(chatMessage));
        sendInputText.value = '';
    }
    event.preventDefault();
}

// Получение сообщений
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);
    console.log('Message received from: ' + message.sender + 'Content: ' + message.content);

    if (message.type === 'JOIN') {
        // messageElement.textContent = message.sender + ' joined the chat!';
    } else if (message.type === 'LEAVE') {
        // messageElement.textContent = message.sender + ' left the chat!';
    } else if (message.type === 'CHAT' && document.title === 'Chat') {
        let currentUserLocation = window.location.pathname.split('/');
        // console.log(typeof currentUserLocation[currentUserLocation.length - 1] + '    ' + typeof message.chatId);
        if (Number(currentUserLocation[currentUserLocation.length - 1]) === message.chatId) {
            let messageItem = document.createElement('li');
            messageList = document.querySelector('.message-list');
            messageItem.textContent = message.sender + ': ' + message.content;
            messageItem.className = ".message-block__person";
            messageList.appendChild(messageItem);
            messageList.scrollTop = messageList.scrollHeight;
        }
    } else {
        // messageElement.textContent = message.sender + ': ' + message.content;
    }

    // messageArea.appendChild(messageElement);
    // messageArea.scrollTop = messageArea.scrollHeight;
}
// usernameForm.addEventListener('submit', connect, true);

