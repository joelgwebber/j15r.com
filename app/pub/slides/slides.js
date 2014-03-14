var Slides;
(function (Slides) {
    var slides = [];
    var notes = [];
    var curSlide = -1;
    var lastShownSlide;
    var bugDiv;
    var printBug;
    var presentBug;
    var notesWindow;
    var printMode = false;
    var presentMode = false;

    function init() {
        var slideList = window.document.querySelectorAll('.slide');
        for (var i = 0; i < slideList.length; ++i) {
            slides[i] = slideList[i];
            notes[i] = slides[i].querySelector('.notes');
        }

        hideAllSlides();
        showSlide(0);

        window.addEventListener('keydown', function (e) {
            onKeyDown(e);
        }, false);
        window.addEventListener('click', function (e) {
            onClick(e);
        }, false);

        initBugs();
        printBug = addBug('print', function (e) {
            switchPrintMode(e);
        });
        presentBug = addBug('present', function (e) {
            switchPresentMode(e);
        });
    }
    Slides.init = init;

    function squash(evt) {
        if (evt != null) {
            evt.preventDefault();
            evt.stopPropagation();
        }
    }

    function hide(elem) {
        var s = elem.style;
        s.opacity = '0';
        s.visibility = 'hidden';
    }

    function show(elem) {
        var s = elem.style;
        s.opacity = '1';
        s.visibility = '';
    }

    function fireOnShow(slide) {
        var id = slide.getAttribute('id');
        if (id != null) {
            var fnName = id + '_onShow';
            if (fnName in window) {
                var fn = window[fnName];
                fn(slide);
            }
        }
    }

    function fireOnHide(slide) {
        var id = slide.getAttribute('id');
        if (id != null) {
            var fnName = id + '_onHide';
            if (fnName in window) {
                var fn = window[fnName];
                fn(slide);
            }
        }
    }

    function hideAllSlides() {
        for (var i in slides) {
            hide(slides[i]);
            var s = slides[i].style;
            s.position = 'absolute';
            s.left = s.top = '1em';
        }
    }

    function showAllSlides() {
        for (var i in slides) {
            show(slides[i]);
            slides[i].style.position = '';
        }
    }

    function hideAllNotes() {
        for (var i in slides) {
            if (notes[i] != null) {
                hide(notes[i]);
            }
        }
    }

    function showAllNotes() {
        for (var i in slides) {
            if (notes[i] != null) {
                show(notes[i]);
            }
        }
    }

    function showSlide(idx) {
        if (printMode || (curSlide == idx) || (idx < 0) || (idx >= slides.length)) {
            return;
        }

        if (curSlide != -1) {
            hide(slides[curSlide]);
            fireOnHide(slides[curSlide]);
        }

        curSlide = idx;

        if (curSlide != -1) {
            fireOnShow(slides[curSlide]);
            show(slides[curSlide]);
            updateNotes();
        }
    }

    function nextSlide(evt) {
        squash(evt);
        showSlide(curSlide + 1);
    }

    function prevSlide(evt) {
        squash(evt);
        showSlide(curSlide - 1);
    }

    function updateNotes() {
        if (presentMode) {
            var html = '';
            if (notes[curSlide]) {
                html = notes[curSlide].innerHTML;
            }
            notesWindow.document.body.innerHTML = html;
        }
    }

    function switchPrintMode(evt) {
        squash(evt);

        if (!printMode) {
            showAllSlides();
            printBug.innerHTML = 'done';
            hide(presentBug);
            fireOnHide(slides[curSlide]);
            lastShownSlide = curSlide;
            curSlide = -1;
            printMode = true;
        } else {
            hideAllSlides();
            printBug.innerHTML = 'print';
            show(presentBug);
            printMode = false;
            showSlide(lastShownSlide);
        }
    }

    function switchPresentMode(evt) {
        squash(evt);

        if (!presentMode) {
            notesWindow = window.open('about:blank', '_blank', 'menubar:0,status:0,location:0');
            if (!notesWindow) {
                // Popup likely blocked.
                return;
            }

            notesWindow.addEventListener('unload', function (e) {
                if (presentMode) {
                    switchPresentMode(e);
                }
            }, false);

            hideAllNotes();
            hide(bugDiv);
            presentMode = true;
            window.document.body.style.overflow = 'hidden';
            updateNotes();
        } else {
            showAllNotes();
            show(bugDiv);
            notesWindow.close();
            window.document.body.style.overflow = 'auto';
            presentMode = false;
        }
    }

    function initBugs() {
        bugDiv = window.document.createElement('div');
        bugDiv.className = 'bug-container';
        window.document.body.appendChild(bugDiv);
    }

    function addBug(html, fn) {
        var bug = window.document.createElement('div');
        bug.className = 'bug';
        bug.innerHTML = html;
        bug.addEventListener('mousedown', fn, false);

        bugDiv.appendChild(bug);
        return bug;
    }

    function onKeyDown(evt) {
        var keyEvent = evt;
        switch (keyEvent.keyCode) {
            case 32:
                if (keyEvent.shiftKey) {
                    prevSlide(evt);
                } else {
                    nextSlide(evt);
                }
                break;
            case 27:
                if (printMode) {
                    switchPrintMode(evt);
                } else {
                    switchPresentMode(evt);
                }
                break;
            case 36:
                showSlide(0);
                break;
            case 35:
                showSlide(slides.length - 1);
                break;
            case 37:
            case 38:
                prevSlide(evt);
                break;
            case 39:
            case 40:
                nextSlide(evt);
                break;
        }
    }

    function onClick(evt) {
        var mouse = evt;
        if (mouse.clientX < 480) {
            prevSlide(evt);
        } else {
            nextSlide(evt);
        }
    }
})(Slides || (Slides = {}));

Slides.init();
//# sourceMappingURL=slides.js.map
