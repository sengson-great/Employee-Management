package com.example.employeeManagement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    public static void load() {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream(".env");
            props.load(fis);
            props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
        } catch (IOException e) {
            System.out.println(".env file not found, skipping...");
        }
    }
}