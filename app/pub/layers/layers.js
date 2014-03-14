function undef(x) { return x === void(0); }
function screenWidth() { return document.documentElement.clientWidth; }
function screenHeight() { return document.documentElement.clientHeight; }

var FAST = true;
var ANIM_TIME = 500;

function xform(e, x, y, s) {
  if (FAST) {
    if (undef(x)) x = 0;
    if (undef(y)) y = 0;
    if (undef(s)) s = 1;

    // TODO: ie9 (no 3d transform)
    var xform =
      'translate3d(' + x + 'px,' + y + 'px,0px) ' +
      'scale(' + s + ')';
    e.style['webkitTransform'] = xform;
    e.style['mozTransform'] = xform;
    e.style['oTransform'] = xform;
    e.style['transform'] = xform;
  } else {
    if (undef(x)) x = 0;
    if (undef(y)) y = 0;
    if (undef(s)) s = 1;

    e.style.marginLeft = x + 'px';
    e.style.marginTop = y + 'px';
    // TODO: Scale, somehow.
  }
}

function slideFromRight_hide(e, cb) {
  xform(e, screenWidth());
  if (cb) { setTimeout(cb, ANIM_TIME); }
}

function slideFromRight_show(e) {
  xform(e);
}

function fadeIn_hide(e, x, y, s, cb) {
  if (FAST) {
    xform(e, x, y, s);
  } else {
    xform(e, 0, screenHeight());
  }
  e.style.opacity = 0.25;
  if (cb) { setTimeout(cb, ANIM_TIME); }
}

function fadeIn_show(e) {
  xform(e);
  e.style.opacity = 1;
}

function Card(_feed) {
  var self = {
    elem: function() { return _elem; },

    moveTo: function(x, y) {
      _x = x; _y = y;
      xform(_elem, _x, _y);
    },
  };

  var _x = 0, _y = 0;
  var _elem = document.createElement('div');

  _elem.className = 'Card animateTransform';
  _elem.innerHTML = _feed.title;
  _elem.onclick = function(e) {
    var list = ListView(_feed);

    var w = screenWidth(), h = screenHeight();
    var targetX = _x - w/2 + Card.WIDTH/2 + 16, targetY = _y - h/2 + Card.HEIGHT/2;
    var targetScale = Card.WIDTH/w;
    list.setSourcePosition(targetX, targetY, targetScale);

    pushView(list);
  };

  return self;
}

Card.WIDTH = 180;
Card.HEIGHT = 240;

function CardsView(feeds) {
  var self = {
    elem: function() { return _elem; },

    show: function() {
      self.layout(false);
    },

    hide: function(cb) {
      self.layout(true);
    },

    layout: function(offscreen) {
      var width = _elem.offsetWidth;
      var x = 16, y = offscreen ? screenHeight() + 16 : 16;

      for (var i = 0; i < _tiles.length; ++i) {
        _tiles[i].moveTo(x, y);
        x += Card.WIDTH + 16;
        if (x + Card.WIDTH + 16 > width) {
          x = 16;
          y += Card.HEIGHT + (offscreen ? 64 : 16);
        }
      }
    },
  };

  function _addCard(feed) {
    var tile = Card(feed);
    _inner.appendChild(tile.elem());
    _tiles.push(tile);
    return tile;
  }

  var _tiles = [];
  var _elem = document.createElement('div');
  _elem.className = 'CardsView';

  var _inner = document.createElement('div');
  _elem.appendChild(_inner);

  for (var i in feeds) {
    _addCard(feeds[i]);
  }

  self.hide();

  return self;
}

function ListHeader(_title) {
  var self = {
    elem: function() { return _elem; },
  }

  var _elem = document.createElement('div');
  _elem.className = 'ListHeader';
  _elem.textContent = _title;

  return self;
}

function ListItem(_item) {
  var self = {
    elem: function() { return _elem; },
  };

  var _elem = document.createElement('div');
  _elem.className = 'ListItem';

  _elem.innerHTML =
    '<div class="title">' + _item.title + '</div>' +
    '<div class="summary">' + _item.summary + '</div>'
  ;

  _elem.onclick = function(e) {
    var detail = DetailView(_item);
    pushView(detail);
    e.stopPropagation();
  };

  return self;
}

function ListView(_feed) {
  var self = {
    elem: function() { return _elem; },

    layout: function() { },

    setSourcePosition: function(x, y, s) {
      _sourceX = x; _sourceY = y; _sourceScale = s;
      fadeIn_hide(_elem, _sourceX, _sourceY, _sourceScale);
    },

    show: function() {
      fadeIn_show(_elem);
    },

    hide: function(cb) {
      fadeIn_hide(_elem, _sourceX, _sourceY, _sourceScale, cb);
    },
  };

  function _render() {
    if (_feed.items.length == 0) {
      return;
    }

    var lastDate = new Date(0);
    for (var i in _feed.items) {
      var item = _feed.items[i];

      if (item.date.getDate() != lastDate.getDate()) {
        _inner.appendChild(ListHeader(item.date.toDateString()).elem());
      }
      lastDate = item.date;
      _inner.appendChild(ListItem(_feed.items[i]).elem());
    }
  }

  var _sourceX = 0, _sourceY = 0, _sourceScale = 0;
  var _elem = document.createElement('div');
  var _inner = document.createElement('div');
  _elem.appendChild(_inner);
  _elem.className = 'ListView animateTransform';

  _render();
  self.hide();
  return self;
}

function DetailView(_item) {
  var self = {
    elem: function() { return _elem; },

    layout: function() { },

    show: function() {
      slideFromRight_show(_elem);
    },

    hide: function(cb) {
      slideFromRight_hide(_elem, cb);
    },
  };

  function _render() {
    _elem.innerHTML = _item.contents;
  }

  var _elem = document.createElement('div');
  _elem.className = 'DetailView animateTransform';

  _render();
  self.hide();
  return self;
}

function NavBar() {
  var self = {
    elem: function() { return _elem; },
  };

  var _elem = document.createElement('div');
  _elem.className = 'NavBar';

  var _button = document.createElement('button');
  _elem.appendChild(_button);
  _button.textContent = '<';
  _button.onclick = function(e) {
    popView();
  };

  var _check = document.createElement('input');
  _check.type = 'checkbox';
  _check.defaultChecked = true;
  _elem.appendChild(_check);
  _check.onchange = function(e) {
    FAST = _check.checked;
    initViews();
  };

  return self;
}

var _loremPhrases = [
  "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "Donec mollis nisl vel erat aliquet sed vestibulum lectus aliquam.", "In imperdiet tristique odio nec blandit.", "Donec orci purus, pulvinar vel aliquet sed, tempus dapibus magna.", "Ut et odio vitae arcu bibendum faucibus.", "Proin sapien mi, ultricies vitae pulvinar ut, facilisis non metus.", "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.", "Cras porttitor interdum justo eget elementum.", "Pellentesque porttitor mauris non ipsum euismod quis gravida ante vehicula.", "Curabitur mi sapien, egestas sit amet pulvinar et, consequat eu nunc.", "Integer eu neque eget nisl vehicula volutpat nec id enim.",
  "Sed non porttitor lorem.", "Donec ullamcorper nisl et arcu semper ac pulvinar quam consequat.", "Etiam odio odio, lacinia ac dictum nec, consectetur bibendum metus.", "In lacinia ullamcorper lectus, a faucibus eros pellentesque non.", "Vivamus pharetra, justo non blandit congue, est tellus ornare lorem, nec ultrices magna tellus in quam.", "Nulla facilisi.", "Quisque lacinia aliquet purus, vel elementum ligula auctor eu.", "Vivamus eleifend, nibh sed sollicitudin consectetur, mi augue vulputate dui, eget ornare risus dui ut sapien.", "Nulla dapibus vehicula lacus quis luctus.", "Vivamus ante quam, egestas vitae ultricies vitae, pretium ut eros.",
  "Nulla et nisl vel diam venenatis vestibulum ut eget est.", "Quisque euismod semper nisi, id tempor enim rutrum at.", "Donec egestas elit sed massa fringilla blandit.", "Donec fermentum dignissim lacinia.", "Etiam at tortor lorem, ac posuere sem.", "Aliquam felis nunc, malesuada vitae mattis ac, mattis in arcu.", "Vivamus laoreet urna congue tortor vestibulum condimentum.", "Nunc quis est ante, non vulputate eros.",
  "Quisque hendrerit rhoncus arcu.", "Sed sem elit, placerat et ultricies a, mollis tincidunt nunc.", "Mauris faucibus cursus magna, non congue dui lobortis dictum.", "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Curabitur pulvinar lorem et nunc suscipit varius.", "Morbi non magna tortor.", "Quisque vulputate dui ac nulla volutpat eu malesuada arcu bibendum.", "Nulla eget ligula risus.", "Donec turpis tellus, vestibulum et varius sed, interdum at purus.", "Quisque ultrices tellus eu metus volutpat dictum.", "Nunc in arcu hendrerit massa pellentesque egestas.",
  "Pellentesque vel augue massa.", "Cras feugiat ultrices arcu, at elementum lacus sagittis non.", "Praesent in justo enim.", "Vivamus semper nibh vel sapien fringilla eget porttitor elit porttitor.", "Pellentesque ac nisi tortor.", "Maecenas cursus gravida neque ut tempus.", "Quisque augue justo, semper et aliquet sit amet, scelerisque at velit.", "Nunc id mauris elit.", "Nulla facilisi.",
];

var _imageUrls = [
  'http://farm9.staticflickr.com/8493/8353858334_eeaa06313c.jpg',
  'http://farm9.staticflickr.com/8507/8353354936_99e2963ecb.jpg',
  'http://farm9.staticflickr.com/8356/8354746440_59c1ca38c5_z.jpg',
  'http://farm9.staticflickr.com/8359/8353832893_40ecc9f512.jpg',
  'http://farm9.staticflickr.com/8329/8353380627_38f6ebe4e3.jpg',
  'http://farm9.staticflickr.com/8357/8353280078_e73db80eda.jpg',
  'http://farm9.staticflickr.com/8495/8353225687_161489b824.jpg',
  'http://farm9.staticflickr.com/8503/8352194919_0cc09db74f.jpg',
  'http://farm9.staticflickr.com/8466/8354404821_268f523243.jpg',
  'http://farm9.staticflickr.com/8043/8354176442_5400fc51b8.jpg',
  'http://farm9.staticflickr.com/8085/8353937074_601892d253.jpg',
  'http://farm9.staticflickr.com/8504/8352840203_17b843a499.jpg',
  'http://farm9.staticflickr.com/8335/8352102927_f19c5e19cf.jpg',
  'http://farm9.staticflickr.com/8492/8355327766_6b0de5d10f.jpg',
  'http://farm9.staticflickr.com/8463/8353084339_4ef359ac1a.jpg',
  'http://farm9.staticflickr.com/8362/8352057181_9705b46ecc.jpg',
  'http://farm9.staticflickr.com/8361/8355054856_bd4f103ffb.jpg',
  'http://farm9.staticflickr.com/8367/8353711596_84c1d79bd2.jpg',
  'http://farm9.staticflickr.com/8238/8353871832_b28ca533df.jpg',
  'http://farm9.staticflickr.com/8230/8355043888_6547b82f40.jpg',
];

function randPhrase() {
  return _loremPhrases[randInt(_loremPhrases.length)];
}

function randImage() {
  return _imageUrls[randInt(_imageUrls.length)];
}

function lorem(min, max) {
  var n = randInt(max - min) + min;
  var s = '';
  for (var i = 0; i < n; ++i) {
    s += randPhrase();
    if (i < n - 1) {
      s += ' ';
    }
  }
  return s;
}

function loremContent(min, max) {
  var np = randInt(max - min) + min;
  var s = '';
  for (var i = 0; i < np; ++i) {
    // 20% chance of an image.
    if ((i > 0) && (Math.random() < 0.2)) {
      s += '<p style="text-align:center;margin:2em;"><img src="' + randImage() + '"></p>';
    } else {
      s += '<p>';
      var ns = randInt(20) + 5;
      for (var j = 0; j < ns; ++j) {
        s += randPhrase();
        if (i < ns - 1) {
          s += ' ';
        }
      }
      s += '</p>';
    }
  }
  return s;
}

function randInt(range) {
  return Math.floor(Math.random() * range);
}

function fakeDate(maxDaysBeforeToday) {
  var now = new Date();
  var d = new Date();
  d.setFullYear(now.getFullYear());
  d.setMonth(now.getMonth());
  d.setDate(now.getDate() - randInt(maxDaysBeforeToday));
  return d;
}

function makeFeeds() {
  var feeds = [];

  for (var i = 0; i < 30; ++i) {
    var feed = { title: lorem(1, 1), items: [] };
    feeds.push(feed);

    var d = fakeDate(10);
    for (var j = 0; j < 100; ++j) {
      feed.items.push({
        title: lorem(1, 1),
        summary: lorem(2, 5),
        date: new Date(d.getTime()),
        contents: loremContent(5, 25),
      });

      // 20% of the time, go back 1-5 days.
      if (Math.random() < 0.2) {
        d.setDate(d.getDate() - randInt(5) + 1);
      }
    }
  }
  return feeds;
}

var _viewStack = [];

function layoutViews() {
  var view = topView();
  var style = view.elem().style;
  style.left = '32px';
  style.top = '0px';
  style.width = (screenWidth() - 32) + 'px';
  style.height = screenHeight() + 'px';
  view.layout();
}

function topView() {
  return _viewStack[_viewStack.length - 1];
}

function pushView(view) {
  _viewStack.push(view);
  document.body.appendChild(view.elem());
  setTimeout(function() {
    layoutViews();
    view.show();
  }, 0);
}

function popView() {
  if (_viewStack.length <= 1) {
    return;
  }

  var view = _viewStack.pop();
  layoutViews();
  view.hide(function() {
    document.body.removeChild(view.elem());
  });
}

function initViews() {
  // Clear out old views.
  for (var i in _viewStack) {
    document.body.removeChild(_viewStack[i].elem());
  }
  _viewStack = [];

  // And start with the cards view.
  var cards = CardsView(makeFeeds());
  pushView(cards);
  layoutViews();
}

function main() {
  var nav = NavBar();
  document.body.appendChild(nav.elem());

  window.onresize = function() {
    layoutViews();
  };

  initViews();
}

main();
