package main

import (
	"encoding/json"
	"fmt"
	"github.com/kellegous/pork"
	"j15r/gute"
	"log"
	"net/http"
	"strconv"
)

var bookIndex gute.Index

// HTTP handler for retrieving a book's pages.
func pageHandler(w http.ResponseWriter, r *http.Request) {
	bookId := r.URL.Query().Get("bookId")
	firstPage, _ := strconv.ParseInt(r.URL.Query().Get("firstPage"), 10, 32)
	pageCount, _ := strconv.ParseInt(r.URL.Query().Get("pageCount"), 10, 32)

	book, err := gute.GetBook(bookId)
	if err != nil {
		log.Printf("Not found: %v", bookId)
		http.NotFound(w, r)
		return
	}

	if int(firstPage) < 0 || int(firstPage) >= book.ChunkCount || int(firstPage+pageCount) > book.ChunkCount {
		log.Printf("Out of range (%v) : %v + %v of %v", bookId, firstPage, pageCount, book.ChunkCount)
		http.NotFound(w, r)
		return
	}

	w.Header().Add("Content-Type", book.ContentType)

	for i := firstPage; i < firstPage+pageCount; i++ {
		w.Write([]byte(book.Chunks[i]))
		if i < firstPage+pageCount-1 {
			w.Write([]byte("\u0000"))
		}
	}
}

// HTTP handler for retrieving a book's metadata.
func bookHandler(w http.ResponseWriter, r *http.Request) {
	bookId := r.URL.Query().Get("bookId")

	book, err := gute.GetBook(bookId)
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

// HTTP handler for retrieving a raw index of books.
func bookIndexHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("Content-Type", "text/html; charset=utf-8")
	w.Write([]byte("<!DOCTYPE html><html><body>"))

	for bookId, entry := range bookIndex {
		w.Write([]byte(fmt.Sprintf("<a href='/gutenberg/#%s'>%s</a><br>", bookId, entry.Title)))
	}

	w.Write([]byte("</body></html>"))
}

// InitGutenberg initializes the index and HTTP handlers for 'Little Gutenberg'.
func InitGutenberg(r pork.Router) error {
	var err error
	bookIndex, err = gute.LoadIndex()
	if err != nil {
		fmt.Println(err)
		return err
	}

	config := pork.Config{Level: pork.None}
	r.Handle("/gutenberg/", pork.Content(&config, http.Dir(".")))
	r.HandleFunc("/gutenberg/index", bookIndexHandler)
	r.HandleFunc("/gutenberg/book", bookHandler)
	r.HandleFunc("/gutenberg/page", pageHandler)

	return nil
}
