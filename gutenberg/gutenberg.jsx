import 'js/web.jsx';

class Reader {
  static const PAGE_SIZE = 512;

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

      var bounds = this._bound(offset, offset + wordCount, Reader.PAGE_SIZE * 2);
      this._curPageElem.innerHTML = words.slice(bounds[0], bounds[1]).join(' ');
      this._endPosition = bounds[1];
    });
  }

  function _setEndPosition(pos : int) : void {
    // Keep in bounds.
    // TODO: Need a book manifest to know the upper bound.
    this._endPosition = pos;
    if (this._endPosition < 0) this._endPosition = 0;

    // Fetch the new page
    var page = Math.floor(this._endPosition / Reader.PAGE_SIZE) - 2;
    if (page < 0) page = 0;
    this._fetch(this._bookId, page, 2, function(text : string) : void {
      var offset = this._endPosition - (page * Reader.PAGE_SIZE);
      assert offset >= 0 && offset < Reader.PAGE_SIZE * 2;

      var words = text.split(' ');
      var wordCount = this._pageSize(words, offset, true);

      var bounds = this._bound(offset - wordCount, offset, Reader.PAGE_SIZE * 2);
      this._curPageElem.innerHTML = words.slice(bounds[0], bounds[1]).join(' ');
      this._position = bounds[0];
    });
  }

  function _pageSize(words : string[], offset : int, backwards : boolean) : int {
    var min = 0, max = Reader.PAGE_SIZE * 2;
    while (true) {
      var trialSize = ((min + max) / 2) as int;

      var start : int, end : int;
      if (!backwards) {
        start = offset;
        end = offset + trialSize;
      } else {
        start = offset - trialSize;
        end = offset;
      }
      var bounds = this._bound(start, end, words.length);
      var slice = words.slice(bounds[0], bounds[1]);

      this._curPageElem.innerHTML = slice.join(' ');
      var height = this._curPageElem.offsetHeight;

      if (height < dom.window.innerHeight) {
        min = trialSize + 1;
      } else {
        max = trialSize - 1;
      }

      if (min == max) {
        break;
      }
    }
    return max;
  }

  function _bound(start : int, end : int, size : int) : int[] {
    if (start < 0) start = 0;
    if (end   < 0) end  = 0;
    if (start > size - 1) start = size - 1;
    if (end   > size)     end   = size;
    return [start, end] : int[];
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
