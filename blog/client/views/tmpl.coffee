module.exports =
  foreach: (fn, list) ->
    (fn item for item in list).join ''

  values: (fn, map) ->
    (fn value for key, value of map).join ''

  maybe: (bool, str) ->
    return str if bool
    ''
