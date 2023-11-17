package strategy

import "mock-server/generator"

type TestWrongType struct {
}

func (t TestWrongType) HandleHTTP(param HTTPParam) {
	_, endpoint, err := param.MatchServiceEndpoint()
	if err != nil {
		panic(err)
	}
	ins, err := generator.GenerateCustomTypeInstance(endpoint.ResponseType,
		generator.NewOptions(generator.WithTestDataGenerator(
			generator.WrongTypeTestDataGenerator{BaseGenerator: generator.RandomTestDataGenerator{}})))
	param.MustRespondWithJSON(200, ins)
}
