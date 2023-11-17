package util

import "math/rand"

func RandIntInclusive(min int, max int) int {
	return min + rand.Intn(max-min+1)
}
func RandFloat64(min, max float64) float64 {
	return min + rand.Float64()*(max-min)
}
func RandStringRunes(n int) string {
	letterRunes := []rune("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
	b := make([]rune, n)
	for i := range b {
		b[i] = letterRunes[rand.Intn(len(letterRunes))]
	}
	return string(b)
}
