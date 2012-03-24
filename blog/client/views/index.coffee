navView = require './nav'

module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>As simple as possible, but no simpler</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    #{navView.render data.posts}
    <div class='body'>
      <h1>As simple as possible, but no simpler</h1>
    </div>
  </body>
</html>
"""
