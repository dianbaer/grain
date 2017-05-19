package mina;

public class TestMinaClientName implements IMinaClientName {
	public static String CENTER = "Center";
	public static String OTHER = "Other";

	@Override
	public String[] getClientNames() {
		return new String[] { CENTER, OTHER };
	}

}
