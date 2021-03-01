package com.qualityobjects.springboot.exception;

import com.qualityobjects.commons.exception.QOException;

public class InvalidTokenException extends QOException {

	private static final long serialVersionUID = 5207795630755628143L;

	public InvalidTokenException() {
		super(401 /* UNAUTHORIZED */, ErrorCodes.INVALID_TOKEN, "Token no vÃ¡lido.");
	}

}