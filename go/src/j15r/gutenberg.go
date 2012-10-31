package main

import (
	"errors"
	"strconv"
	"strings"
	"io/ioutil"
	"net/http"
	"github.com/kellegous/pork"
)

const (
	PAGE_SIZE = 1024
)

type Book []string

var bookCache map[string]Book

func readBook(bookId string) (Book, error) {
	// Fetch from Gutenberg.
	rsp, err := http.Get("http://www.gutenberg.org/ebooks/" + bookId + ".txt.utf-8")
	if err != nil {
		return nil, err
	}
	if rsp.ContentLength == 0 {
		return nil, errors.New("ContentLength 0")
	}
	defer rsp.Body.Close()
	bytes, err := ioutil.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

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
	w.Header().Set("Content-Type", "text/plain")
	bookId := r.URL.Query().Get("bookId")
	firstPage, _ := strconv.ParseInt(r.URL.Query().Get("firstPage"), 10, 32)
	pageCount, _ := strconv.ParseInt(r.URL.Query().Get("pageCount"), 10, 32)

	book, exists := bookCache[bookId]
	if !exists {
		book, err := readBook(bookId)
		if err != nil {
			http.NotFound(w, r)
			return
		}
		bookCache[bookId] = book
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
