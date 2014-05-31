package j15r

import "html/template"

type jobs struct {
	tmpl     *template.Template
	articles []*Article
}

func (j *jobs) GetArticles() []*Article {
	return j.articles
}

func InitJobs(tmpl *template.Template) (ArticleProvider, error) {
	return &jobs{
		tmpl: tmpl,
		articles: []*Article{
			&Article{
				Title: "ZSoft",
				Icon:  "/s/img/zsoft.jpg",
				Date:  SimpleDate{1991, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/ZSoft_Corporation",
			},
			&Article{
				Title: "Lotus Development",
				Icon:  "/s/img/lotus.jpg",
				Date:  SimpleDate{1992, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/Lotus_Software",
			},
			&Article{
				Title: "Pixel Technologies",
				Icon:  "/s/img/pixel.png",
				Date:  SimpleDate{1993, 0, 0},
			},
			&Article{
				Title: "Heuristic Park",
				Icon:  "/s/img/heuristicpark.jpg",
				Date:  SimpleDate{1995, 0, 0},
				Url:   "http://www.heuristicpark.com/",
			},
			&Article{
				Title: "Holistic Design",
				Icon:  "/s/img/holistic.png",
				Date:  SimpleDate{1997, 0, 0},
				Url:   "http://www.holistic-design.com/",
			},
			&Article{
				Title: "AppForge",
				Icon:  "/s/img/appforge.png",
				Date:  SimpleDate{2000, 0, 0},
				Url:   "http://en.wikipedia.org/wiki/AppForge",
			},
			&Article{
				Title: "Innuvo",
				Icon:  "/s/img/innuvo.png",
				Date:  SimpleDate{2002, 0, 0},
			},
			&Article{
				Title: "Google",
				Icon:  "/s/img/g.jpg",
				Date:  SimpleDate{2005, 7, 31},
				Url:   "http://www.google.com/",
			},
			&Article{
				Title: "Homebase.io",
				Icon:  "/s/img/homebaseio.png",
				Date:  SimpleDate{2012, 3, 5},
				Url:   "http://homebase.io/",
			},
			&Article{
				Title: "FullStory",
				Icon:  "/s/img/fullstory.png",
				Date:  SimpleDate{2014, 1, 1},
				Url:   "http://thefullstory.com/",
			},
		},
	}, nil
}
