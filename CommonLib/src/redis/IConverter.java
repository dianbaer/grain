package redis;

public interface IConverter<S, T> {
	T convert(S source);
}
