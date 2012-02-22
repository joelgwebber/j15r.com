# Test client-server app
connect = require 'connect'
hb = require 'handlebars'
fs = require 'fs'

# Import Views
makeTemplate = (file) ->
  tmpl = fs.readFileSync file, 'utf8'
  return {
    render: (hb.compile tmpl)
    precompiled: (hb.precompile tmpl)
  }

indexView = makeTemplate 'wiki/views/index.hb'
itemView = makeTemplate 'wiki/views/item.hb'

module.exports.init = (app) ->
  app.static [ 'wiki/static' ]

  app.client 'wiki/client/index.coffee'

  app.get '/wiki', (req, rsp, next) ->
    data =
      templates: [
        { name: 'item', fn: itemView.precompiled }
      ]
      partials: [
      ]
    rsp.end (indexView.render data)
