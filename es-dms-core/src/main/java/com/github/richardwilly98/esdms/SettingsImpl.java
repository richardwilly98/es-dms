package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Settings;
import com.google.common.base.Objects;

public class SettingsImpl implements Settings {

	private String library;
	private String esHost;
	private int esPort;
	private boolean indexRefresh;

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#getLibrary()
	 */
	@Override
	public String getLibrary() {
		return library;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#setLibrary(java.lang.String)
	 */
	@Override
	public void setLibrary(String library) {
		this.library = library;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#getEsHost()
	 */
	@Override
	public String getEsHost() {
		return esHost;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#setEsHost(java.lang.String)
	 */
	@Override
	public void setEsHost(String esHost) {
		this.esHost = esHost;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#getEsPort()
	 */
	@Override
	public int getEsPort() {
		return esPort;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.Settings#setEsPort(int)
	 */
	@Override
	public void setEsPort(int esPort) {
		this.esPort = esPort;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("library", library).add("esPort", esPort)
				.add("esHost", esHost).add("indexRefresh", indexRefresh).toString();
	}

	@Override
	public boolean isIndexRefresh() {
		return indexRefresh;
	}

	@Override
	public void setIndexRefresh(boolean indexRefresh) {
		this.indexRefresh = indexRefresh;
		
	}
}
