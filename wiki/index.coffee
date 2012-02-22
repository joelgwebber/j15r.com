# Test client-server app
connect = require 'connect'
hb = require 'handlebars'
fs = require 'fs'

# Import Views
indexView = require './views/index.coffee'
itemView = require './views/item.coffee'

module.exports.init = (app) ->
  app.static [ 'wiki/static' ]

  app.client 'wiki/client/index.coffee'

  app.get '/wiki', (req, rsp, next) ->
    data = {}
    rsp.end (indexView.render data)
