package org.grain.config;

public class TestTemplate implements ITemplate {

	private String id;
	public String name;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ITemplate initTemplate(String[] data) throws Exception {
		TestTemplate testTemplate = new TestTemplate();
		testTemplate.id = data[0];
		testTemplate.name = data[1];
		return testTemplate;
	}

}
