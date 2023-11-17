package generator

type Options struct {
	ListLength        int // -1 0 +
	MapLength         int //-1 0 +
	TestDataGenerator TestDataGenerator
}

var DefaultOptions = Options{
	ListLength:        3,
	MapLength:         3,
	TestDataGenerator: nil,
}

type WithOptionFunc func(options *Options)

func NewOptions(withFuncs ...WithOptionFunc) Options {
	options := DefaultOptions
	for _, f := range withFuncs {
		f(&options)
	}
	return options
}
func WithListLengthOption(length int) WithOptionFunc {
	return func(options *Options) {
		options.ListLength = length
	}
}
func WithMapLengthOption(length int) WithOptionFunc {
	return func(options *Options) {
		options.MapLength = length
	}
}
func WithTestDataGenerator(g TestDataGenerator) WithOptionFunc {
	return func(options *Options) {
		options.TestDataGenerator = g
	}
}
