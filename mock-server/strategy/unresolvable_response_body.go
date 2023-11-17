package strategy

import "mock-server/generator"

type TestUnresolvableResponseBody struct {
}

func (o *TestUnresolvableResponseBody) HandleHTTP(param HTTPParam) {
	_, endpoint, err := param.MatchServiceEndpoint()
	if err != nil {
		panic(err)
	}
	option := generator.DefaultOptions
	option.TestDataGenerator = generator.RandomTestDataGenerator{}
	ins, err := generator.GenerateCustomTypeInstance(endpoint.ResponseType, option)
	if err != nil {
		panic(err)
	}
	param.MustRespondWithJSON(200, ins)
}
