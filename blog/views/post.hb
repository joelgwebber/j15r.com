<!DOCTYPE html>
<html>
  <head>
    <title>{{title}}</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    <h1>As simple as possible, but no simpler</h1>
    {{>blog_nav}}
    <h1>{{title}}</h1>
    <div>{{{content}}}</div>
  </body>
</html>
