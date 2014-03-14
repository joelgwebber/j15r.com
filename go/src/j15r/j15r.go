package j15r

import (
	"fmt"
	"html/template"
	"log"
	"net/http"
	"sort"
)

const indexTemplate = `
{{define "index"}}
<!DOCTYPE html>
<html>
  {{template "head" "j15r.com"}}

  <body>
  <div class='top-wrapper'>
		<div class='header'>
			{{template "header-main"}}
		</div>

		<div class='intro-wrapper'>
			<div class='intro'>
				<div style='display:inline-block; margin-right:16px; float:left; font-size:48px;'>Hi.</div>
				I'm Joel Webber. I'm an engineer who occasionally writes about software development, games,
				and a few other odds and ends. Above you'll find a list of ways to reach me. Below you'll
				find a chronology of things I've written and built, places I've worked, and so forth.
			</div>
		</div>
	</div>

  <div class='outer'>
    <div class='content'>
      {{range .YearArticles}}
        <div class='year'>
        <div class='year-header'>{{.Year}}</div>

        {{range .Articles}}
          <a class='article' style='background-image: url({{.Icon}})' {{if .Url}}href='{{.Url}}'{{end}}>
            {{if .Date.Month}}
            <div class='date'>
              {{if .Date.Date}}{{.Date.Date}}{{end}}
              {{monthString .Date.Month}}
            </div>
            {{end}}
            <div class='title'>{{.Title}}</div>
          </a>
        {{end}}
        </div>
      {{end}}
    </div>
  </div>

  {{template "fullstory-crap"}}
  </body>
</html>
{{end}}
`

const sharedTemplates = `
{{define "head"}}
  <head>
    <title>{{.}}</title>
    <link rel='stylesheet' href='/s/j15r.css'>
  </head>
{{end}}

{{define "header-main"}}
    <div class='header-main'>
      <a href='/' class='logo'>as simple as possible, but no simpler</a>
    </div>
    <div class='header-main-right'>
      <a class='reflink' href='mailto:jgw@pobox.com'><img width='24px' height='24px' src='/s/img/email_white.png'></a>
      <a class='reflink' href='/blog/feed'><img width='24px' height='24px' src='/s/img/rss_white.png'></a>
      <a class='reflink' href='https://code.google.com/u/joelgwebber/'><img width='24px' height='24px' src='/s/img/google_icon_white.png'></a>
      <a class='reflink' href='https://github.com/joelgwebber'><img width='24px' height='24px' src='/s/img/github_white.png'></a>
      <a class='reflink' href='http://twitter.com/jgw'><img width='24px' height='24px' src='/s/img/twitter_white.png'></a>
      <a class='reflink' href='https://plus.google.com/u/0/+JoelWebber'><img width='24px' height='24px' src='/s/img/gplus_white.png'></a>
    </div>
{{end}}

{{define "fullstory-crap"}}
  <script>
    var _fs_debug = true;
    var _fs_host='fs-staging.appspot.com',_fs_org='agxzfmZzLXN0YWdpbmdyEAsSA09yZxiAgICAsPvqCww';
    var _fs_ready = function() { };
    (function(w,a,t){a=w.createElement('script');a.async=1;a.src='https://'+_fs_host+'/s/fs.js';w.body.appendChild(a)})(document);
  </script>
{{end}}
`

type ArticleProvider interface {
	GetArticles() []*Article
}

type SimpleDate struct {
	Year  int
	Month int
	Date  int
}

func (d *SimpleDate) abs() int { return d.Year*13*32 + d.Month*32 + d.Date }

type Article struct {
	Title string
	Url   string
	Icon  string
	Date  SimpleDate
	Size  int
}

type yearArticles struct {
	Year     int
	Articles []*Article
}

type indexData struct {
	YearArticles []*yearArticles
}

var tmpl *template.Template
var providers []ArticleProvider

type articlesSortedBackwards []*Article

func (a articlesSortedBackwards) Len() int           { return len(a) }
func (a articlesSortedBackwards) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a articlesSortedBackwards) Less(i, j int) bool { return a[i].Date.abs() > a[j].Date.abs() }

type yearArticlesSortedBackwards []*yearArticles

func (ya yearArticlesSortedBackwards) Len() int           { return len(ya) }
func (ya yearArticlesSortedBackwards) Swap(i, j int)      { ya[i], ya[j] = ya[j], ya[i] }
func (ya yearArticlesSortedBackwards) Less(i, j int) bool { return ya[i].Year > ya[j].Year }

var monthStrings = []string{"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"}

func monthString(i int) string {
	if i >= 1 && i <= 12 {
		return monthStrings[i-1]
	}
	panic("illegal month index")
}

func mergeAndSortArticles() []*yearArticles {
	// Build a map from year to articles-by-year.
	yaMap := make(map[int]*yearArticles, 0)
	for _, p := range providers {
		for _, a := range p.GetArticles() {
			_, exists := yaMap[a.Date.Year]
			if !exists {
				yaMap[a.Date.Year] = &yearArticles{Year: a.Date.Year, Articles: make([]*Article, 0)}
			}
			yaMap[a.Date.Year].Articles = append(yaMap[a.Date.Year].Articles, a)
		}
	}

	// Turn the map into a reverse-sorted array of articles-by-year.
	i := 0
	yas := make([]*yearArticles, len(yaMap))
	for _, ya := range yaMap {
		// Also reverse-sort articles within each year.
		sort.Sort(articlesSortedBackwards(ya.Articles))
		yas[i] = ya
		i++
	}
	sort.Sort(yearArticlesSortedBackwards(yas))

	return yas
}

func IndexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")

	err := tmpl.ExecuteTemplate(w, "index", &indexData{mergeAndSortArticles()})
	if err != nil {
		http.Error(w, "Unexpected error", 500)
	}
}

func initTemplates() (err error) {
	tmp := template.New("index").Funcs(template.FuncMap{"monthString": monthString})

	tmp, err = tmp.Parse(sharedTemplates)
	if err != nil {
		return err
	}
	tmp, err = tmp.Parse(indexTemplate)
	if err != nil {
		return err
	}
	tmpl = tmp

	return nil
}

func addProvider(init func(*template.Template) (ArticleProvider, error)) {
	p, err := init(tmpl)
	if err != nil {
		panic(fmt.Sprintf("%v", err))
	}
	providers = append(providers, p)
}

func init() {
	// Parse site templates.
	err := initTemplates()
	if err != nil {
		log.Fatalf("Unable to initialize site templates: %v", err)
		return
	}

	// Article providers.
	addProvider(InitBlog)
	addProvider(InitSlides)
	addProvider(InitJobs)
	addProvider(InitProjects)
	addProvider(InitMisc)
}
