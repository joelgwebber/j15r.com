bar = require './bar'

module.exports = (x) ->
    return x * (bar.coeff x) + (x * 3 - 2)

