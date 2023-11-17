package strategy

import (
	"encoding/json"
	"github.com/life4/genesis/slices"
	"log/slog"
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
			uri := regexp.MustCompile(`\{.*?}`).ReplaceAllString(endpoint.Uri, `([^/]+)`)
			if regexp.MustCompile("(?m)^"+uri+"$").MatchString(o.Request.RequestURI) && endpoint.Method == o.Request.Method {
				slog.Info("Match endpoint", "endpoint", endpoint.Uri)
				return true
			}
			return false
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
func (o HTTPParam) MustRespondWithJSON(statusCode int, data any) {
	o.Writer.Header().Set("Content-Type", "application/json")
	o.Writer.WriteHeader(statusCode)
	v, err := json.Marshal(data)
	if err != nil {
		panic(err)
	}
	_, err = o.Writer.Write(v)
	if err != nil {
		panic(err)
	}
}
func (o HTTPParam) MustWriteString(str string) {
	_, err := o.Writer.Write([]byte(str))
	if err != nil {
		panic(err)
	}
}

type TCPParam struct {
	Conn net.Conn
}
