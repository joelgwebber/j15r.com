package main

import (
	"os"
	"fmt"
	"encoding/xml"
	"j15r/gute"
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
	file, err := os.Open("catalog.rdf")
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
		index[text.Id] = gute.IndexEntry{
			Title:    text.FriendlyTitle,
			Language: text.Language,
			Files:    make(map[string]string),
		}

		for format, file := range fileIndex[text.Id] {
			index[text.Id].Files[format] = file.Path
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
	err = index.Save("index.gob")
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println("Done.")
}
