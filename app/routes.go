package app

import (
	"net/http"
	"j15r"
)

func init() {
	http.HandleFunc("/", j15r.IndexHandler)
}
