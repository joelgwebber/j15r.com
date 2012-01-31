<!DOCTYPE html>
<html>
  <head>
  </head>
  <body>
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
    <script src='/app/client/app.coffee'></script>
  </body>
</html>
