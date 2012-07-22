module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>Test app</title>
    <link rel='stylesheet' href='/app/static/app.css'>
  </head>
  <body>
    <div id='editor'>
      <div id='realeditor'></div>
    </div>

    <form id='make'>
      <div>Name: <input type='text' id='objname'></input></div>
      <input type='submit' value='make'></input>
    </form>

    <!-- Non-require() Dependencies
         (hate that I can't use require() deps for these - there should just be one script) -->
    <script src="http://ajaxorg.github.com/ace/build/src/ace.js"></script>
    <script src="/static/socket.io.js"></script>
    <script src="/static/share.uncompressed.js"></script>
    <script src="/static/json.uncompressed.js"></script>
    <script src="/static/share-ace.js"></script>

    <!-- App code -->
    <script src='/app/client/index.coffee'></script>
  </body>
</html>
"""
