// ---------------------------------------------------------------------------
// Utilities
function cssTransformOrigin(elem, ox, oy) {
  elem.style.webkitTransformOrigin = '' + ox + (oy ? ' ' + oy : '');
}

function cssAbsolute(elem) {
  elem.style.position = 'absolute';
}

function cssTransitionProperty(elem, value) {
  elem.style.webkitTransitionProperty = value;
}

function cssTransitionDuration(elem, value) {
  elem.style.webkitTransitionDuration = value;
}

function cssTransitionTimingFunction(elem, value) {
  elem.style.webkitTransitionTimingFunction = value;
}

function cssTransform(elem, r, tx, ty, s) {
  elem.style.webkitTransform = 'rotate(' + r + 'deg) translate(' + tx + 'px, ' + ty + 'px) scale(' + s + ')';
}

function cssDisplay(elem, value) {
  elem.style.display = value;
}

function cssPosition(elem, x, y) {
  elem.style.left = x + 'px';
  elem.style.top = y + 'px';
}

function cssSize(elem, w, h) {
  elem.style.width = w + 'px';
  elem.style.height = h + 'px';
}

function cssLTRB(elem, l, t, r, b) {
  elem.style.left = l + 'px';
  elem.style.top = t + 'px';
  elem.style.right = r + 'px';
  elem.style.bottom = b + 'px';
}

function cssZIndex(elem, z) {
  elem.style.zIndex = z;
}

function cssOpacity(elem, alpha) {
  elem.style.opacity = alpha;
}

function cssAddClass(elem, className) {
  if (!cssHasClass(elem, className)) {
    elem.className += ' ' + className;
  }
}

function cssRemoveClass(elem, className) {
  var classes = elem.className.split(' ');
  elem.className = '';
  for (var idx in classes) {
    if (classes[idx] != className)
      elem.className += classes[idx] + ' ';
  }
}

function cssHasClass(elem, className) {
  var classes = elem.className.split(' ');
  for (var idx in classes) {
    if (classes[idx] == className)
      return true;
  }
  return false;
}

function bind(_this, fn) {
  return function() {
    fn.apply(_this, arguments);
  }
}

// ---------------------------------------------------------------------------
// Axis
function Axis(name, values) {
  this._name = name;
  this._values = values;
}

Axis.prototype = {
  _name: null,
  _values: [],
  _index: 0,

  index: function() { return this._index; },
  value: function() { return this.valueAt(this.index()); },
  valueAt: function(index) { return this._values[index]; },
  length: function() { return this._values.length; },
  name: function() { return this._name; },

  prev: function() { this.set(this.index() - 1); },
  next: function() { this.set(this.index() + 1); },

  set: function(index) {
    if (index < 0) index = 0;
    if (index >= this.length()) index = this.length() - 1;

    this._index = index;
  },
};

function Spec(focalLength, iso, fStop, shutter) {
  this.focalLength = focalLength;
  this.iso = iso;
  this.fStop = fStop;
  this.shutter = shutter;
}

// ---------------------------------------------------------------------------
// Spec
Spec.prototype = {
  focalLength: 0,
  iso: 0,
  fStop: 0,
  shutter: 0,

  folderName: function() { return 'viol/' + this.focalLength + 'mm_iso' + this.iso + '_f' + this.fStop; },
  thumbName: function() { return this.folderName() + '/' + this.shutter + '_thumb.jpg'; },
  fileName: function() { return this.folderName() + '/' + this.shutter + '.jpg'; },
};

// ---------------------------------------------------------------------------
// AxisView
function AxisView(axis, vertical) {
  this._vertical = vertical;

  this._div = document.createElement('div');
  this._div.className = 'AxisView';
  cssAbsolute(this._div);
  cssTransformOrigin(this._div, '0', '0');
  cssZIndex(this._div, 3);

  this.setAxis(axis);
  this.layout();
}

AxisView.prototype = {
  _vertical: false,
  _axis: null,
  _div: null,

  element: function() { return this._div; },

  update: function() {
    this._div.innerHTML = '\u2190 ' + this._axis.name() + ' :: ' + this._axis.value() + ' \u2192';
    this._div.style.width = '384px';
    this._div.style.height = '40px';
    this.layout();
  },

  layout: function() {
    var tx, ty;

    if (this._vertical) {
      tx = window.innerHeight / 2 - (192 + 8);
      ty = -64;
    } else {
      tx = window.innerWidth / 2 - (192 + 8);
      ty = window.innerHeight - 64;
    }

    cssTransform(this._div, this._vertical ? 90 : 0, tx, ty, 1);
  },

  setAxis: function(axis) {
    this._axis = axis;
    this.update();
  },
};


// ---------------------------------------------------------------------------
// ZoomView
function ZoomView() {
  this._img = document.createElement('img');
  cssAbsolute(this._img);
  cssPosition(this._img, 64, 64);
  cssZIndex(this._img, 3);
  this.hide();
  this.layout();

  this._img.addEventListener('click', bind(this, this.hide), false);
}

ZoomView.prototype = {
  _img: null,

  element: function() { return this._img; },

  show: function(spec) {
    this._img.src = spec.fileName();
    cssDisplay(this._img, 'block');
  },

  hide: function() {
    cssDisplay(this._img, 'none');
  },

  layout: function() {
    cssSize(this._img, window.innerWidth - 128, window.innerHeight - 128);
  },
};

// ---------------------------------------------------------------------------
// PhotoGrid
function PhotoGrid(axis0, axis1) {
  this._axis0 = axis0;
  this._axis1 = axis1;

  this._axisView0 = new AxisView(g_iso, false);
  this._axisView1 = new AxisView(g_shutter, true);
  this._zoomView = new ZoomView();

  this._imageDiv = document.createElement('div');

  this._div = document.createElement('div');
  this._div.className = 'PhotoGrid';
  this._div.appendChild(this._imageDiv);
  this._div.appendChild(this._zoomView.element());
  this._div.appendChild(this._axisView0.element());
  this._div.appendChild(this._axisView1.element());

  this._makeImages();
  this.layout();

  window.addEventListener('keydown', bind(this, this.onKeyDown), true);
  window.addEventListener('resize', bind(this, this.layout), true);
}

PhotoGrid.prototype = {
  _div: null,
  _imageDiv: null,
  _curImg: null,

  _axis0: null,
  _axis1: null,

  _axisView0: null,
  _axisView1: null,
  _zoomView: null,

  _images: null,

  _makeImages: function() {
    this._imageDiv.innerHTML = '';
    this._images = [];

    for (var j = 0; j < this._axis1.length(); ++j) {
      this._images[j] = [];
      this._axis1.set(j);

      for (var i = 0; i < this._axis0.length(); ++i) {
        var img = this._images[j][i] = document.createElement('img');
        cssAbsolute(img);
        cssTransitionProperty(img, 'opacity -webkit-transform');
        cssTransitionDuration(img, '0.25s');
        cssTransitionTimingFunction(img, 'ease-out');
        cssTransformOrigin(img, '50%', '50%');
        this._imageDiv.appendChild(img);

        this._axis0.set(i);
        img.src = new Spec(
          g_focalLength.value(),
          g_iso.value(),
          g_fStop.value(),
          g_shutter.value()
        ).thumbName();

        img._x = i;
        img._y = j;
        img.addEventListener('click', bind(this, this._imageClicked), false);
      }
    }

    this._axis0.set(0);
    this._axis1.set(0);
  },

  _updateImages: function() {
    var stepWidth = THUMB_WIDTH * 2;
    var stepHeight = THUMB_HEIGHT * 2;

    var x = window.innerWidth  / 2 - this._axis0.index() * stepWidth  - THUMB_WIDTH  / 2,
        y = window.innerHeight / 2 - this._axis1.index() * stepHeight - THUMB_HEIGHT / 2;
    var startX = x;

    for (var j = 0; j < this._axis1.length(); ++j) {
      for (var i = 0; i < this._axis0.length(); ++i) {
        var img = this._images[j][i];
        var isCenter = (i == this._axis0.index()) && (j == this._axis1.index());
        var dist = Math.abs(i - this._axis0.index()) + Math.abs(j - this._axis1.index());

        var scale = (dist == 0) ? 2.5 : (dist == 1) ? 1.5 : 1;
        cssTransform(img, 0, x, y, scale);
        cssZIndex(img, scale);

        var opacity = 1.0 - (0.33 * dist);
        if (opacity < 0) opacity = 0;
        cssOpacity(img, opacity);

        img.width = THUMB_WIDTH;
        img.height = THUMB_HEIGHT;
        x += stepWidth;
      }
      x = startX; y += stepHeight;
    }
  },

  _imageClicked: function(evt) {
    var img = evt.currentTarget;

    if (cssHasClass(img, 'center')) {
      this._zoomView.show(new Spec(
        g_focalLength.value(),
        g_iso.value(),
        g_fStop.value(),
        g_shutter.value()
      ));
      return;
    }

    var x = img._x, y = img._y;
    this._axis0.set(x);
    this._axis1.set(y);
    this.update();
  },

  element: function() { return this._div; },

  update: function() {
    if (this._curImg)
      cssRemoveClass(this._curImg, 'center');

    this._curImg = this._images[this._axis1.index()][this._axis0.index()];
    cssAddClass(this._curImg, 'center');

    this._updateImages();
    this._axisView0.update();
    this._axisView1.update();
  },

  layout: function(evt) {
    this.update();
    this._axisView0.layout();
    this._axisView1.layout();
  },

  onKeyDown: function(evt) {
    switch (evt.keyCode) {
      case 37: this._axis0.prev(); this.update(); break;
      case 39: this._axis0.next(); this.update(); break;
      case 38: this._axis1.prev(); this.update(); break;
      case 40: this._axis1.next(); this.update(); break;
    }
  },
}

// ---------------------------------------------------------------------------
var THUMB_WIDTH = 160, THUMB_HEIGHT = 120;
var IMAGE_WIDTH = THUMB_WIDTH * 3, IMAGE_HEIGHT = THUMB_HEIGHT * 3;

var g_focalLength = new Axis('focal-length', [35, 50]);
var g_iso = new Axis('iso', [100, 200, 400, 800, 1600, 3200]);
var g_fStop = new Axis('f-stop', [1.8, 2.8, 4.5, 7.1, 11, 18]);
var g_shutter = new Axis('shutter-speed', [1, 2.5, 6, 15, 40, 100, 250, 640, 1600, 4000, 6400]);

function main() {
  var grid = new PhotoGrid(g_iso, g_shutter);

  document.body.appendChild(grid.element());
}

