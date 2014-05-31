package j15r

import (
	"bytes"
	"encoding/xml"
	"fmt"
	"github.com/russross/blackfriday"
	"html/template"
	"io"
	"io/ioutil"
	"net/http"
	"os"
	"path/filepath"
	"sort"
	"strconv"
	"strings"
	"log"
)

const blogTemplate = `
{{define "blog"}}
<!DOCTYPE html>
<html>
  {{template "head" .Title}}

  <body>
  <script type='text/javascript' src='/slides/decks/syntax/scripts/shCore.js'></script>
  <script type='text/javascript' src='/slides/decks/syntax/scripts/shBrushJava.js'></script>
  <script type='text/javascript' src='/slides/decks/syntax/scripts/shBrushJScript.js'></script>
  <script type='text/javascript' src='/slides/decks/syntax/scripts/shBrushCss.js'></script>
  <link type='text/css' rel='stylesheet' href='/slides/decks/syntax/styles/shCoreEclipse.css'/>
  <script type='text/javascript'>
    SyntaxHighlighter.defaults['gutter'] = false;
    SyntaxHighlighter.all();
  </script>

	<div class='header'>
		{{template "header-main"}}
		<div class='intro-wrapper'>
			<div class='intro'>
				<h1>{{.Title}}</h1>
			</div>
		</div>
	</div>

	<div class='content'>
		{{.Content}}
		<hr>
		<b>
		Note: I'm moving to G+ comments, but also want to preserve the old blog comments in read-only form.
		I just haven't gotten around to that last part yet, so they're temporarily unavailable.
		</b>
	</div>

	{{template "gplus-crap" .Path}}
  </body>
</html>
{{end}}

{{define "gplus-crap"}}
<script src="https://apis.google.com/js/plusone.js"></script>
<div style="margin-top: 32px; text-align: center;">
	<div class="g-comments"
			data-href="http://www.j15r.com{{.}}"
			data-first_party_property="BLOGGER"
			data-view_type="FILTERED_POSTMOD">
	</div>
</div>
{{end}}

{{define "atom"}}
<feed xmlns='http://www.w3.org/2005/Atom' xmlns:thr='http://purl.org/syndication/thread/1.0' xmlns:openSearch='http://a9.com/-/spec/opensearchrss/1.0/'>
	<id>http://j15r.com/</id>
  <title type='text'>as simple as possible, but no simpler</title>
  <updated>{{template "atom-date" .Updated}}</updated>

  <link rel='self' type='application/atom+xml' href='http://j15r.com/blog/feed'/>
  <link rel='alternate' type='text/html' href='http://j15r.com/'/>

  <author>
    <name>Joel Webber</name>
    <uri>http://j15r.com/</uri>
    <email>jgw@pobox.com</email>
  </author>

	<openSearch:totalResults>{{len .Articles}}</openSearch:totalResults>
	<openSearch:startIndex>1</openSearch:startIndex>
	<openSearch:itemsPerPage>{{len .Articles}}</openSearch:itemsPerPage>

  {{range .Articles}}
  <entry>
		<id>http://j15r.com{{.Article.Url}}</id>
	  <published>{{template "atom-date" .Article.Date}}</published>
	  <updated>{{template "atom-date" .Article.Date}}</updated>
    <title type='text'>{{.Article.Title}}</title>
    <content type='html'>{{.Content}}</content>
  </entry>
  {{end}}
</feed>
{{end}}

{{define "atom-date"}}{{printf "%04d" .Year}}-{{printf "%02d" .Month}}-{{printf "%02d" .Date}}T00:00:00Z{{end}}
`

var originalUrls = map[string]string{
	"/blog/2011/12/15/Box2D_as_a_Measure_of_Runtime_Performance":             "/2011/12/for-those-unfamiliar-with-it-box2d-is.html",
	"/blog/2010/09/16/IE9_Memory_Leaks_Finally_Declared_Dead":                "/2010/09/ie9-memory-leaks-finally-declared-dead.html",
	"/blog/2010/09/16/An_Ugly_Bug_in_the_IE9_Beta":                           "/2010/09/ugly-bug-in-ie9-beta.html",
	"/blog/2010/04/02/Quake_II_in_HTML5_--_What_Does_This_Really_Mean":       "/2010/04/quake-ii-in-html5-what-does-this-really.html",
	"/blog/2009/09/11/Javascript_Variables,_Continued":                       "/2009/09/javascript-variables-continued.html",
	"/blog/2009/08/10/Where_should_I_define_Javascript_variables":            "/2009/08/where-should-i-define-javascript.html",
	"/blog/2009/07/12/Memory_Leaks_in_IE8":                                   "/2009/07/memory-leaks-in-ie8.html",
	"/blog/2009/07/12/GWT,_Javascript,_and_the_Correct_Level_of_Abstraction": "/2009/07/note-i-originally-posted-this-last.html",
	"/blog/2007/09/17/IE's_Memory_Leak_Fix_Greatly_Exaggerated":              "/2007/09/ies-memory-leak-fix-greatly-exaggerated.html",
	"/blog/2007/01/07/This_Old_Blog":                                         "/2007/01/this-old-blog.html",
	"/blog/2005/07/11/Google_Maps_Information":                               "/2005/07/google-maps-information.html",
	"/blog/2005/06/22/Another_Word_or_Two_on_Memory_Leaks":                   "/2005/06/another-word-or-two-on-memory-leaks.html",
	"/blog/2005/06/06/Drip_0.2":                                              "/2005/06/drip-02.html",
	"/blog/2005/06/04/Drip_Redux":                                            "/2005/06/drip-redux.html",
	"/blog/2005/05/31/Drip:_IE_Leak_Detector":                                "/2005/05/drip-ie-leak-detector.html",
	"/blog/2005/04/07/More_Maps":                                             "/2005/04/more-maps.html",
	"/blog/2005/03/15/Ajax_Buzz":                                             "/2005/03/ajax-buzz.html",
	"/blog/2005/02/12/Making_the_Back_Button_Dance":                          "/2005/02/making-back-button-dance.html",
	"/blog/2005/02/11/Still_More_Fun_with_Maps":                              "/2005/02/still-more-fun-with-maps.html",
	"/blog/2005/02/09/Mapping_Google":                                        "/2005/02/mapping-google.html",
	"/blog/2005/01/02/DHTML_Leaks_Like_a_Sieve":                              "/2005/01/dhtml-leaks-like-sieve.html",
	"/blog/2004/12/20/The_Insanity_of_HTTP_Compression":                      "/2004/12/insanity-of-http-compression.html",
}

type articleData struct {
	Title   string
	Content template.HTML
	Path    string
	OrigUrl string
}

type fullArticle struct {
	Article *Article
	Content template.HTML
}

type atomData struct {
	Updated  SimpleDate
	Articles []fullArticle
}

func (a atomData) Len() int      { return len(a.Articles) }
func (a atomData) Swap(i, j int) { a.Articles[i], a.Articles[j] = a.Articles[j], a.Articles[i] }
func (a atomData) Less(i, j int) bool {
	return a.Articles[i].Article.Date.abs() > a.Articles[j].Article.Date.abs()
}

var reverseUrls = make(map[string]string)

type blog struct {
	tmpl         *template.Template
	articles     []*Article
	articleIndex map[string]*Article
}

func (b *blog) atomServer() func(http.ResponseWriter, *http.Request) {
	return func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/atom+xml")

		fullArticles := make([]fullArticle, len(b.articles))
		for i, article := range b.articles {
			content, err := b.renderContent(article.Url)
			if err != nil {
				http.NotFound(w, r)
				return
			}
			var buf bytes.Buffer
			xml.Escape(&buf, []byte(content))

			fullArticles[i] = fullArticle{
				Article: article,
				Content: template.HTML(buf.String()),
			}
		}

		atom := &atomData{
			Articles: fullArticles,
		}
		sort.Sort(atom)
		atom.Updated = atom.Articles[0].Article.Date

		err := b.tmpl.ExecuteTemplate(w, "atom", atom)
		if err != nil {
			http.Error(w, err.Error(), 500)
		}
	}
}

func (b *blog) draftServer() func(http.ResponseWriter, *http.Request) {
	return func(w http.ResponseWriter, r *http.Request) {
		path := r.URL.Path

		// If it's a raw file, just serve it.
		fi, err := os.Stat(path[1:])
		if err == nil && !fi.IsDir() {
			http.ServeFile(w, r, path[1:])
			return
		}

		err = b.renderArticle(path, w)
		if err != nil {
			http.Error(w, err.Error(), 500)
		}
	}
}

func (b *blog) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path

	// If it's a raw file, just serve it.
	fi, err := os.Stat(path[1:])
	if err == nil && !fi.IsDir() {
		http.ServeFile(w, r, path[1:])
		return
	}

	// If it's an image request, but not found on-disk, serve a default image.
	if strings.HasSuffix(path, ".jpg") {
		log.Printf(">>> " + path)
		http.ServeFile(w, r, "pub/img/blog.png")
		return
	}

	// Otherwise, assume it refers to an article.
	w.Header().Set("Content-Type", "text/html")
	_, exists := b.articleIndex[path]
	if !exists {
		newUrl, exists := reverseUrls[path]
		if exists {
			http.Redirect(w, r, newUrl, 301)
		} else {
			http.NotFound(w, r)
		}
		return
	}

	err = b.renderArticle(path, w)
	if err != nil {
		http.Error(w, err.Error(), 500)
	}
}

func (b *blog) renderArticle(path string, w io.Writer) error {
	var title string
	article := b.articleIndex[path]
	if article != nil {
		title = article.Title
	}

	md, err := b.renderContent(path)
	if err != nil {
		return err
	}

	return b.tmpl.ExecuteTemplate(w, "blog", &articleData{
			Title:   title,
			Content: template.HTML(md),
			Path: path,
			OrigUrl: originalUrls[path],
		})
}

func (b *blog) renderContent(path string) (string, error) {
	relPath := fmt.Sprintf("%v.md", path[1:])
	mdBytes, err := ioutil.ReadFile(relPath)
	if err != nil {
		return "", err
	}

	htmlBytes := blackfriday.MarkdownCommon(mdBytes)
	return string(htmlBytes), nil
}

func (b *blog) initArticleIndex() error {
	b.articles = make([]*Article, 0)
	b.articleIndex = make(map[string]*Article)

	yearDirs, err := ioutil.ReadDir("blog")
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

		yearDirPath := filepath.Join("blog", yearDir.Name())
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
					url := fmt.Sprintf("/blog%v/%v", dir, strippedName)
					iconUrl := fmt.Sprintf("/blog%v/%v.jpg", dir, strippedName)

					art := &Article{
						Title: title,
						Url:   url,
						Icon:  iconUrl,
						Date:  SimpleDate{year, month, date},
					}
					b.articles = append(b.articles, art)
					b.articleIndex[url] = art
				}
			}
		}
	}

	return nil
}

func (b *blog) GetArticles() []*Article {
	return b.articles
}

func InitBlog(tmpl *template.Template) (ArticleProvider, error) {
	var b = &blog{tmpl: tmpl}
	b.initArticleIndex()

	// Handlers.
	http.Handle("/blog/", b)

	// Special-cases ("/2011/", et al) to URLs from the old Blogger site.
	http.Handle("/2011/", b)
	http.Handle("/2010/", b)
	http.Handle("/2009/", b)
	http.Handle("/2007/", b)
	http.Handle("/2005/", b)
	http.Handle("/2004/", b)

	// Atom (old & new).
	http.HandleFunc("/feeds/posts/default", b.atomServer())
	http.HandleFunc("/blog/feed", b.atomServer())

	// Drafts.
	http.HandleFunc("/blog/drafts/", b.draftServer())

	// Calculate the reverse url map.
	for k, v := range originalUrls {
		reverseUrls[v] = k
	}

	_, err := tmpl.Parse(blogTemplate)
	if err != nil {
		return nil, err
	}

	return b, nil
}
