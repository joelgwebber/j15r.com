connect = require 'connect'

module.exports.init = (app) ->
  app.static [
    'slides/static',
    'slides/decks',
    'slides/syntax',
    'slides/images',
  ]

  app.client 'slides/client/slides.coffee'
