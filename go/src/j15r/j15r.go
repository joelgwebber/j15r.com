package main

// Templates.
import (
	"fmt"
	"log"
	"flag"
	"strconv"
	"strings"
	"io/ioutil"
	"net/http"
	"path/filepath"
	"html/template"
	"github.com/kellegous/pork"
	"github.com/russross/blackfriday"
)

const templates = `
{{define "index"}}
<!DOCTYPE html>
<html>
	{{template "head"}}

	<body>
		<script src='jsx/main.js'></script>

		{{range .Articles}}<a href='{{.Url}}'>{{.Title}}</a><br>
		{{end}}
	</body>
</html>
{{end}}

{{define "article"}}
<!DOCTYPE html>
<html>
	{{template "head"}}

	<body>
	<h1>{{.Title}}</h1>

	{{.Content}}

	{{template "disqus-crap" .OrigUrl}}
	{{template "analytics-crap"}}
	{{template "pardot-crap"}}
	</body>
</html>
{{end}}

{{define "head"}}
	<head>
		<title>j15r.com</title>
		<link rel='stylesheet' href='/scss/j15r.css'>
	</head>
{{end}}

{{define "disqus-crap"}}
  <div id="disqus_thread"></div>
  <script type="text/javascript">
    var disqus_shortname = 'j15r';
		{{if .}}var disqus_url = "http://blog.j15r.com{{.}}";{{end}}
    (function() {
        var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
        dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
    })();
	</script>
	<noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
	<a href="http://disqus.com" class="dsq-brlink">comments powered by <span class="logo-disqus">Disqus</span></a>
{{end}}

{{define "pardot-crap"}}
	<script type='text/javascript'>
		piAId = &#39;8312&#39;;
		piCId = &#39;55716&#39;;
		(function() {
		  function async_load(){
		  var s = document.createElement(&#39;script&#39;); s.type = &#39;text/javascript&#39;;
		  s.src = (&#39;https:&#39; == document.location.protocol ? &#39;https://pi&#39; : &#39;http://cdn&#39;) + &#39;.pardot.com/pd.js&#39;;
		  var c = document.getElementsByTagName(&#39;script&#39;)[0]; c.parentNode.insertBefore(s, c);
		}
		if(window.attachEvent) { window.attachEvent(&#39;onload&#39;, async_load); }
		  else { window.addEventListener(&#39;load&#39;, async_load, false); }
		})();
	</script>
{{end}}

{{define "analytics-crap"}}
	<script type='text/javascript'>
	  var _gaq = _gaq || [];
	  _gaq.push([&#39;_setAccount&#39;, &#39;UA-29878232-1&#39;]);
	  _gaq.push([&#39;_trackPageview&#39;]);
	  (function() {
	    var ga = document.createElement(&#39;script&#39;); ga.type = &#39;text/javascript&#39;; ga.async = true;
	    ga.src = (&#39;https:&#39; == document.location.protocol ? &#39;https://ssl&#39; : &#39;http://www&#39;) + &#39;.google-analytics.com/ga.js&#39;;
	    var s = document.getElementsByTagName(&#39;script&#39;)[0]; s.parentNode.insertBefore(ga, s);
	  })();
	</script>
{{end}}
`

type indexData struct {
	Articles []article
}

type articleData struct {
	Title   string
	Content template.HTML
	OrigUrl string
}

var tmpl *template.Template

// Article index
type simpleDate struct {
	Year  int
	Month int
	Date  int
}

type article struct {
	Title   string
	Url     string
	RelPath string
	Date    simpleDate
}

var articles []article
var articleIndex map[string]article

func initArticleIndex() error {
	articles = make([]article, 0)
	articleIndex = make(map[string]article)

	yearDirs, err := ioutil.ReadDir("articles")
	if err != nil {
		return err
	}

	for _, yearDir := range yearDirs {
		if !yearDir.IsDir() {
			continue
		}
		year, err := strconv.Atoi(yearDir.Name())
		if err != nil {
			continue
		}

		yearDirPath := filepath.Join("articles", yearDir.Name())
		monthDirs, err := ioutil.ReadDir(yearDirPath)
		if err != nil {
			return err
		}

		for _, monthDir := range monthDirs {
			if !monthDir.IsDir() {
				continue
			}
			month, err := strconv.Atoi(monthDir.Name())
			if err != nil {
				continue
			}

			monthDirPath := filepath.Join(yearDirPath, monthDir.Name())
			dateDirs, err := ioutil.ReadDir(monthDirPath)
			if err != nil {
				return err
			}

			for _, dateDir := range dateDirs {
				if !dateDir.IsDir() {
					continue
				}
				date, err := strconv.Atoi(dateDir.Name())
				if err != nil {
					continue
				}

				dateDirPath := filepath.Join(monthDirPath, dateDir.Name())
				articleFiles, err := ioutil.ReadDir(dateDirPath)
				for _, articleFile := range articleFiles {
					filename := articleFile.Name()
					if articleFile.IsDir() || !strings.HasSuffix(filename, ".md") {
						continue
					}

					strippedName := filename[0 : len(filename)-3]
					title := strings.Replace(strippedName, "_", " ", -1)
					dir := fmt.Sprintf("/%v/%v/%v", yearDir.Name(), monthDir.Name(), dateDir.Name())
					url := fmt.Sprintf("/article%v/%v", dir, strippedName)
					relPath := fmt.Sprintf("%v/%v", dir, filename)

					art := article{
						Title: title,
						Url: url,
						RelPath: relPath,
						Date: simpleDate{year, month, date},
					}
					articles = append(articles, art)
					articleIndex[url] = art
				}
			}
		}
	}

	return nil
}

func indexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")
	err := tmpl.ExecuteTemplate(w, "index", &indexData{articles})
	if err != nil {
		http.Error(w, "Unexpected error", 500)
	}
}

var originalUrls = map[string]string{
	"/article/2011/12/15/Box2D_as_a_Measure_of_Runtime_Performance":             "/2011/12/for-those-unfamiliar-with-it-box2d-is.html",
	"/article/2010/09/16/IE9_Memory_Leaks_Finally_Declared_Dead":                "/2010/09/ie9-memory-leaks-finally-declared-dead.html",
	"/article/2010/09/16/An_Ugly_Bug_in_the_IE9_Beta":                           "/2010/09/ugly-bug-in-ie9-beta.html",
	"/article/2010/04/02/Quake_II_in_HTML5_--_What_Does_This_Really_Mean":       "/2010/04/quake-ii-in-html5-what-does-this-really.html",
	"/article/2009/09/11/Javascript_Variables,_Continued":                       "/2009/09/javascript-variables-continued.html",
	"/article/2009/08/10/Where_should_I_define_Javascript_variables":            "/2009/08/where-should-i-define-javascript.html",
	"/article/2009/07/12/Memory_Leaks_in_IE8":                                   "/2009/07/memory-leaks-in-ie8.html",
	"/article/2009/07/12/GWT,_Javascript,_and_the_Correct_Level_of_Abstraction": "/2009/07/note-i-originally-posted-this-last.html",
	"/article/2007/09/17/IE's_Memory_Leak_Fix_Greatly_Exaggerated":              "/2007/09/ies-memory-leak-fix-greatly-exaggerated.html",
	"/article/2007/01/07/This_Old_Blog":                                         "/2007/01/this-old-blog.html",
	"/article/2005/07/11/Google_Maps_Information":                               "/2005/07/google-maps-information.html",
	"/article/2005/06/22/Another_Word_or_Two_on_Memory_Leaks":                   "/2005/06/another-word-or-two-on-memory-leaks.html",
	"/article/2005/06/06/Drip_0.2":                                              "/2005/06/drip-02.html",
	"/article/2005/06/04/Drip_Redux":                                            "/2005/06/drip-redux.html",
	"/article/2005/05/31/Drip:_IE_Leak_Detector":                                "/2005/05/drip-ie-leak-detector.html",
	"/article/2005/04/07/More_Maps":                                             "/2005/04/more-maps.html",
	"/article/2005/03/15/Ajax_Buzz":                                             "/2005/03/ajax-buzz.html",
	"/article/2005/02/12/Making_the_Back_Button_Dance":                          "/2005/02/making-back-button-dance.html",
	"/article/2005/02/11/Still_More_Fun_with_Maps":                              "/2005/02/still-more-fun-with-maps.html",
	"/article/2005/02/09/Mapping_Google":                                        "/2005/02/mapping-google.html",
	"/article/2005/01/02/DHTML_Leaks_Like_a_Sieve":                              "/2005/01/dhtml-leaks-like-sieve.html",
	"/article/2004/12/20/The_Insanity_of_HTTP_Compression":                      "/2004/12/insanity-of-http-compression.html",
}

func articleHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "text/html")

	article, exists := articleIndex[r.URL.Path]
	if !exists { 
		http.NotFound(w, r)
		return
	}

	mdBytes, err := ioutil.ReadFile(filepath.Join("articles", article.RelPath))
	if err != nil {
		http.NotFound(w, r)
		return
	}

	md := blackfriday.MarkdownCommon(mdBytes)

	err = tmpl.ExecuteTemplate(w, "article", &articleData{
		Title:   article.Title,
		Content: template.HTML(md),
		OrigUrl: originalUrls[r.URL.Path],
	})
	if err != nil {
		http.Error(w, "Unexpected error", 500)
	}
}

func initTemplates() error {
	tmp, err := template.New("index").Parse(templates)
	if err != nil {
		return err
	}
	tmpl = tmp
	return nil
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

	// Load article index.
	err = initArticleIndex()
	if err != nil {
		log.Fatalf("Unable to load article index: %v", err)
		return
	}

	// Setup a simple router.
	r := pork.NewRouter(func(status int, r *http.Request) {
		log.Printf("%d %s %s %s", status, r.RemoteAddr, r.Method, r.URL.String())
	}, nil, nil)

	config := pork.Config{
		Level: pork.None,
	}

	r.HandleFunc("/", indexHandler)
	r.HandleFunc("/article/", articleHandler)

	r.Handle("/slides/", pork.Content(&config, http.Dir(".")))
	r.Handle("/photo/", pork.Content(&config, http.Dir(".")))
	r.Handle("/voyageur/", pork.Content(&config, http.Dir(".")))

	scssContent := pork.Content(&config, http.Dir("."))
	jsxContent := pork.Content(&config, http.Dir("."))

	r.Handle("/scss/", scssContent)
	r.Handle("/jsx/", jsxContent)

	http.ListenAndServe(*addr, r)
}
