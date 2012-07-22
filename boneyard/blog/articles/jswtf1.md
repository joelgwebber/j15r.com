Browser WTF #1
--------------

I think it's safe to say that most developers will agree with me when I say
that "WTF?" is one of the most common expressions uttered when working with web
browsers. So I've decided to try and capture some of the strangest and nastiest
cases that have made me yell this while working with browsers myself.


...
 function test(b) {
    if (b) {
      function foo() { return true; }
    } else {
      function foo() { return false; }
    }

    echo(b + " ?= " + foo());
  }

  test(true);
  test(false);
...


