import 'js/web.jsx';

class _Main {

  static function _onLoad() : void {
    var doc = dom.window.document;
    var header = doc.getElementsByClassName('header').item(0) as HTMLElement;
    var headerMain = doc.getElementsByClassName('header-main').item(0) as HTMLElement;
    var headerGradient = doc.getElementsByClassName('header-gradient').item(0) as HTMLElement;
    var maxHeight = header.offsetHeight;
    var minHeight = headerMain.offsetHeight;

    dom.window.onscroll = (e) -> {
      var height = maxHeight - doc.body.scrollTop;
      if (height < minHeight) {
        height = minHeight;
      } else if (height > maxHeight) {
        height = maxHeight;
      }
      var len = (height as string) + 'px';
      header.style.height = len;
      headerGradient.style.top = len;
    };
  }

  static function main() : void {
    dom.window.onload = (e) -> { _Main._onLoad(); };
  }
}
