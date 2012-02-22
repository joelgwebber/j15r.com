dom = require './dom'

# sharejs editor test.
editor = ace.edit("realeditor");
sharejs.open 'text:wiki:hello', 'text', (error, doc) ->
  doc.attach_ace editor
