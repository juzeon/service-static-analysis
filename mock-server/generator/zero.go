package generator

import (
	"time"
)

type ZeroTestDataGenerator struct {
}

func (z ZeroTestDataGenerator) GenerateCharacter() rune {
	return 0
}

func (z ZeroTestDataGenerator) GenerateInteger() int {
	return 0
}

func (z ZeroTestDataGenerator) GenerateString() string {
	return ""
}

func (z ZeroTestDataGenerator) GenerateFloat() float64 {
	return 0.0
}

func (z ZeroTestDataGenerator) GenerateTime() time.Time {
	return time.Time{}
}

func (z ZeroTestDataGenerator) GenerateBoolean() bool {
	return false
}
