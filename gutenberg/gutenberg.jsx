import 'js/web.jsx';

class Reader {
  static const PAGE_SIZE = 1024;
  static const TOUCH_CLICK_DIST_2 = 8 * 8;
  static const BOOKMARK_LAST = 'last';

  var _bookId : int;
  var _pageElem : HTMLElement;
  var _hiddenElem : HTMLElement;
  var _position : int;
  var _endPosition : int;
  var _touchStartX : int;
  var _touchStartY : int;

  function constructor(bookId : int) {
    this._bookId = bookId;

    var doc = dom.window.document;
    this._pageElem = doc.createElement('div') as HTMLElement;
    this._pageElem.className = 'textFormat page';
    this._hiddenElem = doc.createElement('div') as HTMLElement;
    this._hiddenElem.className = 'textFormat';
    this._hiddenElem.style.setProperty('visibility', 'hidden');
    doc.body.appendChild(this._hiddenElem);
    doc.body.appendChild(this._pageElem);

    // Hook up events.
    dom.window.onkeydown = (e) -> { this._onKeyDown(e as KeyboardEvent); };
    dom.window.onresize = (e) -> { this._onResize(); }; // TODO: Detect font-size change.

    this._pageElem.onmousedown = (e) -> { var me = e as MouseEvent; this._onTouchStart(me.clientX, me.clientY); };
    this._pageElem.onmouseup = (e) -> { var me = e as MouseEvent; this._onTouchEnd(me.clientX, me.clientY); };
    this._pageElem.addEventListener('touchstart', (e) -> {var te = e as TouchEvent; this._onTouchStart(te.touches[0].clientX, te.touches[0].clientY); }, false);
    this._pageElem.addEventListener('touchend', (e) -> {var te = e as TouchEvent; this._onTouchEnd(te.touches[0].clientX, te.touches[0].clientY); }, false);

    this._loadBookmark(Reader.BOOKMARK_LAST);
  }

  function _onResize() : void {
    this._setPosition(this._position);
  }

  function _onKeyDown(e : KeyboardEvent) : void {
    switch (e.keyCode) {
      case 32: {
        if (e.shiftKey) {
          this._prevPage();
        } else {
          this._nextPage();
        }
        break;
      }
      case 37:
      case 38: {
        this._prevPage();
        break;
      }
      case 39:
      case 40: {
        this._nextPage();
        break;
      }
    }
  }

  function _onTouchStart(x : int, y : int) : void {
    this._touchStartX = x;
    this._touchStartY = y;
  }

  function _onTouchEnd(x : int, y : int) : void {
    var dx = x - this._touchStartX, dy = y - this._touchStartY;

    if (dx * dx + dy * dy < Reader.TOUCH_CLICK_DIST_2) {
      var w = dom.window.innerWidth;
      if (x < w / 4) {
        this._prevPage();
      } else if (x > w - w / 4) {
        this._nextPage();
      }
    }
  }

  function _prevPage() : void {
    this._setEndPosition(this._position);
  }

  function _nextPage() : void {
    this._setPosition(this._endPosition);
  }

  function _setPosition(pos : int) : void {
    // Keep in bounds.
    // TODO: Need a book manifest to know the upper bound.
    this._position = pos;
    if (this._position < 0) this._position = 0;

    // Fetch the new page
    var page = Math.floor(this._position / Reader.PAGE_SIZE);
    this._fetch(this._bookId, page, 2, function(text : string) : void {
      var offset = this._position - (page * Reader.PAGE_SIZE);
      assert offset >= 0 && offset < Reader.PAGE_SIZE * 2;

      var words = text.split(' ');
      var wordCount = this._pageSize(words, offset, false);

      this._pageElem.innerHTML = words.slice(offset, offset + wordCount).join(' ');
      this._endPosition = this._position + wordCount;

      this._storeBookmark(Reader.BOOKMARK_LAST);
    });
  }

  function _setEndPosition(pos : int) : void {
    // Keep in bounds.
    // TODO: Need a book manifest to know the upper bound.
    this._endPosition = pos;
    if (this._endPosition < 0) {
      this._setPosition(0);
      return;
    }

    // Fetch the new page
    var page = Math.floor(this._endPosition / Reader.PAGE_SIZE) - 1;
    if (page < 0) page = 0;
    this._fetch(this._bookId, page, 2, function(text : string) : void {
      var offset = this._endPosition - (page * Reader.PAGE_SIZE);
      assert offset >= 0 && offset < Reader.PAGE_SIZE * 2;

      var words = text.split(' ');
      var wordCount = this._pageSize(words, offset, true);

      this._pageElem.innerHTML = words.slice(offset - wordCount, offset).join(' ');
      this._position = this._endPosition - wordCount;

      // Quick hack -- keep from showing an invisible first page.
      if (wordCount == 0) {
        this._setPosition(0);
        return;
      }

      this._storeBookmark(Reader.BOOKMARK_LAST);
    });
  }

  function _pageSize(words : string[], offset : int, backwards : boolean) : int {
    var min = 0; var max : int;
    if (!backwards) {
      max = (Reader.PAGE_SIZE * 2) - offset;
    } else {
      max = offset;
    }

    return this._binarySearch(min, max, (trialSize) -> {
      var start : int, end : int;
      if (!backwards) {
        start = offset;
        end = offset + trialSize;
      } else {
        start = offset - trialSize;
        end = offset;
      }
      start = this._bound(start, words.length);
      end = this._bound(end, words.length);
      var slice = words.slice(start, end);

      this._hiddenElem.innerHTML = slice.join(' ');
      var height = this._hiddenElem.offsetHeight;

      return (height > dom.window.innerHeight);
    });
  }

  function _binarySearch(min : int, max : int, fn : function(:int):boolean) : int {
    var trialSize = 0;
    var lastResult = false;
    while (true) {
      if (min >= max) {
        break;
      }

      trialSize = ((min + max) / 2) as int;
      lastResult = fn(trialSize);
      if (!lastResult) {
        min = trialSize + 1;
      } else {
        max = trialSize - 1;
      }
    }

    if (fn(min) == true) {
      --min;
    }
    return min;
  }

  function _bound(x : int, size : int) : int {
    if (x < 0) x = 0;
    if (x > size - 1) x = size - 1;
    return x;
  }

  function _fetch(bookId : int, firstPage : int, pageCount : int, callback : function(:string):void) : void {
    // If all pages are available, call back synchronously.
    var hasAllPages = true;
    for (var i = firstPage; i < firstPage + pageCount; ++i) {
      if (!this._hasCachedPage(i)) {
        hasAllPages = false;
        break;
      }
    }

    if (hasAllPages) {
      callback(this._stringTogether(firstPage, pageCount));
      return;
    }

    // Nope. Go ahead and fetch the whole range from the server.
    // TODO: This is somewhat wasteful, because we might be only missing one page.
    //       It's probably not worthwhile to optimize this perfectly, but we should at least clip the range.
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function(e:Event):void {
      if (xhr.readyState == 4) {
        if (xhr.status != 200) {
          this._error();
          return;
        }

        var pageArray = xhr.responseText.split('\0');
        for (var i = 0; i < pageArray.length; ++i) {
          this._cachePage(firstPage + i, pageArray[i]);
        }
        callback(this._stringTogether(firstPage, pageCount));
      }
    };
    xhr.open('GET', '/gutenberg/book' +
      '?bookId=' + (bookId as string) +
      '&firstPage=' + (firstPage as string) +
      '&pageCount=' + (pageCount as string));
    xhr.send();
  }

  function _error() : void {
    dom.window.location.replace('/gutenberg/examples.html');
  }

  function _stringTogether(firstPage : int, pageCount : int) : string {
    var text = '';
    for (var i = firstPage; i < firstPage + pageCount; ++i) {
      text += this._getCachedPage(i);
      text += ' ';
    }
    return text;
  }

  function _storeBookmark(name : string) : void {
    dom.window.localStorage['mark:' + name + ':' + (this._bookId as string)] = this._position as string;
  }

  function _loadBookmark(name : string) : void {
    var stored = dom.window.localStorage['mark:' + name + ':' + (this._bookId as string)];
    if (stored) {
      this._setPosition(Number.parseInt(stored));
    } else {
      this._setPosition(0);
    }
  }

  function _cachePage(index : int, page : string) : void {
    dom.window.localStorage['page:' + (index as string) + ':' + (this._bookId as string)] = page;
  }

  function _hasCachedPage(index : int) : boolean {
    return dom.window.localStorage['page:' + (index as string) + ':' + (this._bookId as string)] != null;
  }

  function _getCachedPage(index : int) : string {
    return dom.window.localStorage['page:' + (index as string) + ':' + (this._bookId as string)];
  }
}

// TODO: Need to find a way to ensure the fonts are loaded before we start trying to measure.
// Otherwise, the first page can render slightly off, usually cutting off a bit of text.
class _Main {
  static function main() : void {
    var id = 1;
    var hash = dom.window.location.hash;
    if (hash) {
      hash = hash.substring(1);
      id = Number.parseInt(hash);
      if (Number.isNaN(id)) {
        id = 1;
      }
    }
    new Reader(id);
  }
}
