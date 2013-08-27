package com.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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


import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.util.ByteSource;

@Singleton
public class SHA512HashProvider implements HashService {

	private static Logger log = Logger.getLogger(SHA512HashProvider.class);
	
	private final DefaultHashService service;

	public SHA512HashProvider() {
		super();
		service = new DefaultHashService();
		if (log.isTraceEnabled()) {
			log.trace("AlgorithmName: " + service.getHashAlgorithmName());
		}
	}

	@Override
	public byte[] computeHash(byte[] text) {
		return compute(text).getBytes();
	}

	private Hash compute(byte[] text) {
		try {
			byte[] baseSalt = {1, 1, 1, 2, 2, 2, 3, 3, 3};
			ByteSource salt = ByteSource.Util.bytes(baseSalt);
			int iterations = 3;
			HashRequest request = new SimpleHashRequest(
					service.getHashAlgorithmName(),
					ByteSource.Util.bytes(text), salt, iterations);
			Hash hash = service.computeHash(request);
			return hash;
		} catch (Throwable t) {
			log.error("compute failed", t);
			throw new NullPointerException("hash");
		}
	}

	@Override
	public String toHex(byte[] text) {
		return compute(text).toHex();
	}

	@Override
	public String toBase64(byte[] text) {
		return compute(text).toBase64();
	}

}
