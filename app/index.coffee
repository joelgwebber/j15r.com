# Test client-server app
connect = require 'connect'
hb = require 'handlebars'
fs = require 'fs'

# Import Views
indexView = require './client/views/index'
itemView = require './client/views/item'
italicized = require './client/views/italicized'

module.exports.init = (app) ->
  app.static [ 'app/static' ]

  app.client 'app/client/index.coffee'

  app.get '/app', (req, rsp, next) ->
    data = {}
    rsp.end (indexView.render data)
