package org.example.quanlybanhang.security.password;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.github.cdimascio.dotenv.Dotenv;

public class PasswordEncoder {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String PEPPER = dotenv.get("PEPPER_KEY");

    private final Argon2 argon2;

    public PasswordEncoder() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public boolean verify(String hashedPassword, String rawPassword) {
        String inputWithPepper = rawPassword + PEPPER;
        return argon2.verify(hashedPassword, inputWithPepper.toCharArray());
    }
}

