package com.kapturecx.employeelogin.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kapturecx.employeelogin.dao.EmployeeRepository;
import com.kapturecx.employeelogin.dto.EmployeeLoginDto;
import com.kapturecx.employeelogin.dto.EmployeeSignUpDto;
import com.kapturecx.employeelogin.entity.EmployeeLogin;
import com.kapturecx.employeelogin.util.EmployeeMapper;
import com.kapturecx.employeelogin.util.EmployeeSignUpMapper;
import com.kapturecx.employeelogin.validation.LoginValidation;
import com.kapturecx.employeelogin.validation.SignUpValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.kapturecx.employeelogin.constant.Constants.*;

@Service
public class EmployeeLoginService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private KafkaService kafkaService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(SignUpValidation.class);

    public ResponseEntity<ObjectNode> login(EmployeeLoginDto employeeLoginDto, HttpServletRequest request, HttpServletResponse response) {
        ObjectNode responseObject = objectMapper.createObjectNode();
        EmployeeLogin employeeLogin = EmployeeMapper.dtoToEmployee(employeeLoginDto);

        try{
            LoginValidation.validateEmployeeLogin(employeeLogin);
            String username = employeeLogin.getUsername();
            String password = employeeLogin.getPassword();
            int clientId = employeeLogin.getClientId();
            EmployeeLogin foundEmployee = employeeRepository.findByUsernameAndPassword(username, password, clientId);
            if (foundEmployee == null || !foundEmployee.isEnable()) {
                responseObject.put("status", false);
                responseObject.put("message", ERROR_USERNAME_NOT_FOUND);
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
            responseObject.put("status", true);
            responseObject.put("message", SUCCESS_LOGIN);
            foundEmployee.setActiveLogin(true);
            sessionService.createSession(request, response,foundEmployee);

            if (employeeRepository.updateEmployee(foundEmployee)){
                redisService.saveInMap(foundEmployee);
                kafkaService.sendMessage("employee-login-topic", employeeLogin);
            }

            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        }
        catch(Exception e) {
            responseObject.put("status", false);
            responseObject.put("message", e.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ObjectNode> signup(EmployeeSignUpDto employeeSignUpDto) {
        ObjectNode responseObject = objectMapper.createObjectNode();
        EmployeeLogin employeeLogin = EmployeeSignUpMapper.dtoToEmployee(employeeSignUpDto);
        try {
            SignUpValidation.validateSignUp(employeeLogin);
            // Proceed to save employee if validation passes
            boolean isSaved = employeeRepository.saveEmployee(employeeLogin);
            if (!isSaved) {
                responseObject.put("status", false);
                responseObject.put("message", "Employee already exists");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }

            responseObject.put("status", true);
            responseObject.put("message", "Sign-up successful");
            kafkaService.sendMessage("employee-topic", employeeLogin);
            return ResponseEntity.status(HttpStatus.OK).body(responseObject);
        }
        catch (Exception e) {
            responseObject.put("status", false);
            responseObject.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseObject);
        }
    }

    public ResponseEntity<ObjectNode> logout(HttpServletRequest request) {
        ObjectNode responseObject = objectMapper.createObjectNode();
        try {
            if (request == null) {
                throw new NullPointerException("HttpServletRequest is null");
            }
            if (sessionService.invalidateSession(request)) {
                responseObject.put("message", "Successfully logged out");
                return new ResponseEntity<>(responseObject, HttpStatus.OK);
            }
            else{
                responseObject.put("error", "Error logging out");
                return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception e) {
            responseObject.put("error", "Unexpected error: " + e.getMessage());
            return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
