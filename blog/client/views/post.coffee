navView = require './nav'

module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>#{data.title}</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    <h1>As simple as possible, but no simpler</h1>
    #{navView.render data.posts}
    <h1>#{data.title}</h1>
    <div>#{data.content}</div>
  </body>
</html>
"""
