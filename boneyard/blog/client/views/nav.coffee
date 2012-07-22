tmpl = require './tmpl'

module.exports.render = (data) ->
  """
  <div class='nav'>
  	<div class='nav-left'>
  	  <a class='navlink' href='/blog'>Home</a>
  	  <a class='navlink' href='/blog/about'>About</a>
  	</div>
  	<div class='nav-right'>
	  <h1>as simple as possible, but no simpler.</h1>
	</div>
  </div>
  """
