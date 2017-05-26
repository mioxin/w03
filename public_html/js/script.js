'use strict'
var ws;
    function log(html) {
      document.getElementById('log').innerHTML += html+"<br>";
    }

//    document.forms.signin.onsubmit = action;
    document.getElementById("submit").onclick = action;

    function action() {
      var fm = new FormData(document.forms.signin);
      var xhr = new XMLHttpRequest();
      // обработчики можно объединить в один,
      // если status == 200, то это успех, иначе ошибка
      xhr.onload = xhr.onerror = function() {
        if (this.status == 200) {
          log("success: "+ xhr.responseText);
        } else {
          log("error: " + this.status);
        }
      };

      xhr.open("POST", "/api/v1/sessions", true);
      xhr.send(fm);

    }

function init() {
    ws = new WebSocket("ws://localhost:8080/chat");
    ws.onopen = function (event) {

    }
    ws.onmessage = function (event) {
        var $textarea = document.getElementById("messages");
        $textarea.value = $textarea.value + event.data + "\n";
    }
    ws.onclose = function (event) {

    }
};

function sendMessage() {
    var messageField = document.getElementById("message");
    var userNameField = document.getElementById("username");
    var message = userNameField.value + ":" + messageField.value;
    ws.send(message);
    messageField.value = '';
}
