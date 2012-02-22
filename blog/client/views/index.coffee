navView = require './nav'

module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>As simple as possible, but no simpler</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    <h1>As simple as possible, but no simpler</h1>
    #{navView.render data.posts}
  </body>
</html>
"""
