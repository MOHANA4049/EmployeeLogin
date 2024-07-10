package com.kapturecx.employeelogin.service;

import com.kapturecx.employeelogin.dao.EmployeeRepository;
import com.kapturecx.employeelogin.entity.EmployeeLogin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RedisService redisService;

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private static final int SESSION_TIMEOUT_SECONDS = 60; // 1 minute

    public void createSession(HttpServletRequest request, HttpServletResponse response,EmployeeLogin employeeLogin) {
        HttpSession session = request.getSession(true);
        session.setAttribute("employeeId", employeeLogin.getEmployeeId());
        session.setAttribute("username", employeeLogin.getUsername());
        session.setAttribute("clientId", employeeLogin.getClientId());
        session.setAttribute("sessionStartTime", System.currentTimeMillis());
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        createCookie(response, session);

        // Store session details in Redis
        redisService.saveSession((int)session.getAttribute("employeeId"),(int)session.getAttribute("clientId"),SESSION_TIMEOUT_SECONDS);

        logger.info("Session created for user id: {}",session.getAttribute("employeeid"));
    }

    private void createCookie(HttpServletResponse response, HttpSession session) {
        Cookie sessionCookie = new Cookie("KaptureCX", session.getId());
        sessionCookie.setMaxAge(SESSION_TIMEOUT_SECONDS);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);
    }

    public boolean invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.error("No session found for the request.");
            return false;
        }
        Integer Id= (Integer) request.getSession(false).getAttribute("employeeId");
        if(Id>0){
            EmployeeLogin employeeLogin = employeeRepository.findById(Id);
            redisService.removeSession(Id);
            request.getSession().invalidate();
            employeeLogin.setActiveLogin(false);
            employeeRepository.updateEmployee(employeeLogin);
            logger.info("Session invalidated for user id: {}", Id);
        }
        return true;
    }
}
