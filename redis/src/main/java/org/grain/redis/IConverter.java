package org.grain.redis;

public interface IConverter<S, T> {
	T convert(S source);
}
