package com.kapturecx.employeelogin.validation;

import com.kapturecx.employeelogin.entity.EmployeeLogin;

public class SignUpValidation {

        public static void validateSignUp(EmployeeLogin employeeSignUp) {
            if (employeeSignUp== null) {
                throw new IllegalArgumentException("EmployeeLogin cannot be null");
            }

            if(!employeeSignUp.isEnable()){
                throw new IllegalArgumentException("Enable should be true");
            }

            String username = employeeSignUp.getUsername();
            if (username == null || username.isEmpty()) {
                throw new NullPointerException("Username cannot be null or empty");
            }

            if(username.length()>15){
                throw new NullPointerException("Username should be of size 15");
            }

            int clientId = employeeSignUp.getClientId();
            if (clientId <= 0) {
                throw new NullPointerException("Client ID cannot be null or less than or equal to 0");
            }


            String password = employeeSignUp.getPassword();
            if (password == null || password.isEmpty()) {
                throw new NullPointerException("Password cannot be null or empty");
            }
            int employeeId=employeeSignUp.getEmployeeId();
            if(employeeId<=0){
                throw new NullPointerException("EmployeeId cannot be null");
            }
        }
}
