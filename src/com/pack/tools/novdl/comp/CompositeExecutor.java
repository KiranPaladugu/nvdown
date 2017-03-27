package com.pack.tools.novdl.comp;

import java.util.ArrayList;
import java.util.List;

public class CompositeExecutor implements Composite {
	private List<Composite> list = new ArrayList<>();

	public void addComposite(Composite composite) {
		this.list.add(composite);
	}

	@Override
	public String execute(String data) {
		String tmp = data;
		for (Composite composite : list) {
			tmp = composite.execute(tmp);
		}
		return tmp;
	}

}
