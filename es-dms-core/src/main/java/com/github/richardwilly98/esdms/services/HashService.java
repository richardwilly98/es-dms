package com.github.richardwilly98.esdms.services;

public interface HashService {
	
	byte[] computeHash(byte[] text);

	String toHex(byte[] text);

	String toBase64(byte[] text);
}
