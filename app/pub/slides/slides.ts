module Slides {
  var slides: HTMLElement[] = [];
  var notes: HTMLElement[] = [];
  var curSlide = -1;
  var lastShownSlide: number;
  var bugDiv: HTMLElement;
  var printBug: HTMLElement;
  var presentBug: HTMLElement;
  var notesWindow: Window;
  var printMode = false;
  var presentMode = false;

  export function init() {
    var slideList = window.document.querySelectorAll('.slide');
    for (var i = 0; i < slideList.length; ++i) {
      slides[i] = <HTMLElement>slideList[i];
      notes[i] = <HTMLElement>slides[i].querySelector('.notes');
    }

    hideAllSlides();
    showSlide(0);

    window.addEventListener('keydown', (e) => { onKeyDown(e); }, false);
    window.addEventListener('click', (e) => { onClick(e); }, false);

    initBugs();
    printBug = addBug('print', (e) => { switchPrintMode(e); });
    presentBug = addBug('present', (e) => { switchPresentMode(e); });
  }

  function squash(evt: Event): void {
    if (evt != null) {
      evt.preventDefault();
      evt.stopPropagation();
    }
  }

  function hide(elem: HTMLElement): void {
    var s = elem.style;
    s.opacity = '0';
    s.visibility = 'hidden';
  }

  function show(elem: HTMLElement): void {
    var s = elem.style;
    s.opacity = '1';
    s.visibility = '';
  }

  function fireOnShow(slide: HTMLElement): void {
    var id = slide.getAttribute('id');
    if (id != null) {
      var fnName = id + '_onShow';
      if (fnName in window) {
        var fn = <(HTMLElement)=>void>window[fnName];
        fn(slide);
      }
    }
  }

  function fireOnHide(slide: HTMLElement): void {
    var id = slide.getAttribute('id');
    if (id != null) {
      var fnName = id + '_onHide';
      if (fnName in window) {
        var fn = <(HTMLElement)=>void>window[fnName];
        fn(slide);
      }
    }
  }

  function hideAllSlides(): void {
    for (var i in slides) {
      hide(slides[i]);
      var s = slides[i].style;
      s.position = 'absolute';
      s.left = s.top = '1em';
    }
  }

  function showAllSlides(): void {
    for (var i in slides) {
      show(slides[i]);
      slides[i].style.position = '';
    }
  }

  function hideAllNotes(): void {
    for (var i in slides) {
      if (notes[i] != null) {
        hide(notes[i]);
      }
    }
  }

  function showAllNotes(): void {
    for (var i in slides) {
      if (notes[i] != null) {
        show(notes[i]);
      }
    }
  }

  function showSlide(idx: number): void {
    if (printMode ||
        (curSlide == idx) ||
        (idx < 0) ||
        (idx >= slides.length)) {
      return;
    }

    if (curSlide != -1) {
      hide(slides[curSlide]);
      fireOnHide(slides[curSlide]);
    }

    curSlide = idx;

    if (curSlide != -1) {
      fireOnShow(slides[curSlide]);
      show(slides[curSlide]);
      updateNotes();
    }
  }

  function nextSlide(evt: Event): void {
    squash(evt);
    showSlide(curSlide + 1);
  }

  function prevSlide(evt: Event): void {
    squash(evt);
    showSlide(curSlide - 1);
  }

  function updateNotes(): void {
    if (presentMode) {
      var html = '';
      if (notes[curSlide]) {
        html = notes[curSlide].innerHTML;
      }
      notesWindow.document.body.innerHTML = html;
    }
  }

  function switchPrintMode(evt: Event): void {
    squash(evt);

    if (!printMode) {
      showAllSlides();
      printBug.innerHTML = 'done';
      hide(presentBug);
      fireOnHide(slides[curSlide]);
      lastShownSlide = curSlide;
      curSlide = -1;
      printMode = true;
    } else {
      hideAllSlides();
      printBug.innerHTML = 'print';
      show(presentBug);
      printMode = false;
      showSlide(lastShownSlide);
    }
  }

  function switchPresentMode(evt: Event): void {
    squash(evt);

    if (!presentMode) {
      notesWindow = window.open('about:blank', '_blank', 'menubar:0,status:0,location:0');
      if (!notesWindow) {
        // Popup likely blocked.
        return;
      }

      notesWindow.addEventListener('unload', (e) => {
        // If the notes window is closed, while presenting, switch it off.
        if (presentMode) {
          switchPresentMode(e);
        }
      }, false);

      hideAllNotes();
      hide(bugDiv);
      presentMode = true;
      window.document.body.style.overflow = 'hidden';
      updateNotes();
    } else {
      showAllNotes();
      show(bugDiv);
      notesWindow.close();
      window.document.body.style.overflow = 'auto';
      presentMode = false;
    }
  }

  function initBugs(): void {
    bugDiv = <HTMLElement>window.document.createElement('div');
    bugDiv.className = 'bug-container';
    window.document.body.appendChild(bugDiv);
  }

  function addBug(html: string, fn: (Event)=>void): HTMLElement {
    var bug = <HTMLElement>window.document.createElement('div');
    bug.className = 'bug';
    bug.innerHTML = html;
    bug.addEventListener('mousedown', fn, false);

    bugDiv.appendChild(bug);
    return bug;
  }

  function onKeyDown(evt: Event): void {
    var keyEvent = <KeyboardEvent>evt;
    switch (keyEvent.keyCode) {
      case 32:
        if (keyEvent.shiftKey) {
          prevSlide(evt);
        } else {
          nextSlide(evt);
        }
        break;
      case 27:
        if (printMode) {
          switchPrintMode(evt);
        } else {
          switchPresentMode(evt);
        }
        break;
      case 36:
        showSlide(0);
        break;
      case 35:
        showSlide(slides.length - 1);
        break;
      case 37:
      case 38:
        prevSlide(evt);
        break;
      case 39:
      case 40:
        nextSlide(evt);
        break;
    }
  }

  function onClick(evt: Event): void {
    var mouse = <MouseEvent>evt;
    if (mouse.clientX < 480) {
      prevSlide(evt);
    } else {
      nextSlide(evt);
    }
  }
}

Slides.init();
