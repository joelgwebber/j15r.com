http = require 'http'
connect = require 'connect'

server = connect.createServer()

makeProxyReq = (host, port, url, req, rsp) ->
  proxy = http.createClient port, host
  preq = proxy.request req.method, url, req.headers

  console.log 'req: ' + url

  preq.addListener 'response', (proxyRsp) ->
    proxyRsp.addListener 'data', (chunk) ->
      rsp.write chunk, 'binary'
    proxyRsp.addListener 'end', () ->
      rsp.end()
    rsp.writeHead proxyRsp.statusCode, proxyRsp.headers

  req.addListener 'data', (chunk) ->
    preq.write chunk, 'binary'

  req.addListener 'end', () ->
    preq.end()

# Simple example: make proxy request to localhost:2113.
server.use connect.router (app) ->
  app.get '*', (req, rsp, next) ->
    makeProxyReq 'localhost', 2113, (req.url.substring '/blog'.length), req, rsp

server.listen 2112
console.log 'listening on port 2112'
