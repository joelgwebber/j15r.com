module.exports =
  id: (id) ->
    document.getElementById id

  query: (selector) ->
    document.querySelectorAll selector

  listen: (elem, type, fn) ->
    elem.addEventListener type, fn, false

  capture: (elem, type, fn) ->
    elem.addEventListener type, fn, true
