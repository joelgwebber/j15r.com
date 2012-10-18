package main

import (
  "code.google.com/p/go.net/websocket"
  "flag"
  "github.com/kellegous/pork"
  "log"
  "net/http"
  "time"
)

// Message types used by the json interface.

const (
  MSG_LISTGAMES  = "listGames"
  MSG_JOINGAME   = "joinGame"
  MSG_CREATEGAME = "createGame"
)

// json request structures.
type clientReq struct {
  Msg        string         `json:"msg"`
  JoinGame   *joinGameReq   `json:"joinGame,omitempty"`
  CreateGame *createGameReq `json:"createGame,omitempty"`
}

type createGameReq struct {
  Name string `json:"name"`
}

type joinGameReq struct {
  GameId string `json:"gameId"`
}

// json response structures.
type clientRsp struct {
  Msg        string         `json:"msg"`
  ListGames  listGamesRsp   `json:"listGames,omitempty"`
  JoinGame   joinGameRsp    `json:"joinGames,omitempty"`
  CreateGame *createGameRsp `json:"createGame,omitempty"`
}

type joinGameRsp struct {
  GameId string `json:"gameId"`
}

type createGameRsp struct {
  GameId string `json:"gameId"`
}

type listGamesRsp struct {
  Games []gameDesc `json:"games"`
}

type gameDesc struct {
  Name string `json:"name"`
  Id   string `json:"id"`
}

// Game structs.
type player struct {
  id string
  ws *websocket.Conn
}

type lobby struct {
  players      map[string]player
  games        map[string]game
  addPlayer    chan player
  removePlayer chan player
}

type game struct {
  id      string
  players map[string]player
}

func (l *lobby) run() {
  log.Printf("Starting lobby")

  for {
    select {
    case player := <-l.addPlayer:
      l.players[player.id] = player
      clientRsp := clientRsp{
        Msg:       MSG_LISTGAMES,
        ListGames: listGamesRsp{},
      }
      clientRsp.ListGames.Games = make([]gameDesc, 0)
      for _, game := range l.games {
        clientRsp.ListGames.Games = append(clientRsp.ListGames.Games, gameDesc{
          Name: "game " + game.id,
          Id:   game.id,
        })
      }
      websocket.JSON.Send(player.ws, clientRsp)

    case player := <-l.removePlayer:
      delete(l.players, player.id)

    // Wake up every once in a while to inform clients of new games.
    case <-time.After(5 * time.Second):
    }
  }
}

var mainLobby *lobby = &lobby{
  players:      make(map[string]player),
  games:        make(map[string]game),
  addPlayer:    make(chan player),
  removePlayer: make(chan player),
}

func main() {
  addr := flag.String("addr", ":8080", "The address to use")
  flag.Parse()

  var dirs []http.Dir
  if flag.NArg() == 0 {
    dirs = []http.Dir{http.Dir(".")}
  } else {
    dirs = make([]http.Dir, flag.NArg())
    for i, arg := range flag.Args() {
      dirs[i] = http.Dir(arg)
    }
  }

  // setup game routines
  game := game{
    id:      "testGame",
    players: make(map[string]player),
  }
  mainLobby.games[game.id] = game
  go mainLobby.run()

  // setup a simple router
  r := pork.NewRouter(func(status int, r *http.Request) {
    log.Printf("%d %s %s %s", status, r.RemoteAddr, r.Method, r.URL.String())
  }, nil, nil)
  r.Handle("/", pork.Content(pork.None, dirs...))
  r.Handle("/ws", websocket.Handler(handleWebSocket))
  http.ListenAndServe(*addr, r)
}

func handleWebSocket(ws *websocket.Conn) {
  log.Printf("connection")

  // Put new players into the lobby.
  player := player{"player-id", ws}
  mainLobby.addPlayer <- player

  for {
    var req clientReq
    err := websocket.JSON.Receive(ws, &req)
    if err != nil {
      log.Printf("err: %v", err)
      ws.Close()
      break
    }

    switch req.Msg {
    case MSG_JOINGAME:
      log.Printf("join: %s", req.JoinGame.GameId)
      // TODO: ...
    case MSG_CREATEGAME:
      log.Printf("join: %s", req.JoinGame.GameId)
      // TODO: ...
    }
  }
}
