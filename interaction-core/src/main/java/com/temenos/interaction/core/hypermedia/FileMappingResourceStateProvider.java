package com.temenos.interaction.core.hypermedia;

import java.util.Collection;
import java.util.List;

public interface FileMappingResourceStateProvider extends ResourceStateProvider {
	public void loadAndMapFiles(Collection<String> files, boolean register);
}
