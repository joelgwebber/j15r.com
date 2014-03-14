package j15r

import "html/template"

type slides struct {
	tmpl     *template.Template
	articles []*Article
}

func (s *slides) GetArticles() []*Article {
	return s.articles
}

func (s *slides) GetIcon() string {
	return "/s/img/icon-slides.png"
}

func InitSlides(tmpl *template.Template) (ArticleProvider, error) {
	return &slides{
		tmpl: tmpl,
		articles: []*Article{
			&Article{
				Title: "Fast is Better Than Slow",
				Url:   "http://ptgmedia.pearsoncmg.com/imprint_downloads/voicesthatmatter/gwt2007/presentations/Performance_Webber.pdf",
				Icon:  "/s/slides/presentation.jpg",
				Date:  SimpleDate{2007, 12, 3},
			},
			&Article{
				Title: "Creating Widgets",
				Url:   "http://ptgmedia.pearsoncmg.com/imprint_downloads/voicesthatmatter/gwt2007/presentations/CreatingWidgets_Webber.pdf",
				Icon:  "/s/slides/presentation.jpg",
				Date:  SimpleDate{2007, 12, 3},
			},
			&Article{
				Title: "GWT's UI overhaul",
				Url:   "http://www.google.com/events/io/2010/sessions/gwt-ui-overhaul.html",
				Icon:  "/s/slides/presentation.jpg",
				Date:  SimpleDate{2010, 5, 19},
			},
			&Article{
				Title: "Architecting for Performance",
				Url:   "http://www.google.com/events/io/2010/sessions/architecting-performance-gwt.html",
				Icon:  "/s/slides/presentation.jpg",
				Date:  SimpleDate{2010, 5, 19},
			},
			&Article{
				Title: "GWT + HTML5 can do What?!",
				Url:   "http://www.google.com/events/io/2010/sessions/gwt-html5.html",
				Icon:  "/s/slides/presentation.jpg",
				Date:  SimpleDate{2010, 5, 19},
			},
			&Article{
				Title: "Angry Birds on HTML5",
				Url:   "/s/slides/decks/ab.html",
				Icon:  "/s/slides/angrybirds.jpg",
				Date:  SimpleDate{2011, 10, 10},
			},
			&Article{
				Title: "Introduction to Native Client",
				Url:   "/s/slides/decks/nacl.html",
				Icon:  "/s/slides/nacl.jpg",
				Date:  SimpleDate{2012, 3, 21},
			},
			&Article{
				Title: "Introduction to the PlayN Game Library",
				Url:   "/s/slides/decks/playn.html",
				Icon:  "/s/slides/playn.jpg",
				Date:  SimpleDate{2012, 3, 22},
			},
		},
	}, nil
}
