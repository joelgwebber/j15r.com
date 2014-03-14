function init() {
}

function run(callback) {
  layers[0].addEventListener('webkitTransitionEnd', callback, false);
  layers[0].addEventListener('transitionend', callback, false);
  setTimeout(function() { callback(); }, 0);
}

function initLayer(layer) {
  layer.style.position = 'absolute';
  layer.style.webkitTransition = '-webkit-transform ease-in-out 1s';
  layer.style.transition = 'transform ease-in-out 1s';
}

function xformTo(e, x, y, z, rx, ry, rz, s) {
  xform(e, x, y, z, rx, ry, rz, s);
}
