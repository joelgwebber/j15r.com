navView = require './nav'
title = require './title'

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
      #{title.render()}
    </div>
  </body>
</html>
"""
