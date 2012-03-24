slides = null
notes = null
curSlide = -1
lastShownSlide = null
bugDiv = null
printBug = null
presentBug = null
printMode = false
presentMode = false
notesWindow = null

squash = (evt) ->
  if evt
    evt.preventDefault()
    evt.stopPropagation()

hide = (elem) ->
  s = elem.style
  s.opacity = '0'
  s.visibility = 'hidden'

show = (elem) ->
  s = elem.style
  s.opacity = '1'
  s.visibility = ''

fireOnShow = (slide) ->
  fnName = (slide.getAttribute 'id') + '_onShow'
  if window[fnName]
    (window[fnName] slide)

fireOnHide = (slide) ->
  fnName = (slide.getAttribute 'id') + '_onHide'
  if window[fnName]
    (window[fnName] slide)

hideAllSlides = () ->
  for i in [0...slides.length]
    hide slides[i]

    s = slides[i].style
    s.position = 'absolute'
    s.left = s.top = '1em'

showAllSlides = () ->
  for i in [0...slides.length]
    show slides[i]

    s = slides[i].style
    s.position = ''

hideAllNotes = () ->
  for i in [0...slides.length]
    if notes[i]
      hide notes[i]

showAllNotes = () ->
  for i in [0...slides.length]
    if notes[i]
      show notes[i]

showSlide = (idx) ->
  if printMode || (curSlide == idx) || (idx < 0) || (idx >= slides.length)
    return

  if curSlide != -1
    hide slides[curSlide]
    fireOnHide slides[curSlide]

  curSlide = idx

  if curSlide != -1
    fireOnShow slides[curSlide]
    show slides[curSlide]
    updateNotes()

nextSlide = (evt) ->
  squash evt
  showSlide curSlide + 1

prevSlide = (evt) ->
  squash evt
  showSlide curSlide - 1

updateNotes = () ->
  if presentMode
    html = ''
    if notes[curSlide]
      html = notes[curSlide].innerHTML
    notesWindow.document.body.innerHTML = html

switchPrintMode = (evt) ->
  squash evt

  if !printMode
    showAllSlides()
    printBug.innerHTML = 'done'
    hide presentBug
    fireOnHide slides[curSlide]
    lastShownSlide = curSlide
    curSlide = -1
    printMode = true
  else
    hideAllSlides()
    printBug.innerHTML = 'print'
    show presentBug
    printMode = false
    showSlide lastShownSlide

switchPresentMode = (evt) ->
  squash evt

  if !presentMode
    notesWindow = window.open 'about:blank', '_blank', 'menubar:0,status:0,location:0'
    if !notesWindow
      # Popup likely blocked.
      return

    notesWindow.addEventListener 'unload', () ->
      # If the notes window is closed, while presenting, switch it off.
      if presentMode
        switchPresentMode()
    , false

    hideAllNotes()
    hide bugDiv
    presentMode = true
    document.body.style.overflow = 'hidden'
    updateNotes()
  else
    showAllNotes()
    show bugDiv
    notesWindow.close()
    document.body.style.overflow = 'auto'
    presentMode = false

initBugs = () ->
  bugDiv = document.createElement 'div'
  bugDiv.className = 'bug-container'
  document.body.appendChild bugDiv

addBug = (html, fn) ->
  bug = document.createElement 'div'
  bug.className = 'bug'
  bug.innerHTML = html
  bug.addEventListener 'mousedown', fn, false

  bugDiv.appendChild bug
  return bug

onKeyDown = (evt) ->
  switch  evt.keyCode
    when 32
      if evt.shiftKey
        prevSlide evt
      else
        nextSlide evt
    when 27
      if printMode
        switchPrintMode()
      else
        switchPresentMode()
    when 36 then showSlide 0
    when 35 then showSlide slides.length - 1
    when 37, 38 then prevSlide evt
    when 39, 40 then nextSlide evt

onMouseDown = (evt) ->
  nextSlide evt

main = () ->
  slides = document.querySelectorAll '.slide'
  notes = []
  for i in [0...slides.length]
    notes[i] = slides[i].querySelector '.notes'

  hideAllSlides()
  showSlide 0

  window.addEventListener 'keydown', onKeyDown, false
  window.addEventListener 'mousedown', onMouseDown, false

  initBugs()
  printBug = addBug 'print', switchPrintMode
  presentBug = addBug 'present', switchPresentMode

main()
