tmpl = require './tmpl'
nav = require './nav'

module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>As simple as possible, but no simpler</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    #{nav.render()}
    <div class='body'>
      #{renderPosts data.posts}
    </div>
  </body>
</html>
"""

renderPosts = (posts) ->
  s = ''
  for key, post of posts
    # TODO: date headers and all that.
    s += renderPost post
  s

renderPost = (post) ->
  "<a href='/blog/post/#{post.id}'>#{post.title}</a><br>"
