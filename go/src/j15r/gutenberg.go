package main

import (
	"log"
	"fmt"
	"strconv"
	"bytes"
	"strings"
	"io/ioutil"
	"net/http"
	"archive/zip"
	"github.com/kellegous/pork"
)

const (
	PAGE_SIZE = 1024
)

type Book []string

var bookCache map[string]Book

func readFromGutenberg(bookId string) ([]byte, error) {
	log.Printf("Fetching %v from Gutenberg", bookId)

	// URL path is "/X/Y/Z/XYZW/XYZW.zip"
	header := bookId[:len(bookId)-1]
	runes := make([]rune, len(header)*2)
	for i := range header {
		runes[i*2] = '/'
		runes[i*2+1] = rune(header[i])
	}

	// Fetch the book.
	url := fmt.Sprintf("http://www.gutenberg.lib.md.us%v/%v/%v.zip", string(runes), bookId, bookId)
	rsp, err := http.Get(url)
	if err != nil || rsp.StatusCode != 200 {
		// Some are stored as "xxxx-8.zip", presumably for "UTF-8".
		url = fmt.Sprintf("http://www.gutenberg.lib.md.us%v/%v/%v-8.zip", string(runes), bookId, bookId)
		rsp, err = http.Get(url)
		if err != nil || rsp.StatusCode != 200 {
			return nil, err
		}
	}

	// Read the full response body.
	defer rsp.Body.Close()
	zipBytes, err := ioutil.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

	// Unzip the first file and return that.
	zr, err := zip.NewReader(bytes.NewReader(zipBytes), rsp.ContentLength)
	if err != nil {
		return nil, err
	}
	zfr, err := zr.File[0].Open()
	if err != nil {
		return nil, err
	}
	defer zfr.Close()
	return ioutil.ReadAll(zfr)
}

func readBook(bookId string) (Book, error) {
	// See if we have a local copy.
	relPath := fmt.Sprintf("gutenberg/cache/%v.txt", bookId)
	bytes, err := ioutil.ReadFile(relPath)
	if err != nil {
		// Fetch from Gutenberg.
		bytes, err = readFromGutenberg(bookId)
		if err != nil {
			return nil, err
		}

		// Cache it locally.
		err = ioutil.WriteFile(relPath, bytes, 0640)
		if err != nil {
			log.Printf("Error writing '%v': %v", relPath, err)
			return nil, err
		}
	} else {
		log.Printf("Read %v from cache", bookId)
	}

	// Process it into something palatable.
	// TODO: Cache the processed form so we can memory map it or something similarly efficient.
	text := strings.Replace(string(bytes), "\r\n\r\n", " <br> <br> ", -1)
	text = strings.Replace(text, "\r\n", " ", -1)

	words := strings.Split(text, " ")
	var pageCount int = (len(words) / PAGE_SIZE) + 1

	book := make([]string, pageCount)
	for i := 0; i < pageCount; i++ {
		start := i * PAGE_SIZE
		end := (i + 1) * PAGE_SIZE
		if end > len(words) {
			end = len(words)
		}
		book[i] = strings.Join(words[start:end], " ")
	}
	return book, nil
}

func bookHandler(w http.ResponseWriter, r *http.Request) {
	// TODO: Make this stuff UTF-8 when downloading it.
	//   The "-8" suffix might be intended as an indication of UTF-8.
	w.Header().Add("Content-Type", "text/plain; charset=iso-8859-1")

	bookId := r.URL.Query().Get("bookId")
	firstPage, _ := strconv.ParseInt(r.URL.Query().Get("firstPage"), 10, 32)
	pageCount, _ := strconv.ParseInt(r.URL.Query().Get("pageCount"), 10, 32)

	book, exists := bookCache[bookId]
	if !exists {
		var err error
		book, err = readBook(bookId)
		if err != nil {
			log.Printf("Not found: %v", bookId)
			http.NotFound(w, r)
			return
		}
		bookCache[bookId] = book
	}

	if int(firstPage) < 0 || int(firstPage) >= len(book) || int(firstPage+pageCount) > len(book) {
		log.Printf("Out of range (%v) : %v + %v", bookId, firstPage, pageCount)
		http.NotFound(w, r)
		return
	}

	for i := firstPage; i < firstPage+pageCount; i++ {
		w.Write([]byte(book[i]))
		if i < firstPage+pageCount-1 {
			w.Write([]byte("\u0000"))
		}
	}
}

func InitGutenberg(r pork.Router) error {
	bookCache = make(map[string]Book)

	config := pork.Config{Level: pork.None}
	r.Handle("/gutenberg/", pork.Content(&config, http.Dir(".")))
	r.HandleFunc("/gutenberg/book", bookHandler)

	return nil
}
