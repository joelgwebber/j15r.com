package main

import (
	"html/template"
	"github.com/kellegous/pork"
)

type jobs struct {
	tmpl     *template.Template
	articles []*Article
}

func (j *jobs) GetArticles() []*Article {
	return j.articles
}

func InitJobs(r pork.Router, tmpl *template.Template) (ArticleProvider, error) {
	return &jobs{
		tmpl: tmpl,
		articles: []*Article{
			&Article{
				Title: "Lotus Development",
				Icon:  "/img/lotus.jpg",
				Date:  SimpleDate{1992, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/Lotus_Software",
			},
			&Article{
				Title: "Pixel Technologies",
				Icon:  "/img/pixel.png",
				Date:  SimpleDate{1993, 0, 0},
			},
			&Article{
				Title: "Heuristic Park",
				Icon:  "/img/hp.png",
				Date:  SimpleDate{1995, 0, 0},
				Url:   "http://www.heuristicpark.com/",
			},
			&Article{
				Title: "Holistic Design",
				Icon:  "/img/holistic.png",
				Date:  SimpleDate{1997, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/Holistic_Design",
			},
			&Article{
				Title: "AppForge",
				Icon:  "/img/appforge.png",
				Date:  SimpleDate{2000, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/AppForge",
			},
			&Article{
				Title: "Innuvo",
				Icon:  "/img/innuvo.png",
				Date:  SimpleDate{2002, 0, 0},
			},
			&Article{
				Title: "Google",
				Icon:  "/img/g.jpg",
				Date:  SimpleDate{2005, 7, 31},
				Url:   "http://www.google.com/",
			},
			&Article{
				Title: "Monetology",
				Icon:  "/img/mn.jpg",
				Date:  SimpleDate{2012, 3, 5},
				Url:   "http://www.monetology.com/",
			},
		},
	}, nil
}
