package model

import (
	"github.com/life4/genesis/slices"
	"regexp"
)

type Service struct {
	Name         string     `json:"name"`
	SourceRoot   string     `json:"source_root"`
	Dependencies []Endpoint `json:"dependencies"`
}

type Endpoint struct {
	ServiceName  string     `json:"service_name"`
	Method       string     `json:"method"`
	Uri          string     `json:"uri"`
	ResponseType CustomType `json:"response_type"`
}

type CustomField struct {
	Name string     `json:"name"`
	Type CustomType `json:"type"`
}

type CustomType struct {
	IsVoid      bool          `json:"is_void"`
	IsPrimitive bool          `json:"is_primitive"`
	IsGeneric   bool          `json:"is_generic"`
	BaseName    string        `json:"base_name"`
	Fields      []CustomField `json:"fields"`
	GenericName []CustomType  `json:"generic_name"`
}

type ServiceList []Service

func (s *ServiceList) SortEndpoints() {
	r := regexp.MustCompile(`\{.*}`)
	for i := range *s {
		(*s)[i].Dependencies = slices.SortBy((*s)[i].Dependencies, func(endpoint Endpoint) int {
			if r.MatchString(endpoint.Uri) {
				return 1
			}
			return -1
		})
	}
}
