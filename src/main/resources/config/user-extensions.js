if (typeof WEBE == 'undefined') WEBE = {};

WEBE.highlight_with_webdriver = function(element){
  if (element.highlighted) return false;

  var old_bg_color = element.style.backgroundColor;
  var old_border = element.style.border;

  element.style.border = '2px dashed #348017';
  element.style.backgroundColor = '#C3FDB8';
  element.highlighted = true;

  var unhighlight = function(){
    element.style.backgroundColor = old_bg_color;
    element.style.border = old_border;
    element.highlighted = false;
  }

  setTimeout(unhighlight, 1500);
  return true;
}

