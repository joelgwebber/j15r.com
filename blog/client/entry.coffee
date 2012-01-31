tmpl = window.tmpl

###
window.onload = () ->
  div = document.createElement 'div'
  div.innerHTML = tmpl
    foo: 'w00t!'
  document.body.appendChild div
###
