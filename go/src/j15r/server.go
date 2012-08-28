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

    <div class='content'>
      {{range .Articles}}
      	<div class='icon'><img src='{{.Icon}}'></div>
        {{.Date.Year}}.{{.Date.Month}}.{{.Date.Date}} ::
        {{if .Url}}<a href='{{.Url}}'>{{.Title}}</a>{{else}}{{.Title}}{{end}}<br>
      {{end}}
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
      <a href='/'>Home</a>
      <a href='https://github.com/joelgwebber'>Github</a>
      <a href='https://code.google.com/u/joelgwebber/'>Google Code</a>
      <a href='https://plus.google.com/u/0/111111598146968769323'>Google+</a>
      <a href='http://twitter.com/jgw'>Twitter</a>
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
}

type Articles []*Article

func (a Articles) Len() int           { return len(a) }
func (a Articles) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a Articles) Less(i, j int) bool { return a[i].Date.abs() > a[j].Date.abs() }

type indexData struct {
	Articles []*Article
}

var tmpl *template.Template
var providers []ArticleProvider

func mergeAndSortArticles() []*Article {
	result := make(Articles, 0)
	for _, p := range providers {
		result = append(result, p.GetArticles()...)
	}
	sort.Sort(result)
	return result
}

func indexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")
	err := tmpl.ExecuteTemplate(w, "index", &indexData{mergeAndSortArticles()})
	if err != nil {
		http.Error(w, "Unexpected error", 500)
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
	flag.Parse()

	// Parse site templates.
	err := initTemplates()
	if err != nil {
		log.Fatalf("Unable to initialize site templates: %v", err)
		return
	}

	// Setup a simple router.
	r := pork.NewRouter(func(status int, r *http.Request) {
		log.Printf("%d %s %s %s", status, r.RemoteAddr, r.Method, r.URL.String())
	}, nil, nil)

	// Index page handler.
	r.HandleFunc("/", indexHandler)

	// Article providers.
	addProvider(InitBlog, r)
	addProvider(InitSlides, r)
	addProvider(InitJobs, r)
	addProvider(InitProjects, r)
	addProvider(InitMisc, r)

	// Preprocessed content (scripts and styles).
	config := pork.Config{Level: pork.None}
	r.Handle("/scss/", pork.Content(&config, http.Dir(".")))
	r.Handle("/jsx/", pork.Content(&config, http.Dir(".")))
	r.Handle("/img/", pork.Content(&config, http.Dir(".")))

	// Little experiments.
	r.Handle("/photo/", pork.Content(&config, http.Dir(".")))
	r.Handle("/voyageur/", pork.Content(&config, http.Dir(".")))

	// Little Gutenberg.
	InitGutenberg(r)

	// Let 'er rip.
	log.Printf("Listening on port %s", *addr)
	http.ListenAndServe(*addr, r)
}
