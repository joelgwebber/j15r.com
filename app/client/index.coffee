dom = require './dom'

# template test.
div = document.createElement 'div'
div.innerHTML = templates.item
  name: 'foo'
  str: 'w00t!'
document.body.appendChild div

# sharejs editor test.
editor = ace.edit("realeditor");
sharejs.open 'md:hello', 'text', (error, doc) ->
  doc.attach_ace editor

# sharejs json test.
makeForm = dom.id 'make'
objNameInput = dom.id 'objname'

dom.listen makeForm, 'submit', (e) ->
  e.preventDefault()
  sharejs.open objNameInput.value, 'json', (error, doc) ->
    doc.set
      key: 'value'
