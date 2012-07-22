import 'js.jsx';
import 'js/web.jsx';

class Slides {
  var slides = [] : HTMLElement[];
  var notes = [] : HTMLElement[];
  var curSlide = -1;
  var lastShownSlide : int;
  var bugDiv : HTMLElement;
  var printBug : HTMLElement;
  var presentBug : HTMLElement;
  var notesWindow : Window;
  var printMode = false;
  var presentMode = false;

  function constructor() {
    var slideList = dom.window.document.querySelectorAll('.slide');
    for (var i = 0; i < slideList.length; ++i) {
      this.slides[i] = slideList[i] as HTMLElement;
      this.notes[i] = this.slides[i].querySelector('.notes') as HTMLElement;
    }

    this.hideAllSlides();
    this.showSlide(0);

    dom.window.addEventListener('keydown', (e) -> { this.onKeyDown(e); }, false);
    dom.window.addEventListener('mousedown', (e) -> { this.onMouseDown(e); }, false);

    this.initBugs();
    this.printBug = this.addBug('print', (e) -> { this.switchPrintMode(e); });
    this.presentBug = this.addBug('present', (e) -> { this.switchPresentMode(e); });
  }

  function squash(evt : Event) : void {
    if (evt != null) {
      evt.preventDefault();
      evt.stopPropagation();
    }
  }

  function hide(elem : HTMLElement) : void {
    var s = elem.style;
    s.opacity = '0';
    s.visibility = 'hidden';
  }

  function show(elem : HTMLElement) : void {
    var s = elem.style;
    s.opacity = '1';
    s.visibility = '';
  }

  function fireOnShow(slide : HTMLElement) : void {
    var id = slide.getAttribute('id');
    if (id != null) {
      var fnName = id + '_onShow';
      if (fnName in js.global) {
        var fn = js.global[fnName] as function(:HTMLElement):void;
        fn(slide);
      }
    }
  }

  function fireOnHide(slide : HTMLElement) : void {
    var id = slide.getAttribute('id');
    if (id != null) {
      var fnName = id + '_onHide';
      if (fnName in js.global) {
        var fn = js.global[fnName] as function(:HTMLElement):void;
        fn(slide);
      }
    }
  }

  function hideAllSlides() : void {
    for (var i in this.slides) {
      this.hide(this.slides[i]);

      var s = this.slides[i].style;
      s.position = 'absolute';
      s.left = s.top = '1em';
    }
  }

  function showAllSlides() : void {
    for (var i in this.slides) {
      this.show(this.slides[i]);
      this.slides[i].style.position = '';
    }
  }

  function hideAllNotes() : void {
    for (var i in this.slides) {
      if (this.notes[i] != null) {
        this.hide(this.notes[i]);
      }
    }
  }

  function showAllNotes() : void {
    for (var i in this.slides) {
      if (this.notes[i] != null) {
        this.show(this.notes[i]);
      }
    }
  }

  function showSlide(idx : int) : void {
    if (this.printMode ||
        (this.curSlide == idx) ||
        (idx < 0) ||
        (idx >= this.slides.length)) {
      return;
    }

    if (this.curSlide != -1) {
      this.hide(this.slides[this.curSlide]);
      this.fireOnHide(this.slides[this.curSlide]);
    }

    this.curSlide = idx;

    if (this.curSlide != -1) {
      this.fireOnShow(this.slides[this.curSlide]);
      this.show(this.slides[this.curSlide]);
      this.updateNotes();
    }
  }

  function nextSlide(evt : Event) : void {
    this.squash(evt);
    this.showSlide(this.curSlide + 1);
  }

  function prevSlide(evt : Event) : void {
    this.squash(evt);
    this.showSlide(this.curSlide - 1);
  }

  function updateNotes() : void {
    if (this.presentMode) {
      var html = '';
      if (this.notes[this.curSlide]) {
        html = this.notes[this.curSlide].innerHTML;
      }
      this.notesWindow.document.body.innerHTML = html;
    }
  }

  function switchPrintMode(evt : Event) : void {
    this.squash(evt);

    if (!this.printMode) {
      this.showAllSlides();
      this.printBug.innerHTML = 'done';
      this.hide(this.presentBug);
      this.fireOnHide(this.slides[this.curSlide]);
      this.lastShownSlide = this.curSlide;
      this.curSlide = -1;
      this.printMode = true;
    } else {
      this.hideAllSlides();
      this.printBug.innerHTML = 'print';
      this.show(this.presentBug);
      this.printMode = false;
      this.showSlide(this.lastShownSlide);
    }
  }

  function switchPresentMode(evt : Event) : void {
    this.squash(evt);

    if (!this.presentMode) {
      this.notesWindow = dom.window.open('about:blank', '_blank', 'menubar:0,status:0,location:0');
      if (!this.notesWindow) {
        // Popup likely blocked.
        return;
      }

      this.notesWindow.addEventListener('unload', (e) -> {
        // If the notes window is closed, while presenting, switch it off.
        if (this.presentMode) {
          this.switchPresentMode(e);
        }
      }, false);

      this.hideAllNotes();
      this.hide(this.bugDiv);
      this.presentMode = true;
      dom.window.document.body.style.overflow = 'hidden';
      this.updateNotes();
    } else {
      this.showAllNotes();
      this.show(this.bugDiv);
      this.notesWindow.close();
      dom.window.document.body.style.overflow = 'auto';
      this.presentMode = false;
    }
  }

  function initBugs() : void {
    this.bugDiv = dom.window.document.createElement('div') as HTMLElement;
    this.bugDiv.className = 'bug-container';
    dom.window.document.body.appendChild(this.bugDiv);
  }

  function addBug(html : string, fn : function(:Event):void) : HTMLElement {
    var bug = dom.window.document.createElement('div') as HTMLElement;
    bug.className = 'bug';
    bug.innerHTML = html;
    bug.addEventListener('mousedown', fn, false);

    this.bugDiv.appendChild(bug);
    return bug;
  }

  function onKeyDown(evt : Event) : void {
    var keyEvent = evt as KeyboardEvent;
    switch (keyEvent.keyCode) {
      case 32:
        if (keyEvent.shiftKey) {
          this.prevSlide(evt);
        } else {
          this.nextSlide(evt);
        }
        break;
      case 27:
        if (this.printMode) {
          this.switchPrintMode(evt);
        } else {
          this.switchPresentMode(evt);
        }
        break;
      case 36:
        this.showSlide(0);
        break;
      case 35:
        this.showSlide(this.slides.length - 1);
        break;
      case 37:
      case 38:
        this.prevSlide(evt);
        break;
      case 39:
      case 40:
        this.nextSlide(evt);
        break;
    }
  }

  function onMouseDown(evt : Event) : void {
    this.nextSlide(evt);
  }
}

class _Main {
  static function main() : void {
    new Slides();
  }
}
