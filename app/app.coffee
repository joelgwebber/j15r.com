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

indexView = makeTemplate 'app/views/index.hb'
itemView = makeTemplate 'app/views/item.hb'
italicized = makeTemplate 'app/views/italicized.hb'

module.exports.init = (app) ->
  app.static [ 'app/static' ]

  app.client 'app/client/app.coffee'

  app.get '/app', (req, rsp, next) ->
    data =
      templates: [
        { name: 'item', fn: itemView.precompiled }
      ]
      partials: [
        { name: 'italicized', fn: italicized.precompiled }
      ]
    rsp.end (indexView.render data)
