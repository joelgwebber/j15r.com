var lastRafCallback;
var stepCallback;

function Bezier(p1,p2,p3,p4) {
  // defining the bezier functions in the polynomial form
  var Cx = 3 * p1;
  var Bx = 3 * (p3 - p1) - Cx;
  var Ax = 1 - Cx - Bx;

  var Cy = 3 * p2;
  var By = 3 * (p4 - p2) - Cy;
  var Ay = 1 - Cy - By;

  function bezier_x(t) { return t * (Cx + t * (Bx + t * Ax)); }
  function bezier_y(t) { return t * (Cy + t * (By + t * Ay)); }

  // using Newton's method to aproximate the parametric value of x for t
  function bezier_x_der(t) { return Cx + t * (2*Bx + 3*Ax * t); }

  function find_x_for(t) {
    var x=t, i=0, z;

    while (i < 5) { // making 5 iterations max
      z = bezier_x(x) - t;

      if (Math.abs(z) < 1e-3) break; // if already got close enough

      x = x - z/bezier_x_der(x);
      i++;
    }

    return x;
  };

  return function(t) {
    return bezier_y(find_x_for(t));
  }
}

var easeInOut = Bezier(0.42, 0, 0.58, 1.0);

function init() {
  lastRafCallback = now();
  raf(rafCallback);
}

function run(callback) {
  stepCallback = callback;
  stepCallback();
}

function initLayer(e) {
  e.style.position = 'absolute';
  e._x = 0; e._y = 0; e._z = 0;
  e._rx = 0; e._ry = 0; e._rz = 0;
  e._s = 1;

  xformTo(e, 0, 0, 0, 0, 0, 0, 1);
}

function xformTo(e, x, y, z, rx, ry, rz, s) {
  e._sx = e._x; e._sy = e._y; e._sz = e._z;
  e._srx = e._rx; e._sry = e._ry; e._srz = e._rz;
  e._ss = e._s;

  e._tx = x; e._ty = y; e._tz = z;
  e._trx = rx; e._try = ry; e._trz = rz;
  e._ts = s;

  e._a = 0;
}

function raf(callback) {
  if (window.requestAnimationFrame) {
    requestAnimationFrame(callback);
  } else if (window.mozRequestAnimationFrame) {
    mozRequestAnimationFrame(callback);
  } else {
    webkitRequestAnimationFrame(callback);
  }
}

function rafCallback() {
  var t = now();
  var delta = t - lastRafCallback;

  for (var i = 0; i < layers.length; ++i) {
    var e = layers[i];

    e._a += delta;
    var alpha = e._a / 1000;
    a = easeInOut(alpha);
    if (a > 1) {
      a = 1;
    }

    e._x = e._sx + (e._tx - e._sx) * a;
    e._y = e._sy + (e._ty - e._sy) * a;
    e._z = e._sz + (e._tz - e._sz) * a;
    e._rx = e._srx + (e._trx - e._srx) * a;
    e._ry = e._sry + (e._try - e._sry) * a;
    e._rz = e._srz + (e._trz - e._srz) * a;
    e._s = e._ss + (e._ts - e._ss) * a;

    xform(e, e._x, e._y, e._z, e._rx, e._ry, e._rz, e._s);
  }

  if (alpha > 1) {
    stepCallback();
  }

  lastRafCallback = t;
  raf(rafCallback);
}
