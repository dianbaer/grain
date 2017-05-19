package keylock;

public class TestKeyLockType implements IKeyLockType {
	public static String TEST1 = "TEST1";
	public static String TEST2 = "TEST2";

	@Override
	public String[] getkeyLockType() {
		return new String[] { TEST1, TEST2 };
	}

}
