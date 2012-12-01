package main

import (
	"encoding/xml"
	"errors"
	"fmt"
	"j15r/gute"
	"os"
	"strings"
)

const (
	CATALOG_NAME = "catalog.rdf"
)

type Etext struct {
	Id            string `xml:"ID,attr"`
	FriendlyTitle string `xml:"friendlytitle"`
	Language      string `xml:"language>ISO639-2>value"`
}

type IsFormatOf struct {
	Resource string `xml:"resource,attr"`
}

type File struct {
	Path       string     `xml:"about,attr"`
	IsFormatOf IsFormatOf `xml:"isFormatOf"`
	Format     string     `xml:"format>IMT>value"`
}

type Catalog struct {
	XMLName string  `xml:"RDF"`
	Etexts  []Etext `xml:"etext"`
	Files   []File  `xml:"file"`
}

func loadCatalog() (*Catalog, error) {
	file, err := os.Open(CATALOG_NAME)
	if err != nil {
		return nil, err
	}
	dec := xml.NewDecoder(file)
	dec.Entity = map[string]string{
		"pg":  "",
		"lic": "",
		"f":   "",
	}

	var cat Catalog
	dec.Decode(&cat)
	return &cat, nil
}

// Picks the preferred file path and content type from a group of File objects.
// Returns a non-nil error if no appropriate format is found.
func chooseFormat(formats map[string]File) (string, string, error) {
	var chosen File
	chosenPriority := 0

	// Pick the highest-priority available format.
	for format, file := range formats {
		// Only pick formats whose paths start with "dirs/" (others aren't available on the mirror).
		if strings.HasPrefix(file.Path, "dirs/") {
			priority := map[string]int{
				"text/plain; charset=\"utf-8\"":      4,
				"text/plain; charset=\"iso-8859-1\"": 3,
				"text/plain; charset=\"us-ascii\"":   2,
				"text/plain":                         1,
			}[format]
			if priority > chosenPriority {
				chosenPriority = priority
				chosen = file
			}
		}
	}

	if chosenPriority == 0 {
		return "", "", errors.New("Unable to find acceptable format")
	}
	return chosen.Path[len("/dirs"):], chosen.Format, nil
}

func buildIndex(cat *Catalog) gute.Index {
	// Build a map from id to files, indexed by format.
	fileIndex := make(map[string]map[string]File)
	for _, file := range cat.Files {
		id := file.IsFormatOf.Resource[1:]
		_, exists := fileIndex[id]
		if !exists {
			fileIndex[id] = make(map[string]File)
		}
		fileIndex[id][file.Format] = file
	}

	index := make(map[string]gute.IndexEntry)
	for _, text := range cat.Etexts {
		path, contentType, err := chooseFormat(fileIndex[text.Id])
		if err != nil {
			continue
		}

		index[text.Id] = gute.IndexEntry{
			Title:       text.FriendlyTitle,
			Language:    text.Language,
			Path:        path,
			ContentType: contentType,
		}
	}

	return index
}

func main() {
	fmt.Println("Loading catalog...")
	cat, err := loadCatalog()
	if err != nil {
		fmt.Println(err)
		return
	}

	fmt.Println("Building index...")
	index := buildIndex(cat)

	fmt.Println("Saving index...")
	err = index.Save()
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println("Done.")
}
