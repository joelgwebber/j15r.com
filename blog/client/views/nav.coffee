tmpl = require './tmpl'

module.exports.render = (data) ->
  """
  <div class='nav'>
    <a href='/blog/about'>About</a>
    <hr>
    #{tmpl.values renderPost, data}
  </div>
  """

renderPost = (post) ->
  "<a href='/blog/post/#{post.id}'>#{post.title}</a><br>"
