It turns out assigning an object to an element's innerHTML doesn't just do an
implicit cast in IE7.  It will cause a leak.  Our code that hit this assigned a
StringBuffer without explicitly calling toString().
