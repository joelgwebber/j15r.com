package main

import (
	"net/http"
	"html/template"
	"github.com/kellegous/pork"
)

type slides struct {
	tmpl     *template.Template
	articles []*Article
}

func (s *slides) GetArticles() []*Article {
	return s.articles
}

func InitSlides(r pork.Router, tmpl *template.Template) (ArticleProvider, error) {
	// Just serve the slides up statically.
	config := pork.Config{Level: pork.None}
	r.Handle("/slides/", pork.Content(&config, http.Dir(".")))

	return &jobs{
		tmpl: tmpl,
		articles: []*Article{
			&Article{
				Title: "Introduction to Native Client",
				Url:   "/slides/decks/nacl.html",
				Date:  SimpleDate{2012, 3, 21},
			},
			&Article{
				Title: "Introduction to the PlayN Game Library",
				Url:   "/slides/decks/playn.html",
				Date:  SimpleDate{2012, 3, 22},
			},
			&Article{
				Title: "Angry Birds on HTML5",
				Url:   "/slides/decks/ab.html",
				Date:  SimpleDate{2012, 3, 21},
			},
		},
	}, nil
}
