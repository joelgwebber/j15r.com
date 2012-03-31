# j15r.com site server
fs = require 'fs'
http = require 'http'
connect = require 'connect'
browserify = require 'browserify'
router = require 'route66'

slides = require './slides'
blog = require './blog'
testapp = require './app'

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

w = new Wrapper(router, server)

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

# Last-ditch 404 handler
w.get '.*', (req, rsp) ->
  rsp.status = 404
  rsp.end 'Not found'

# Start server
server.use router 
server.listen 2112, '0.0.0.0'
console.log 'listening on port 2112'
