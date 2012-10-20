package main

import (
  "html/template"
  "github.com/kellegous/pork"
)

type projects struct {
  tmpl     *template.Template
  articles []*Article
}

func (s *projects) GetArticles() []*Article {
  return s.articles
}

func InitProjects(r pork.Router, tmpl *template.Template) (ArticleProvider, error) {
  return &projects{
    tmpl: tmpl,
    articles: []*Article{
      &Article{
        Title: "Ami Pro",
        Url: "http://en.wikipedia.org/wiki/IBM_Lotus_Word_Pro",
        Icon: "/img/amipro.jpg",
        Date:  SimpleDate{1992, 0, 0},
      },
      &Article{
        Title: "Trivial Pursuit",
        Url: "http://www.mobygames.com/game/win3x/trivial-pursuit-interactive-multimedia-game",
        Icon: "/img/tp.jpg",
        Date:  SimpleDate{1993, 0, 0},
      },
      &Article{
        Title: "Cyberdillo",
        Url: "http://www.mobygames.com/game/3do/cyberdillo",
        Icon: "/img/dillo.jpg",
        Date:  SimpleDate{1994, 0, 0},
      },
      &Article{
        Title: "Wizards and Warriors",
        Url: "http://www.gamespot.com/wizards-and-warriors/",
        Icon: "/img/ww.jpg",
        Date:  SimpleDate{1996, 0, 0},
      },
      &Article{
        Title: "Fading Suns: Noble Armada",
        Url: "http://www.gamespot.com/fading-suns-noble-armada/",
        Icon: "/img/noblearmada.jpg",
        Date:  SimpleDate{1998, 0, 0},
      },
      &Article{
        Title: "AppForge Mobile VB",
        Url: "http://en.wikipedia.org/wiki/AppForge",
        Icon: "/img/appforge.png",
        Date:  SimpleDate{2000, 0, 0},
      },
      &Article{
        Title: "Google Web Toolkit",
        Url: "https://developers.google.com/web-toolkit/",
        Icon: "/img/gwt.png",
        Date:  SimpleDate{2006, 5, 16},
      },
      &Article{
        Title: "Google AdWords",
        Url: "https://adwords.google.com/",
        Icon: "/img/adwords.png",
        Date:  SimpleDate{2008, 0, 0},
      },
      &Article{
        Title: "Google Wave",
        Url: "http://en.wikipedia.org/wiki/Google_Wave",
        Icon: "/img/wave.png",
        Date:  SimpleDate{2009, 5, 0},
      },
      &Article{
        Title: "Quake II HTML5",
        Url: "http://code.google.com/p/quake2-gwt-port/",
        Icon: "/img/quake2.jpg",
        Date:  SimpleDate{2010, 4, 1},
      },
      &Article{
        Title: "Angry Birds HTML5",
        Url: "http://chrome.angrybirds.com/",
        Icon: "/img/angrybirds.png",
        Date:  SimpleDate{2011, 5, 0},
      },
    },
  }, nil
}
