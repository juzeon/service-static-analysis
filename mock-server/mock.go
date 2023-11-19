package main

import (
	"encoding/json"
	"fmt"
	"log/slog"
	"mock-server/model"
	"mock-server/strategy"
	"net/http"
	"os"
)

type Mock struct {
	TestType model.TestType
	Services model.ServiceList
}

func NewMock(testType model.TestType, configPath string) (*Mock, error) {
	v, err := os.ReadFile(configPath)
	if err != nil {
		return nil, err
	}
	mock := Mock{TestType: testType}
	err = json.Unmarshal(v, &mock.Services)
	if err != nil {
		return nil, err
	}
	mock.Services.SortEndpoints()
	return &mock, nil
}

func (o *Mock) Run(addr string) error {
	slog.Info("Starting at " + addr)
	if o.TestType.GetTestStrategyLayer() == model.TestStrategyLayerHTTP {
		testStrategyHTTP := o.getTestStrategyHTTP()
		return http.ListenAndServe(addr, http.HandlerFunc(func(writer http.ResponseWriter, request *http.Request) {
			defer func() {
				if err := recover(); err != nil {
					slog.Error("Panic", "error", err)
					writer.Header().Set("Content-Type", "text/plain")
					writer.WriteHeader(500)
					writer.Write([]byte(fmt.Sprintf("panic: %v", err)))
				}
			}()
			testStrategyHTTP.HandleHTTP(strategy.HTTPParam{
				Writer:   writer,
				Request:  request,
				Services: o.Services,
			})
		}))
	} else if o.TestType.GetTestStrategyLayer() == model.TestStrategyLayerTCP {
		panic("not implemented")
	} else {
		panic("not defined layer")
	}
}
func (o *Mock) getTestStrategyHTTP() strategy.TestHTTP {
	switch o.TestType {
	case model.TestTypeUnresolvable:
		return &strategy.TestUnresolvable{}
	case model.TestTypeWrongType:
		return &strategy.TestWrongType{}
	case model.TestTypeTooLarge:
		return &strategy.TestTooLarge{}
	default:
		panic("unknown testType")
	}
}
