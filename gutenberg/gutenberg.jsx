import 'js/web.jsx';

class Reader {
  static const PAGE_SIZE = 1024;

  var _bookId : string;
  var _curPageElem : HTMLElement;
  var _pageCache = {} : Map.<string>;
  var _position : int;
  var _endPosition : int;

  function constructor(bookId : string) {
    this._bookId = bookId;

    var doc = dom.window.document;
    this._curPageElem = doc.createElement('div') as HTMLElement;
    this._curPageElem.className = 'page';
    doc.body.appendChild(this._curPageElem);

    dom.window.onkeydown = function(e:Event):void { this._onKeyDown(e as KeyboardEvent); };
    dom.window.onmousedown = function(e:Event):void { this._onTap(e as MouseEvent); };

    this._setPosition(0);
  }

  function _onKeyDown(e : KeyboardEvent) : void {
  }

  function _onTap(e : MouseEvent) : void {
    if (e.clientX < dom.window.innerWidth / 2) {
      this._prevPage();
    } else {
      this._nextPage();
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

      this._curPageElem.innerHTML = words.slice(offset, offset + wordCount).join(' ');
      this._endPosition = this._position + wordCount;
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

      this._curPageElem.innerHTML = words.slice(offset - wordCount, offset).join(' ');
      this._position = this._endPosition - wordCount;
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

      this._curPageElem.innerHTML = slice.join(' ');
      var height = this._curPageElem.offsetHeight;

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

  function _fetch(bookId : string, firstPage : int, pageCount : int, callback : function(:string):void) : void {
    // If all pages are available, call back synchronously.
    var hasAllPages = true;
    for (var i = firstPage; i < firstPage + pageCount; ++i) {
      if (!((i as string) in this._pageCache)) {
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
          this._networkError(xhr.responseText);
          return;
        }

        var pageArray = xhr.responseText.split('\0');
        for (var i = 0; i < pageArray.length; ++i) {
          this._pageCache[(firstPage + i) as string] = pageArray[i];
        }
        callback(this._stringTogether(firstPage, pageCount));
      }
    };
    xhr.open('GET', '/gutenberg/book' +
      '?bookId=' + bookId +
      '&firstPage=' + (firstPage as string) +
      '&pageCount=' + (pageCount as string));
    xhr.send();
  }

  function _networkError(text : string) : void {
    dom.window.alert(text);
  }

  function _stringTogether(firstPage : int, pageCount : int) : string {
    var text = '';
    for (var i = firstPage; i < firstPage + pageCount; ++i) {
      assert (i as string) in this._pageCache;
      text += this._pageCache[i as string];
      text += ' ';
    }
    return text;
  }
}

class _Main {
  static function main() : void {
    new Reader('pg1184');
  }
}
