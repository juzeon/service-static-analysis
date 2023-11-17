package generator

import (
	"math/rand"
	"mock-server/util"
	"time"
)

type RandomTestDataGenerator struct {
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
	return rand.Float64()*100 - 50
}

func (r RandomTestDataGenerator) GenerateTime() any {
	return time.Now().Add(time.Hour * time.Duration(util.RandIntInclusive(0, 500)-250))
}

func (r RandomTestDataGenerator) GenerateBoolean() any {
	return util.RandIntInclusive(0, 1) == 0
}
