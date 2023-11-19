package strategy

import (
	"math"
	"mock-server/generator"
)

type TestTooLarge struct {
}

func (t TestTooLarge) HandleHTTP(param HTTPParam) {
	_, endpoint, err := param.MatchServiceEndpoint()
	if err != nil {
		panic(err)
	}
	ins, err := generator.GenerateCustomTypeInstance(endpoint.ResponseType,
		generator.NewOptions(
			generator.WithTestDataGenerator(
				generator.MaximumTestDataGenerator{BaseGenerator: generator.RandomTestDataGenerator{}}),
			generator.WithListLengthOption(int(math.Pow(10, 5))),
			generator.WithMapLengthOption(int(math.Pow(10, 5))),
		))
	if err != nil {
		panic(err)
	}
	param.MustRespondWithJSON(200, ins)
}
