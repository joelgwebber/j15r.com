module.exports =
  foreach: (fn, list) ->
    (fn item for item in list).join ''
