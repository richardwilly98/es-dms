package com.github.richardwilly98.esdms;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
