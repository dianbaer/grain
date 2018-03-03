package org.grain.threadkeylock;

@FunctionalInterface
public interface KeylockFunction {
	public Object apply(Object... params);
}
