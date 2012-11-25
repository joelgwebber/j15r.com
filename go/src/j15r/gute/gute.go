package gute

import (
	"os"
	"encoding/gob"
)

type IndexEntry struct {
	Title    string
	Language string
	Files    map[string]string
}

type Index map[string]IndexEntry

func LoadIndex(filename string) (Index, error) {
	f, err := os.OpenFile(filename, os.O_RDONLY, 0)
	if err != nil {
		return nil, err
	}

	idx := make(map[string]IndexEntry)
	dec := gob.NewDecoder(f)
	err = dec.Decode(&idx)
	if err != nil {
		return nil, err
	}

	return idx, nil
}

func (idx Index) Save(filename string) error {
	f, err := os.OpenFile(filename, os.O_CREATE|os.O_WRONLY, 0660)
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
