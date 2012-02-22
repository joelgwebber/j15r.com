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

    <!-- Handlebars template stuff -->
    <script src='/static/handlebars.runtime.js'></script>
    <script>
    var templates = {
      {{#each templates}}
        {{name}} : Handlebars.template({{{fn}}})
      {{/each}}
    };
    {{#each partials}}
      Handlebars.registerPartial('{{name}}', Handlebars.template({{{fn}}}));
    {{/each}}
    </script>

    <!-- Non-require() Dependencies
         (hate that I can't use require() deps for these - there should just be one script) -->
    <script src="http://ajaxorg.github.com/ace/build/src/ace.js"></script>
    <script src="/static/socket.io.js"></script>
    <script src="/static/share.uncompressed.js"></script>
    <script src="/static/json.uncompressed.js"></script>
    <script src="/static/share-ace.js"></script>

    <!-- App code -->
    <script src='/wiki/client/index.coffee'></script>
  </body>
</html>
