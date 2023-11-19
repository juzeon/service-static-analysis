package generator

import (
	"mock-server/model"
	"mock-server/util"
	"time"
)

type RandomTestDataGenerator struct {
}

func (r RandomTestDataGenerator) PostProcess(customType model.CustomType, data any) any {
	return data
}

func (r RandomTestDataGenerator) GenerateEnum(enums []string) any {
	return enums[util.RandIntInclusive(0, len(enums)-1)]
}

func (r RandomTestDataGenerator) GenerateCharacter() any {
	return rune(util.RandStringRunes(1)[0])
}

func (r RandomTestDataGenerator) GenerateInteger() any {
	return util.RandIntInclusive(-999, 999)
}

func (r RandomTestDataGenerator) GenerateString() any {
	return util.RandStringRunes(util.RandIntInclusive(6, 20))
}

func (r RandomTestDataGenerator) GenerateFloat() any {
	return util.RandFloat64(-999, 999)
}

func (r RandomTestDataGenerator) GenerateTime() any {
	return time.Now().Add(time.Hour * time.Duration(util.RandIntInclusive(-500, 500)))
}

func (r RandomTestDataGenerator) GenerateBoolean() any {
	return util.RandIntInclusive(0, 1) == 0
}
