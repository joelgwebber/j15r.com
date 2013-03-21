package gute

import (
	"encoding/gob"
	"errors"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strings"
	"unicode"
	"unicode/utf8"
)

const (
	PAGE_SIZE    = 1024
	INDEX_NAME   = "index.gob"
	MAX_WORD_LEN = 100
	MAX_WORDS    = 100
)

type IndexEntry struct {
	Title       string
	Language    string
	Path        string
	ContentType string
}

type Index map[string]IndexEntry

type BookSummary struct {
	ContentType string
	WordCount   int
	ChunkCount  int
}

type Book struct {
	BookSummary
	Chunks []string
}

var cache map[string]*Book
var index Index

// Reads the given book from the gutenberg.lib.md.us mirror, by its
// path. The path is different from the bookId, and can only be determined
// via the index.
func readFromGutenberg(bookPath string) ([]byte, error) {
	log.Printf("Fetching %v from Gutenberg", bookPath)
	url := fmt.Sprintf("http://www.gutenberg.lib.md.us/%s", bookPath)
	rsp, err := http.Get(url)
	if err != nil {
		return nil, err
	}
	defer rsp.Body.Close()
	return ioutil.ReadAll(rsp.Body)
}

// Takes the raw book byte stream and processes it into a Book. Performs
// all necessary mangling and formatting to make the client happy.
func processBook(raw []byte, contentType string) *Book {
	words := make([]string, PAGE_SIZE)
	wordPos := 0
	for {
		wordCount := 0
		raw, wordCount = processLine(raw, words[wordPos:])
		wordPos += wordCount
		if wordPos == PAGE_SIZE {
			break
		}
	}

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
			WordCount:   0, // TODO
			ChunkCount:  chunkCount,
			ContentType: contentType,
		},
		chunks,
	}
}

func processLine(raw []byte, words []string) (newRaw []byte, wordCount int) {
	runes := make([]rune, MAX_WORD_LEN)
	pos := 0
	bytePos := 0
	wordCount = 0
	max := len(raw)

	makeWord := func() bool {
		words[wordCount] = string(runes[0:bytePos])
		bytePos = 0
		wordCount++
		return wordCount == len(words)
	}

	for {
		if pos == max {
			break
		}

		r, size := utf8.DecodeRune(raw)
		pos++
		bytePos += size
		raw = raw[bytePos:]

		if r == '\r' {
			if r, size = utf8.DecodeRune(raw); r == '\n' {
				pos++
				bytePos += size
				raw = raw[bytePos:]
			}
			makeWord()
			break
		}

		runes[pos] = r
		if unicode.IsSpace(r) {
			if makeWord() {
				break
			}
		}
	}
	return raw, wordCount
}

// Reads the given book from the local cache, if it's available. If not,
// it will retrieve the book from a Gutenberg mirror, and cache it locally.
func readBook(bookPath string, contentType string) (*Book, error) {
	// See if we have a local copy.
	lastSlash := strings.LastIndex(bookPath, "/")
	relPath := fmt.Sprintf("gutenberg/cache/%v", bookPath[:lastSlash])
	relFile := relPath + bookPath[lastSlash:]
	log.Println(relFile)
	os.MkdirAll(relPath, 0750)

	inFile, err := os.OpenFile(relFile, os.O_RDONLY, 0)
	if err != nil {
		// Fetch from Gutenberg.
		raw, err := readFromGutenberg(bookPath)
		if err != nil {
			return nil, err
		}

		// Process it into something palatable.
		book := processBook(raw, contentType)

		// Cache it locally.
		outFile, err := os.OpenFile(relFile, os.O_CREATE|os.O_WRONLY, 0660)
		if err != nil {
			log.Printf("Error writing '%v': %v", relFile, err)
			return nil, err
		}
		gob.NewEncoder(outFile).Encode(&book)
		outFile.Close()

		// And return it.
		return book, nil
	}

	// Read the cached book.
	log.Printf("Read %v from cache", bookPath)
	var book Book
	gob.NewDecoder(inFile).Decode(&book)
	inFile.Close()
	return &book, nil
}

// GetBook retrieves the given book, by its numeric id.
// It will fetch the book from a Gutenberg mirror if necessary.
func GetBook(bookId string) (*Book, error) {
	// Lazy-init cache
	if cache == nil {
		cache = make(map[string]*Book)
	}

	book, exists := cache[bookId]
	if !exists {
		entry, exists := index[bookId]
		if !exists {
			return nil, errors.New("Unknown book id " + bookId)
		}

		var err error
		book, err = readBook(entry.Path, entry.ContentType)
		if err != nil {
			return nil, err
		}
		cache[bookId] = book
	}
	return book, nil
}

// LoadIndex loads the Gutenberg index from disk.
func LoadIndex() (Index, error) {
	if index != nil {
		return index, nil
	}

	f, err := os.OpenFile(INDEX_NAME, os.O_RDONLY, 0)
	if err != nil {
		return nil, err
	}

	index = make(map[string]IndexEntry)
	dec := gob.NewDecoder(f)
	err = dec.Decode(&index)
	if err != nil {
		return nil, err
	}

	return index, nil
}

func (idx Index) Save() error {
	f, err := os.OpenFile(INDEX_NAME, os.O_CREATE|os.O_WRONLY, 0660)
	if err != nil {
		return err
	}

	enc := gob.NewEncoder(f)
	err = enc.Encode(idx)
	if err != nil {
		return err
	}

	err = f.Close()
	if err != nil {
		return err
	}
	return nil
}
