# j15r.com site server
fs = require 'fs'
http = require 'http'
connect = require 'connect'
browserify = require 'browserify'

shareserver = require './shareserver'

slides = require './slides'
blog = require './blog'
testapp = require './app'
wiki = require './wiki'

server = connect.createServer()

server.use '/static', (connect.static __dirname + '/static')

class Wrapper
  constructor: (@app, @server) ->

  get: (route, handler) ->
    @app.get route, handler

  post: (route, handler) ->
    @app.post route, handler

  static: (paths) ->
    (@server.use '/' + path, connect.static path) for path in paths

  client: (entry) ->
    bundle = browserify
      entry: entry,
      mount: '/' + entry,
      watch: true
    server.use bundle

server.use connect.router (app) ->
  w = new Wrapper(app, server)

  indexHandler = (req, rsp, next) ->
    fs.readFile __dirname + '/index.html', 'utf8', (err, data) ->
      if err
        rsp.statusCode = 500
        rsp.end err
      else
        rsp.statusCode = 200
        rsp.end data

  w.get '/', indexHandler
  w.get '/index.html', indexHandler

  blog.init w
  slides.init w
  testapp.init w
  wiki.init w
  shareserver.init w, server

# Start server
server.listen 2112, '0.0.0.0'
console.log 'listening on port 2112'
