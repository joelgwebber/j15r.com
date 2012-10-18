import 'js/web.jsx';


// Client/server protocol.
native __fake__ class ClientReq {
  var msg : string;
  var joinGame : JoinGameReq;
  var createGame : CreateGameReq;
}

native __fake__ class JoinGameReq {
  var gameId : string;
}

native __fake__ class CreateGameReq {
  var name : string;
}

native __fake__ class ClientRsp {
  var msg : string;
  var listGames : ListGamesRsp;
  var joinGame : JoinGameRsp;
  var createGame : CreateGameRsp;
}

native __fake__ class JoinGameRsp {
  var gameId : string;
}

native __fake__ class CreateGameRsp {
  var gameId : string;
}

native __fake__ class ListGamesRsp {
  var games : GameDesc[];
}

native __fake__ class GameDesc {
  var name : string;
  var id : string;
}


class Connection {
  // Time to retry a failed connection, in ms.
  static const RETRY_TIME = 1000;

  var queue = [] : ClientReq[];
  var socket : WebSocket;
  var isConnected : boolean;
  var curClientId = 0;

  function constructor() {
    this.connect();
  }

  function wsopen() : void {
    log('wsopen');
    this.isConnected = true;
    this.pumpQueue();
  }

  function wsclose() : void {
    log('wsclose');
    this.isConnected = false;
    this.socket = null;

    log('retrying websocket...');
    this.reconnect();
  }

  function wsmessage(e : Event) : void {
    var msgEvent = e as MessageEvent;
    var msg = msgEvent.data as string;
    log('wsmessage');

    var rsp = JSON.parse(msg) as __noconvert__ ClientRsp;
    switch (rsp.msg as string) {
    case "joinGame":
      log rsp.joinGame;
      break;
    case "listGames":
      log rsp.listGames;
      break;
    }
  }

  function pushMessage(o : ClientReq) : void {
    this.queue.push(o);
  }

  function nextClientId() : int {
    return ++this.curClientId;
  }

  function connect() : void {
    var url = 'ws://' + dom.window.location.host + '/ws';
    this.socket = new WebSocket(url);
    this.socket.onopen = function(e:Event):void { this.wsopen(); };
    this.socket.onclose = function(e:Event):void { this.wsclose(); };
    this.socket.onmessage = function(e:Event):void { this.wsmessage(e); };
  }

  function clearQueue() : void {
    this.queue = [] : ClientReq[];
  }

  function reconnect() : void {
    this.clearQueue();

    dom.window.setTimeout(function() : void {
      // TODO: whatever's necessary to reconstitute game state.

      // Reconnect to the server.
      this.connect();
    }, Connection.RETRY_TIME);
  }

  function pumpQueue() : void {
    while (this.queue.length > 0) {
      this.socket.send(JSON.stringify(this.queue.shift()));
    }

    if (this.isConnected) {
      dom.window.setTimeout(function() : void {
        this.pumpQueue();
      }, 0);
    }
  }
}

class _Main {
  static function main() : void {
    var conn = new Connection();
  }
}
