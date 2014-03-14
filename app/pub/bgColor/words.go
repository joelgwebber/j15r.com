package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"math/rand"
)

func readWords() [][]byte {
	wordBytes, err := ioutil.ReadFile("/usr/share/dict/words")
	if err != nil {
		panic(err)
	}
	return bytes.Split(wordBytes, []byte("\n"))
}

func valueOf(b byte) byte {
	if b >= byte('A') && b <= byte('F') {
		return 10 + b - byte('A')
	}
	if b >= byte('a') && b <= byte('f') {
		return 10 + b - byte('a')
	}
	if b >= byte('0') && b <= byte('9') {
		return b - byte('0')
	}
	return 0
}

var leetNumbers = map[byte][]byte{
	byte('l'): []byte{byte('1')},
	byte('L'): []byte{byte('1')},
	byte('z'): []byte{byte('2')},
	byte('Z'): []byte{byte('2')},
	byte('b'): []byte{byte('3'), byte('6'), byte('8')},
	byte('B'): []byte{byte('3'), byte('8'), byte('6')},
	byte('e'): []byte{byte('3')},
	byte('E'): []byte{byte('3')},
	byte('a'): []byte{byte('4')},
	byte('A'): []byte{byte('4')},
	byte('s'): []byte{byte('5')},
	byte('S'): []byte{byte('5')},
	byte('g'): []byte{byte('6'), byte('9')},
	byte('G'): []byte{byte('6'), byte('9')},
	byte('t'): []byte{byte('7')},
	byte('T'): []byte{byte('7')},
}

func leetsFor(ch byte) []byte {
	if leets, exists := leetNumbers[ch]; exists {
		return leets
	}
	return make([]byte, 0)
}

func replaceChar(word []byte, idx int, ch byte) []byte {
	newWord := make([]byte, len(word))
	copy(newWord, word)
	newWord[idx] = ch
	return newWord
}

func permuteWord(word []byte) [][]byte {
	perms := make([][]byte, 1)
	perms[0] = word

	firstLeets := leetsFor(word[0])
	secondLeets := leetsFor(word[1])

	for _, first := range firstLeets {
		replaced := replaceChar(word, 0, first)
		perms = append(perms, replaced)
		for _, second := range secondLeets {
			perms = append(perms, replaceChar(replaced, 1, second))
		}
	}

	for _, second := range secondLeets {
		perms = append(perms, replaceChar(word, 1, second))
	}

	return perms
}

func colorOf(word []byte) byte {
	i := 0
	for ; i < len(word)-2; i++ {
		if valueOf(word[i]) != 0 {
			break
		}
	}
	return (valueOf(word[i]) << 4) + valueOf(word[i+1])
}

func main() {
	words := readWords()

	lenMap := make(map[int]map[byte][]string)
	for _, word := range words {
		l := len(word)
		if l < 5 || l > 10 {
			continue
		}

		for _, perm := range permuteWord(word) {
			colorMap, exists := lenMap[l]
			if !exists {
				colorMap = make(map[byte][]string)
				lenMap[l] = colorMap
			}

			c := colorOf(perm)
			wordArr, exists := colorMap[c]
			if !exists {
				wordArr = make([]string, 0)
			}
			colorMap[c] = append(wordArr, string(perm))
		}
	}

	fmt.Println("var _words_ = {")
	for l := 5; l <= 8; l++ {
		fmt.Printf(" %d: {\n", l)
		for c, wordArr := range lenMap[l] {
			fmt.Printf("  0x%02x: [", c)
			for i := 0; i < 32; i++ {
				fmt.Printf("\"%s\", ", wordArr[rand.Intn(len(wordArr))])
			}
			fmt.Println("],")
		}
		fmt.Println(" },")
	}
	fmt.Println("}")
}
