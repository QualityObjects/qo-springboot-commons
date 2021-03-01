package com.qualityobjects.springboot.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.commons.exception.QOException.ErrorCodes;

public class ExceptionTest {
	
	@Test
	void invalidTokenException() {
		assertThrows(InvalidTokenException.class, () -> {
			try {
				throw new InvalidTokenException();
			} catch (QOException ex) {
				assertEquals("Token no vÃ¡lido.", ex.getMessage());
				assertEquals(401 /* UNAUTHORIZED */, ex.getHttpStatus());
				assertEquals(ErrorCodes.INVALID_TOKEN, ex.getCode());
				throw ex;
			}
		});
	}
	
	@Test
	void accessDeniedException() {
		assertThrows(AccessDeniedException.class, () -> {
			try {
				throw new AccessDeniedException();
			} catch (QOException ex) {
				assertEquals("Acceso denegado", ex.getMessage());
				assertEquals(403 /* ACCESS_DENIED */, ex.getHttpStatus());
				assertEquals(ErrorCodes.ACCESS_DENIED, ex.getCode());
				throw ex;
			}
		});
	}

}
