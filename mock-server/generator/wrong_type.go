package generator

import (
	"mock-server/model"
)

type WrongTypeTestDataGenerator struct {
	BaseGenerator TestDataGenerator
}

func (w WrongTypeTestDataGenerator) PostProcess(customType model.CustomType, data any) any {
	return data
}

func (w WrongTypeTestDataGenerator) GenerateInteger() any {
	return w.BaseGenerator.GenerateString()
}

func (w WrongTypeTestDataGenerator) GenerateString() any {
	return w.BaseGenerator.GenerateInteger()
}

func (w WrongTypeTestDataGenerator) GenerateFloat() any {
	return w.BaseGenerator.GenerateString()
}

func (w WrongTypeTestDataGenerator) GenerateTime() any {
	return w.BaseGenerator.GenerateString()
}

func (w WrongTypeTestDataGenerator) GenerateBoolean() any {
	return w.BaseGenerator.GenerateFloat()
}

func (w WrongTypeTestDataGenerator) GenerateCharacter() any {
	return w.BaseGenerator.GenerateBoolean()
}

func (w WrongTypeTestDataGenerator) GenerateEnum(enums []string) any {
	return w.BaseGenerator.GenerateBoolean()
}
