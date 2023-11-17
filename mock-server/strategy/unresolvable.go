package strategy

import (
	"encoding/json"
	"mock-server/generator"
)

type TestUnresolvable struct {
}

func (o TestUnresolvable) HandleHTTP(param HTTPParam) {
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
	v, err := json.Marshal(ins)
	if err != nil {
		panic(err)
	}
	param.Writer.Header().Set("Content-Type", "application/json")
	param.Writer.WriteHeader(200)
	jsonStr := string(v)
	param.MustWriteString(jsonStr[:len(jsonStr)-2])
}
