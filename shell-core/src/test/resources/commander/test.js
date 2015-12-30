var commanderDefinition = {
    code : "demoCmd",
    name : "22",
    desc : "33"
}
function executeCommand(commandSessionId, commandCode, params, response) {
    response.writeMessage("接收到command");
    response.writeMessage("command is executed");
    return 0;
}