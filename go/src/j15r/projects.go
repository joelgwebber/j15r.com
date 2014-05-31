package j15r

import "html/template"

type projects struct {
  tmpl     *template.Template
  articles []*Article
}

func (s *projects) GetArticles() []*Article {
  return s.articles
}

func InitProjects(tmpl *template.Template) (ArticleProvider, error) {
  return &projects{
    tmpl: tmpl,
    articles: []*Article{
      &Article{
        Title: "Ami Pro",
        Url: "http://en.wikipedia.org/wiki/IBM_Lotus_Word_Pro",
        Icon: "/s/img/amipro.jpg",
        Date:  SimpleDate{1992, 0, 0},
      },
      &Article{
        Title: "Trivial Pursuit",
        Url: "http://www.mobygames.com/game/win3x/trivial-pursuit-interactive-multimedia-game",
        Icon: "/s/img/tp.jpg",
        Date:  SimpleDate{1993, 0, 0},
      },
      &Article{
        Title: "Cyberdillo",
        Url: "http://www.mobygames.com/game/3do/cyberdillo",
        Icon: "/s/img/dillo.jpg",
        Date:  SimpleDate{1993, 0, 0},
      },
      &Article{
        Title: "Wizards and Warriors",
        Url: "http://www.gamespot.com/wizards-and-warriors/",
        Icon: "/s/img/ww.jpg",
        Date:  SimpleDate{1995, 0, 0},
      },
      &Article{
        Title: "Fading Suns: Noble Armada",
        Url: "http://www.gamespot.com/fading-suns-noble-armada/",
        Icon: "/s/img/noblearmada.jpg",
        Date:  SimpleDate{1997, 0, 0},
      },
      &Article{
        Title: "AppForge Mobile VB",
        Url: "http://en.wikipedia.org/wiki/AppForge",
        Icon: "/s/img/appforge.png",
        Date:  SimpleDate{2000, 0, 0},
      },
      &Article{
        Title: "Google Web Toolkit",
        Url: "https://developers.google.com/web-toolkit/",
        Icon: "/s/img/gwt.png",
        Date:  SimpleDate{2006, 5, 16},
      },
      &Article{
        Title: "Google AdWords",
        Url: "https://adwords.google.com/",
        Icon: "/s/img/adwords.png",
        Date:  SimpleDate{2008, 0, 0},
      },
      &Article{
        Title: "Google Wave",
        Url: "http://en.wikipedia.org/wiki/Google_Wave",
        Icon: "/s/img/wave.png",
        Date:  SimpleDate{2009, 5, 0},
      },
      &Article{
        Title: "Quake II HTML5",
        Url: "http://code.google.com/p/quake2-gwt-port/",
        Icon: "/s/img/quake2.jpg",
        Date:  SimpleDate{2010, 4, 1},
      },
      &Article{
        Title: "Angry Birds HTML5",
        Url: "http://chrome.angrybirds.com/",
        Icon: "/s/img/angrybirds.png",
        Date:  SimpleDate{2011, 5, 0},
      },
    },
  }, nil
}
