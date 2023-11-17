package main

import (
	"log/slog"
	"mock-server/model"
	"os"
)

func main() {
	configPath := "../collector/services.json"
	if len(os.Args) >= 2 {
		configPath = os.Args[1]
	}
	mock, err := NewMock(model.TestTypeUnresolvable, configPath)
	if err != nil {
		panic(err)
	}
	err = mock.Run(":8123")
	if err != nil {
		slog.Error("An error has occurred", "error", err)
	}
}
