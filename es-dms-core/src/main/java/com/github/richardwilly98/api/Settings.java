package com.github.richardwilly98.api;

import com.google.common.base.Objects;

public class Settings {

	private String library;
	private String esHost;
	private int esPort;

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getEsHost() {
		return esHost;
	}

	public void setEsHost(String esHost) {
		this.esHost = esHost;
	}

	public int getEsPort() {
		return esPort;
	}

	public void setEsPort(int esPort) {
		this.esPort = esPort;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("library", library).add("esPort", esPort)
				.add("esHost", esHost).toString();
	}
}
