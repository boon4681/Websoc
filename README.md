# Websoc
Minecraft Plugin that provide socket to Allow you to send command directly to Server

<h4>Default Plugin config</h4>

```yml
Authorization: [Random-UUID]
Port: 3000 # Server Socket Port
Timeout: 1500 # Server Connection Timeout
```

<!-- <h4>Node.js client example</h4>

```js
var net = require('net'), Socket = net.Socket;
var socket = new Socket()
var package = `Authorization: [Authorization_Token]
               Command: kick boon4681`
socket.on('connect', function () {
    socket.write(package)
    socket.end()
});
socket.setTimeout(5000);
socket.on('timeout', function () { socket.destroy(); });
socket.on('error', function (exception) { });
socket.on('close', function (exception) { });
socket.on('data', function (a) {
    console.log(Buffer.from(a).toString("utf-8"))
    socket.destroy();
})
socket.connect(3000, "localhost");
```
 -->
