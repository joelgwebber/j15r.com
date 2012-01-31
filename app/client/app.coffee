div = document.createElement 'div'

div.innerHTML = templates.item
  name: 'foo'
  str: 'w00t!'

document.body.appendChild div
