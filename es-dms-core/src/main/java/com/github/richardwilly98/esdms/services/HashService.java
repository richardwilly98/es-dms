package com.github.richardwilly98.esdms.services;

public interface HashService {
	
	public abstract byte[] computeHash(byte[] text);

	public abstract String toHex(byte[] text);

	public abstract String toBase64(byte[] text);
}
