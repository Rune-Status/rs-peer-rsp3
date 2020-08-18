package org.rspeer.environment.arguments;

public interface DataArgument<T> extends Argument {
    T getValue(String raw);
}
