package generator

import (
	"mock-server/model"
	"time"
)

type ZeroTestDataGenerator struct {
}

func (z ZeroTestDataGenerator) PostProcess(customType model.CustomType, data any) any {
	return data
}

func (z ZeroTestDataGenerator) GenerateEnum(enums []string) any {
	return ""
}

func (z ZeroTestDataGenerator) GenerateCharacter() any {
	return 0
}

func (z ZeroTestDataGenerator) GenerateInteger() any {
	return 0
}

func (z ZeroTestDataGenerator) GenerateString() any {
	return ""
}

func (z ZeroTestDataGenerator) GenerateFloat() any {
	return 0.0
}

func (z ZeroTestDataGenerator) GenerateTime() any {
	return time.Time{}
}

func (z ZeroTestDataGenerator) GenerateBoolean() any {
	return false
}
