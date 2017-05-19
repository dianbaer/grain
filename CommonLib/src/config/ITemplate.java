package config;

public interface ITemplate {
	public int getId();

	public ITemplate initTemplate(String[] data) throws Exception;
}
