package com.github.richardwilly98.service;

import org.apache.log4j.Logger;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.util.ByteSource;

public class SHA512HashService implements HashService {

	private static Logger log = Logger.getLogger(SHA512HashService.class);
	
	private final DefaultHashService service;

	public SHA512HashService() {
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
