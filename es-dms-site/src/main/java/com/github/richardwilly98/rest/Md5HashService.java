package com.github.richardwilly98.rest;

import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.HashRequest;
import org.apache.shiro.crypto.hash.SimpleHashRequest;
import org.apache.shiro.util.ByteSource;

public class Md5HashService implements HashService {

	private final DefaultHashService service;

	public Md5HashService() {
		super();
		service = new DefaultHashService();
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
			t.printStackTrace();
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
