italicizedView = require './italicized'

module.exports.render = (data) -> """
#{data.name} : #{italicizedView.render data.italicized}
"""
