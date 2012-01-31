# j15r.com site server
http = require 'http'
connect = require 'connect'
browserify = require 'browserify'

slides = require './slides/slides'
blog = require './blog/blog'
testapp = require './app/app'

server = connect.createServer()

server.use '/static', (connect.static __dirname + '/static')

class Wrapper
  constructor: (@app, @server) ->

  static: (paths) ->
    (@server.use '/' + path, connect.static path) for path in paths

  get: (route, handler) ->
    @app.get route, handler

  client: (entry) ->
    bundle = browserify
      entry: entry,
      mount: '/' + entry,
      watch: true
    server.use bundle

server.use connect.router (app) ->
  w = new Wrapper(app, server)

  blog.init w
  slides.init w
  testapp.init w

# Start server
server.listen 2112, '0.0.0.0'
console.log 'listening on port 2112'
