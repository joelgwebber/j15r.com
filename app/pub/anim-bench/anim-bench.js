var urls = [
  'http://farm9.staticflickr.com/8493/8353858334_eeaa06313c.jpg',
  'http://farm9.staticflickr.com/8507/8353354936_99e2963ecb.jpg',
  'http://farm9.staticflickr.com/8356/8354746440_59c1ca38c5_z.jpg',
  'http://farm9.staticflickr.com/8511/8354190795_5fcc6b7868_n.jpg',
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

var layers = [];
var phase = 0;

function now() {
  return new Date().getTime();
}

function xform(e, x, y, z, rx, ry, rz, s) {
  var xform =
    'translate3d(' + x + 'px,' + y + 'px,' + z + 'px) ' +
    'rotateX(' + rx + 'deg) rotateY(' + ry + 'deg) rotateZ(' + rz + 'deg) ' +
    'scale(' + s + ')';
  e.style['webkitTransform'] = xform;
  e.style['transform'] = xform;
}

function makeLayer() {
  var layer = document.createElement('img');
  layer.width = 400; layer.height = 400;
  initLayer(layer);
  layer.src = urls[layers.length % urls.length];
  document.body.appendChild(layer);
  layers.push(layer);
}

function nextStep() {
  var sw = window.innerWidth, sh = window.innerHeight;
  var len = layers.length;
  for (var i = 0; i < len; ++i) {
    var a = Math.PI * 2 * (i + phase) / len;
    var cos = Math.cos(a);
    var x = (sw / 2) + Math.cos(a) * 0.75 * sw / 2 - 200;
    var y = (sh / 2) + Math.sin(a) * 0.75 * sh / 2 - 200;
    xformTo(layers[i], x, y, 0, 0, 0, (i + phase) * 10, (Math.sin(a * 5) + 2) * 0.25);
  }

  phase += layers.length / 3;
}

var arr = [];
function spewGarbage() {
  setInterval(function() {
    arr = [];
    for (var i = 0; i < 100000; ++i) {
      arr[i] = {
        x: Math.random(),
        y: Math.random(),
        z: Math.random()
      };
    }
  }, 1);
}


init();
for (var i = 0; i < 50; ++i) {
  makeLayer();
}
run(nextStep);

// spewGarbage();
