package thread;

public interface IChangeCycle extends ICycle {
	public void onAdd() throws Exception;

	public void onRemove() throws Exception;
}
