package model

type TestType int

const (
	_ = TestType(iota)
	TestTypeUnresolvable
	TestTypeWrongType
	TestTypeTooLarge
)

func (o TestType) GetTestStrategyLayer() TestStrategyLayer {
	if o <= 3 {
		return TestStrategyLayerHTTP
	}
	return TestStrategyLayerTCP
}

type TestStrategyLayer int

const (
	_ = TestStrategyLayer(iota)
	TestStrategyLayerHTTP
	TestStrategyLayerTCP
)

type JSONObject map[string]any
type JSONArray []any
