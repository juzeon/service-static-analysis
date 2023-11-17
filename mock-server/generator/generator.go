package generator

import (
	"errors"
	"github.com/life4/genesis/slices"
	"mock-server/model"
	"mock-server/util"
)

type TestDataGenerator interface {
	GenerateInteger() any
	GenerateString() any
	GenerateFloat() any
	GenerateTime() any
	GenerateBoolean() any
	GenerateCharacter() any
	GenerateEnum(enums []string) any
}

func GenerateRawData(generator TestDataGenerator, customType model.CustomType) (any, error) {
	if len(customType.Fields) > 0 && customType.Fields[0].Type.BaseName == "__enum__" {
		return generator.GenerateEnum(slices.Map(customType.Fields, func(el model.CustomField) string {
			return el.Name
		})), nil
	}
	switch customType.BaseName {
	case "java.lang.String":
		return generator.GenerateString(), nil
	case "java.lang.Integer", "java.math.BigDecimal", "java.math.BigInteger":
		return generator.GenerateInteger(), nil
	case "java.lang.Float", "java.lang.Double":
		return generator.GenerateFloat(), nil
	case "java.lang.Boolean":
		return generator.GenerateBoolean(), nil
	case "java.time.LocalDateTime", "java.time.DateTime",
		"java.util.Date", "java.sql.Date", "java.sql.Timestamp":
		return generator.GenerateTime(), nil
	case "java.lang.Character":
		return generator.GenerateCharacter(), nil
	case "java.lang.Byte", "java.lang.Short", "java.lang.Long":
		return generator.GenerateInteger(), nil
	default:
		return nil, errors.New("baseName is not raw type: " + customType.BaseName)
	}
}

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

func GenerateCustomTypeInstance(customType model.CustomType, options Options) (any, error) {
	return generateCustomTypeInstance(customType, options, nil)
}
func generateCustomTypeInstance(customType model.CustomType, options Options,
	genericType *model.CustomType) (any, error) {
	if customType.IsVoid {
		return nil, nil
	}
	if slices.Contains([]string{"java.util.List", "java.util.ArrayList",
		"java.util.LinkedList"}, customType.BaseName) {
		if len(customType.GenericName) < 1 {
			return nil, errors.New("no generic name provided for list")
		}
		if options.ListLength < 0 {
			return nil, nil
		}
		jsonArray := make(model.JSONArray, 0)
		for i := 0; i < options.ListLength; i++ {
			instance, err := generateCustomTypeInstance(customType.GenericName[0], options, nil)
			if err != nil {
				return nil, err
			}
			jsonArray = append(jsonArray, instance)
		}
		return jsonArray, nil
	}
	if slices.Contains([]string{"java.util.Map", "java.util.HashMap",
		"java.util.TreeMap"}, customType.BaseName) {
		if len(customType.GenericName) < 2 {
			return nil, errors.New("no enough generic name provided for map")
		}
		if options.MapLength < 0 {
			return nil, nil
		}
		jsonObject := make(model.JSONObject)
		for i := 0; i < options.MapLength; i++ {
			value, err := generateCustomTypeInstance(customType.GenericName[1], options, nil)
			if err != nil {
				return nil, err
			}
			jsonObject[util.RandStringRunes(3)] = value
		}
		return jsonObject, nil
	}
	if len(customType.Fields) > 0 && customType.Fields[0].Type.BaseName != "__enum__" {
		jsonObject := make(model.JSONObject)
		useGenericIndex := 0
		for _, field := range customType.Fields {
			if field.Type.IsGeneric {
				if useGenericIndex >= len(customType.GenericName) {
					return nil, errors.New("not enough generic name for generic fields")
				}
				instance, err := generateCustomTypeInstance(field.Type, options, &customType.GenericName[useGenericIndex])
				if err != nil {
					return nil, err
				}
				useGenericIndex++
				jsonObject[field.Name] = instance
				continue
			}
			instance, err := generateCustomTypeInstance(field.Type, options, nil)
			if err != nil {
				return nil, err
			}
			jsonObject[field.Name] = instance
		}
		return jsonObject, nil
	}
	if customType.IsGeneric {
		if genericType == nil {
			return nil, errors.New("customType is generic but no generic name provided")
		}
		instance, err := generateCustomTypeInstance(*genericType, options, nil)
		if err != nil {
			return nil, err
		}
		return instance, nil
	}
	instance, err := GenerateRawData(options.TestDataGenerator, customType)
	if err != nil {
		return nil, err
	}
	return instance, nil
}
