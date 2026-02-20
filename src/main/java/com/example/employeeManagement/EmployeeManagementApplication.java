package com.example.employeeManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeManagementApplication {

	public static void main(String[] args) {
		EnvLoader.load();
		SpringApplication.run(EmployeeManagementApplication.class, args);
	}

}
