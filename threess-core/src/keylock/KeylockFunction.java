package keylock;

@FunctionalInterface
public interface KeylockFunction {
	public Object apply(Object... params);
}
