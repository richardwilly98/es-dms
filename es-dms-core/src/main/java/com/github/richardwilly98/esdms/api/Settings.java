package com.github.richardwilly98.esdms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.SettingsImpl;

@JsonDeserialize(as = SettingsImpl.class)
public interface Settings {

	public abstract String getLibrary();

	public abstract void setLibrary(String library);

	public abstract String getEsHost();

	public abstract void setEsHost(String esHost);

	public abstract int getEsPort();

	public abstract void setEsPort(int esPort);
	
	public abstract boolean isIndexRefresh();
	
	abstract void setIndexRefresh(boolean indexRefresh);

}