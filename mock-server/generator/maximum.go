package generator

import (
	"math"
	"mock-server/model"
	"mock-server/util"
	"time"
)

type MaximumTestDataGenerator struct {
	BaseGenerator TestDataGenerator
}

func (m MaximumTestDataGenerator) PostProcess(customType model.CustomType, data any) any {
	t, ok := data.(time.Time)
	if !ok {
		return data
	}
	return "9" + t.Format(time.RFC3339)
}

func (m MaximumTestDataGenerator) GenerateInteger() any {
	return math.MaxInt64
}

func (m MaximumTestDataGenerator) GenerateString() any {
	return util.RandStringRunes(int(math.Pow(10, 5)))
}

func (m MaximumTestDataGenerator) GenerateFloat() any {
	return math.MaxFloat64
}

func (m MaximumTestDataGenerator) GenerateTime() any {
	return time.Date(9999, 12, 31, 23, 59, 59, 0, time.UTC)
}

func (m MaximumTestDataGenerator) GenerateBoolean() any {
	return m.BaseGenerator.GenerateBoolean()
}

func (m MaximumTestDataGenerator) GenerateCharacter() any {
	return m.BaseGenerator.GenerateCharacter()
}

func (m MaximumTestDataGenerator) GenerateEnum(enums []string) any {
	return m.BaseGenerator.GenerateEnum(enums)
}
