package com.kapturecx.employeelogin.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kapturecx.employeelogin.dto.EmployeeLoginDto;
import com.kapturecx.employeelogin.dto.EmployeeSignUpDto;
import com.kapturecx.employeelogin.service.EmployeeLoginService;
import com.kapturecx.employeelogin.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class EmployeeLoginController {
    @Autowired
    EmployeeLoginService employeeLoginService;
    @Autowired
    SessionService sessionService;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/signin")
    public ResponseEntity<?> logIn(@RequestBody EmployeeLoginDto employeeLoginDto, HttpServletRequest request, HttpServletResponse response){
        return employeeLoginService.login(employeeLoginDto,request,response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody EmployeeSignUpDto employeeSignUpDto){
        return employeeLoginService.signup(employeeSignUpDto);
    }

    @PostMapping("/signout")
    public ResponseEntity<ObjectNode> logOut(HttpServletRequest request) {
        return employeeLoginService.logout(request);
    }

}
