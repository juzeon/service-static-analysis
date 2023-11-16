package strategy

import "log/slog"

type TestUnresolvableResponseBody struct {
}

func (o *TestUnresolvableResponseBody) HandleHTTP(param HTTPParam) {
	_, endpoint, err := param.MatchServiceEndpoint()
	if err != nil {
		panic(err)
	}
	slog.Info("Handle", "endpoint", endpoint)
}
