function infect(name) {
  var script = document.createElement('script');
  script.src = base + name + '.js';
  document.getElementsByTagName('head')[0].appendChild(script);
}

function loadCallback(onLoad) {
  onLoad(null, name, base);
}