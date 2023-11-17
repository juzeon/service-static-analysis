package main

import (
	"flag"
	"log/slog"
	"mock-server/model"
)

func main() {
	testType := flag.Int("t", 0, "test type")
	configPath := flag.String("c", "../collector/services.json", "config path")
	flag.Parse()
	mock, err := NewMock(model.TestType(*testType), *configPath)
	if err != nil {
		panic(err)
	}
	err = mock.Run(":8123")
	if err != nil {
		slog.Error("An error has occurred", "error", err)
	}
}
