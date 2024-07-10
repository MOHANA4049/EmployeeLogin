package com.kapturecx.employeelogin.validation;
import com.kapturecx.employeelogin.entity.EmployeeLogin;

public class LoginValidation {
    public static void validateEmployeeLogin(EmployeeLogin employeeLogin) {
        if (employeeLogin == null) {
            throw new IllegalArgumentException("EmployeeLogin cannot be null");
        }

        String username = employeeLogin.getUsername();
        if (username == null || username.isEmpty()) {
            throw new NullPointerException("Username cannot be null or empty");
        }

        if(username.length()>15){
            throw new NullPointerException("Username should be of size 15");
        }

        int clientId = employeeLogin.getClientId();
        if (clientId <= 0) {
            throw new NullPointerException("Client ID cannot be null or less than or equal to 0");
        }

        String password = employeeLogin.getPassword();
        if (password == null || password.isEmpty()) {
            throw new NullPointerException("Password cannot be null or empty");
        }
    }
}
