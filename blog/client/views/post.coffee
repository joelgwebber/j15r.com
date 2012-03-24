navView = require './nav'
tmpl = require './tmpl'

module.exports.render = (data) -> """
<!DOCTYPE html>
<html>
  <head>
    <title>#{data.title}</title>
    <link rel="stylesheet" href="/blog/static/site.css">
  </head>
  <body>
    #{navView.render data.posts}
    <div class='body'>
      <h1>As simple as possible, but no simpler</h1>
      <h1>#{data.title}</h1>
      <div>#{data.date}</div>
      <div>#{data.content}</div>
      <div id="disqus_thread"></div>
      <script type="text/javascript">
      disqus_url = 'http://blog.j15r.com/2011/12/for-those-unfamiliar-with-it-box2d-is.html';
      (function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = 'http://j15r.disqus.com/embed.js'; (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();</script>
      <noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
      <a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
    </div>
  </body>
</html>
"""
