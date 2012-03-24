connect = require 'connect'

module.exports.init = (app) ->
  app.static [
    'slides/static',
    'slides/decks',
  ]

  app.client 'slides/client/slides.coffee'
