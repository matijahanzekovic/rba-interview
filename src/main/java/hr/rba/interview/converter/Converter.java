package hr.rba.interview.converter;

public interface Converter<S, T> {

  T convert(S source);

}
