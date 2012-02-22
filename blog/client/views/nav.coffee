tmpl = require './tmpl'

module.exports.render = (data) ->
  """
  <a href='/blog/about'>About</a>
  <hr>
  #{tmpl.foreach renderPost, data}
  """

renderPost = (post) ->
  "<a href='/blog/post/#{post.id}'>#{post.title}</a><br>"
