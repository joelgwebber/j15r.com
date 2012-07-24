package main

import (
  "net/http"
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

func (j *jobs) ServeHTTP(w http.ResponseWriter, r *http.Request) {
}

func InitJobs(r pork.Router, tmpl *template.Template) (ArticleProvider, error) {
  // Just serve the slides up statically.
  config := pork.Config{Level: pork.None}
  r.Handle("/jobs/", pork.Content(&config, http.Dir(".")))

  return &jobs{
    tmpl: tmpl,
    articles: []*Article{
      &Article{
        Title:   "Lotus Development",
        Date:    SimpleDate{1991, 0, 0},
      },
      &Article{
        Title:   "Pixel Technologies",
        Date:    SimpleDate{1992, 0, 0},
      },
      &Article{
        Title:   "Heuristic Park",
        Date:    SimpleDate{1995, 0, 0},
      },
      &Article{
        Title:   "Holistic Design",
        Date:    SimpleDate{1997, 0, 0},
      },
      &Article{
        Title:   "AppForge",
        Date:    SimpleDate{2000, 0, 0},
      },
      &Article{
        Title:   "Innuvo",
        Date:    SimpleDate{2002, 0, 0},
      },
      &Article{
        Title:   "Google",
        Date:    SimpleDate{2005, 7, 31},
      },
      &Article{
        Title:   "Monetology",
        Date:    SimpleDate{2012, 3, 5},
      },
    },
  }, nil
}
