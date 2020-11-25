package com.qualityobjects.springboot.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.qualityobjects.commons.utils.HashHelper;

public class SHA256PasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence rawPassword) {

		return HashHelper.hashSHA256(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		String pass = encode(rawPassword);
		return pass.equals(encodedPassword);
	}

}