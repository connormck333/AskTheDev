package com.devconnor.askthedev.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvUtils {

    private static EnvironmentType envType = EnvironmentType.LOCAL;
    private static Dotenv dotenv;

    public static void loadDotEnv(EnvironmentType envType) {
        EnvUtils.envType = envType;
        if (envType == EnvironmentType.LOCAL) {
            EnvUtils.dotenv = Dotenv.configure().load();
        }
    }

    public static String loadString(String key) {
        if (envType == EnvironmentType.LOCAL) {
            return dotenv.get(key);
        }

        return System.getenv(key);
    }
}
