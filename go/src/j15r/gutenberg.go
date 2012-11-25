package main

import (
	"log"
	"fmt"
	"strconv"
	"strings"
	"os"
	"io/ioutil"
	"encoding/json"
	"net/http"
	"github.com/kellegous/pork"
	"j15r/gute"
)

const (
	PAGE_SIZE = 1024
)

type BookSummary struct {
	WordCount  int
	ChunkCount int
}

type Book struct {
	BookSummary
	Chunks []string
}

var idx gute.Index
var bookCache map[string]*Book

func readFromGutenberg(bookId string) ([]byte, error) {
	log.Printf("Fetching %v from Gutenberg", bookId)
	url := fmt.Sprintf("http://www.gutenberg.lib.md.us/%s", bookId)
	rsp, err := http.Get(url)
	if err != nil {
		return nil, err
	}
	defer rsp.Body.Close()
	return ioutil.ReadAll(rsp.Body)
}

func readBook(bookId string) (*Book, error) {
	// See if we have a local copy.
	lastSlash := strings.LastIndex(bookId, "/")
	relPath := fmt.Sprintf("gutenberg/cache/%v.txt", bookId[:lastSlash])
	os.MkdirAll(relPath, 0750)

	relFile := relPath + bookId[lastSlash:]

	bytes, err := ioutil.ReadFile(relFile)
	if err != nil {
		// Fetch from Gutenberg.
		bytes, err = readFromGutenberg(bookId)
		if err != nil {
			return nil, err
		}

		// Cache it locally.
		err = ioutil.WriteFile(relFile, bytes, 0640)
		if err != nil {
			log.Printf("Error writing '%v': %v", relFile, err)
			return nil, err
		}
	} else {
		log.Printf("Read %v from cache", bookId)
	}

	// Process it into something palatable.
	// TODO: Cache the processed form so we can memory map it or something similarly efficient.
	text := string(bytes)
	text = strings.Replace(text, "\r\n\r\n", " <br> <br> ", -1)
	text = strings.Replace(text, "\r\n", " ", -1)

	words := strings.Split(text, " ")
	var chunkCount int = (len(words) / PAGE_SIZE) + 1

	chunks := make([]string, chunkCount)
	for i := 0; i < chunkCount; i++ {
		start := i * PAGE_SIZE
		end := (i + 1) * PAGE_SIZE
		if end > len(words) {
			end = len(words)
		}
		chunks[i] = strings.Join(words[start:end], " ")
	}

	return &Book{
		BookSummary{
			WordCount:  0, // TODO
			ChunkCount: chunkCount,
		},
		chunks,
	}, nil
}

func getBook(bookId string) (*Book, error) {
	book, exists := bookCache[bookId]
	if !exists {
		var err error
		book, err = readBook(bookId)
		if err != nil {
			return nil, err
		}
		bookCache[bookId] = book
	}
	return book, nil
}

func pageHandler(w http.ResponseWriter, r *http.Request) {
	// TODO: Make this stuff UTF-8 when downloading it.
	//   The "-8" suffix might be intended as an indication of UTF-8.
	w.Header().Add("Content-Type", "text/plain; charset=iso-8859-1")

	bookId := r.URL.Query().Get("bookId")
	firstPage, _ := strconv.ParseInt(r.URL.Query().Get("firstPage"), 10, 32)
	pageCount, _ := strconv.ParseInt(r.URL.Query().Get("pageCount"), 10, 32)

	book, err := getBook(bookId)
	if err != nil {
		log.Printf("Not found: %v", bookId)
		http.NotFound(w, r)
		return
	}

	if int(firstPage) < 0 || int(firstPage) >= book.ChunkCount || int(firstPage+pageCount) > book.ChunkCount {
		log.Printf("Out of range (%v) : %v + %v", bookId, firstPage, pageCount)
		http.NotFound(w, r)
		return
	}

	for i := firstPage; i < firstPage+pageCount; i++ {
		w.Write([]byte(book.Chunks[i]))
		if i < firstPage+pageCount-1 {
			w.Write([]byte("\u0000"))
		}
	}
}

func bookHandler(w http.ResponseWriter, r *http.Request) {
	bookId := r.URL.Query().Get("bookId")

	book, err := getBook(bookId)
	if err != nil {
		log.Printf("Not found: %v", bookId)
		http.NotFound(w, r)
		return
	}

	serialized, err := json.Marshal(&book.BookSummary)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.Header().Add("Content-Type", "application/json")
	w.Write(serialized)
}

func bookIndexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Content-Type", "text/html; charset=utf-8")
	w.Write([]byte("<!DOCTYPE html><html><body>"))

	for _, entry := range idx {
		var chosen string
		chosenPriority := 0
		// Pick the highest-priority available format.
		for format, path := range entry.Files {
			// Only pick formats whose paths start with "dirs/" (others aren't available on the mirror).
			if strings.HasPrefix(path, "dirs/") {
				priority := map[string]int{
					"text/plain; charset=\"utf-8\"":      4,
					"text/plain; charset=\"iso-8859-1\"": 3,
					"text/plain; charset=\"us-ascii\"":   2,
					"text/plain":                         1,
				}[format]
				if priority > chosenPriority {
					chosenPriority = priority
					chosen = format
				}
			}
		}

		if len(chosen) == 0 {
			continue
		}
		path := entry.Files[chosen][5:]
		w.Write([]byte(fmt.Sprintf("<a href='/gutenberg/#%s'>%s</a><br>", path, entry.Title)))
	}

	w.Write([]byte("</body></html>"))
}

func InitGutenberg(r pork.Router) error {
	var err error
	idx, err = gute.LoadIndex("index.gob")
	if err != nil {
		fmt.Println(err)
		return err
	}

	bookCache = make(map[string]*Book)

	config := pork.Config{Level: pork.None}
	r.Handle("/gutenberg/", pork.Content(&config, http.Dir(".")))
	r.HandleFunc("/gutenberg/index", bookIndexHandler)
	r.HandleFunc("/gutenberg/book", bookHandler)
	r.HandleFunc("/gutenberg/page", pageHandler)

	return nil
}
