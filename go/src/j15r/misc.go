package main

import (
	"html/template"
	"github.com/kellegous/pork"
)

type misc struct {
	tmpl     *template.Template
	articles []*Article
}

func (s *misc) GetArticles() []*Article {
	return s.articles
}

func InitMisc(r pork.Router, tmpl *template.Template) (ArticleProvider, error) {
	return &misc{
		tmpl: tmpl,
		articles: []*Article{
      &Article{
        Title: "Georgia Institute of Technology",
        Url:   "http://cc.gatech.edu/",
        Icon:  "img/gatech.jpg",
        Date:  SimpleDate{1990, 8, 0},
      },
			&Article{
				Title: "Finally graduated!",
				Icon:  "/img/graduation.jpg",
				Date:  SimpleDate{1998, 6, 0},
			},
		},
	}, nil
}
