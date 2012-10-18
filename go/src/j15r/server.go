package main

import (
	"flag"
	"fmt"
	"log"
	"sort"
	"net/http"
	"html/template"
	"github.com/kellegous/pork"
)

const indexTemplate = `
{{define "index"}}
<!DOCTYPE html>
<html>
  {{template "head"}}

  <body>
    {{template "header"}}

    <div class='outer'>
      <div class='content'>
        {{range .YearArticles}}
          <div class='year'>
          <div class='year-header'>{{.Year}}</div>

          {{range .Articles}}
            <a class='article' style='background-image: url({{.Icon}})' {{if .Url}}href='{{.Url}}'{{end}}>
              <div class='date'>{{.Date.Year}}.{{.Date.Month}}.{{.Date.Date}}</div>
              <div class='title'>{{.Title}}</div>
            </a>
          {{end}}
          </div>
        {{end}}
      </div>
    </div>
  </body>
</html>
{{end}}
`

const sharedTemplates = `
{{define "head"}}
  <head>
    <title>j15r.com</title>
    <link rel='stylesheet' href='/scss/j15r.css'>
  </head>
{{end}}

{{define "header"}}
  <div class='header'>
    <div class='header-main'>
      <a href='/'><img src='/img/j15r.png'></a>
      <a class='reflink' href='https://github.com/joelgwebber'><img src='/img/github.png'></a>
      <a class='reflink' href='https://code.google.com/u/joelgwebber/'><img src='/img/googlecode.png'></a>
      <a class='reflink' href='https://plus.google.com/u/0/111111598146968769323'><img src='/img/gplus.png'></a>
      <a class='reflink' href='http://twitter.com/jgw'><img src='/img/twitter.png'></a>
    </div>
    <div class='header-gradient'></div>
  </div>
{{end}}

{{define "pardot-crap"}}
  <script type='text/javascript'>
    piAId = '8312';
    piCId = '55716';
    (function() {
      function async_load(){
      var s = document.createElement('script'); s.type = 'text/javascript';
      s.src = ('https:' == document.location.protocol ? 'https://pi' : 'http://cdn') + '.pardot.com/pd.js';
      var c = document.getElementsByTagName('script')[0]; c.parentNode.insertBefore(s, c);
    }
    if(window.attachEvent) { window.attachEvent('onload', async_load); }
      else { window.addEventListener('load', async_load, false); }
    })();
  </script>
{{end}}

{{define "analytics-crap"}}
  <script type='text/javascript'>
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-29878232-1']);
    _gaq.push(['_trackPageview']);
    (function() {
      var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
      ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
      var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
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

var pub pork.Handler
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

func indexHandler(w http.ResponseWriter, r *http.Request) {
	if r.URL.Path == "/" {
		w.Header().Set("Content-Type", "text/html")

		err := tmpl.ExecuteTemplate(w, "index", &indexData{mergeAndSortArticles()})
		if err != nil {
			http.Error(w, "Unexpected error", 500)
		}
	} else {
		pub.ServeHTTP(w, r)
	}
}

func initTemplates() (err error) {
	tmp := template.New("index")
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

func addProvider(init func(pork.Router, *template.Template) (ArticleProvider, error), r pork.Router) {
	p, err := init(r, tmpl)
	if err != nil {
		panic(fmt.Sprintf("%v", err))
	}
	providers = append(providers, p)
}

func main() {
	// Flags.
	addr := flag.String("addr", ":8080", "The address to use")
	prod := flag.Bool("prod", false, "productionize, and run from the compiled output")
	flag.Parse()

	// Parse site templates.
	err := initTemplates()
	if err != nil {
		log.Fatalf("Unable to initialize site templates: %v", err)
		return
	}

	// Setup a simple router.
	r := pork.NewRouter(func(status int, r *http.Request) {
		// log.Printf("%d %s %s %s", status, r.RemoteAddr, r.Method, r.URL.String())
	}, nil, nil)

	// Article providers.
	addProvider(InitBlog, r)
	addProvider(InitSlides, r)
	addProvider(InitJobs, r)
	addProvider(InitProjects, r)
	addProvider(InitMisc, r)

	// Preprocessed content (scripts and styles).
	config := pork.Config{Level: pork.None}
	pub = pork.Content(&config, http.Dir("pub"))
	if *prod {
		pub.Productionize(http.Dir("out"))
	}

	// Little Gutenberg.
	InitGutenberg(r)

	// Index and static content handler.
	r.HandleFunc("/", indexHandler)

	// Let 'er rip.
	log.Printf("Listening on port %s", *addr)
	err = http.ListenAndServe(*addr, r)
	if err != nil {
		log.Fatal(err)
	}
}
