navView = require './nav'
tmpl = require './tmpl'
title = require './title'

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
      #{title.render()}
      <h2>#{data.title}</h2>

      <div>#{data.date}</div>
      <div>#{data.content}</div>

      <div id="disqus_thread"></div>
      <script type="text/javascript">
      #{tmpl.maybe data.origurl, "var disqus_url = '#{data.origurl}';"}
      (function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = 'http://j15r.disqus.com/embed.js'; (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();</script>
      <noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
      <a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
    </div>
  </body>
</html>
"""
