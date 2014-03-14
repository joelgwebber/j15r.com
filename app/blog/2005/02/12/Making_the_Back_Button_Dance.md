It seems that I keep discovering nifty things about Google Maps. I didn't
notice anyone commenting on the details of the way it interacts with the
browser history, but there's one really groovy trick it plays that really adds
to usability.We already know that clicking the back button causes it to display
the results of your previous search. That's certainly helpful, as you don't
lose your search history. If you play with it a bit, however, you may also
notice that it also moves the **map** to your previous search location as
well. This applies to all of your search history, as you move forwards and
backwards amongst your results. Try it out.

## But how?
If you dig through the DOM a bit (e.g. using Mozilla's DOM Inspector), you will
see that there are three input fields in the hidden IFrame that the application
uses to communicate with the server. At first glance, it doesn't appear that
they're used for much of anything (I myself first dismissed them as an
unfinished attempt to make the application state easily to link to).  If you
look at the application code, you'll see that it stores the current map state
(latitude, longitude, and zoom level) in these fields every time it changes.

Here's the tricky part. When a search result comes back from the server, the
HTML in which it's wrapped contains yet another set of three empty fields.
When you click back, though, the browser replaces the IFrame contents with its
previous state, **including** the values that were stored in these fields.
You've probably seen this before in regular web applications, when the browser
tries to maintain the state of forms when it goes back to a cached version.

So you've clicked the back button, the hidden IFrame contains its previous
state, and the right panel now displays your previous search results. The
application also reaches into these three fields to get the previous map state,
and pans/zooms as necessary to display the map in its previous state as well
(you'll find the code that does this in the application's loadVPage() method).
Voila!

## Reclaiming the back button
I believe that, as more developers build web applications that make good use of
DHTML, we will find tricks like this to be invaluable in maintaining the user
experience. Hopefully, users will be able to depend upon the browser history to
work as advertised -- something we can't often say about most web apps these
days.

