package strategy

import (
	"github.com/life4/genesis/slices"
	"mock-server/model"
	"net"
	"net/http"
	"regexp"
)

type TestStrategyHTTP interface {
	HandleHTTP(param HTTPParam)
}
type TestStrategyTCP interface {
	HandleTCP(param TCPParam)
}

type HTTPParam struct {
	Writer   http.ResponseWriter
	Request  *http.Request
	Services model.ServiceList
}

func (o HTTPParam) MatchServiceEndpoint() (model.Service, model.Endpoint, error) {
	var resEndpoint model.Endpoint
	resService, err := slices.Find(o.Services, func(service model.Service) bool {
		endpoint, err := slices.Find(service.Dependencies, func(endpoint model.Endpoint) bool {
			uri := regexp.MustCompile(`\{.*?}`).ReplaceAllString(endpoint.Uri, `(.*?)`)
			return regexp.MustCompile("(?m)^"+uri).MatchString(o.Request.RequestURI) && endpoint.Method == o.Request.Method
		})
		if err != nil {
			return false
		}
		resEndpoint = endpoint
		return true
	})
	if err != nil {
		return model.Service{}, model.Endpoint{}, err
	}
	return resService, resEndpoint, nil
}

type TCPParam struct {
	Conn net.Conn
}
