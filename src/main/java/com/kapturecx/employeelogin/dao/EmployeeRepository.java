package com.kapturecx.employeelogin.dao;
import com.kapturecx.employeelogin.entity.EmployeeLogin;


public interface EmployeeRepository {

    public boolean saveEmployee(EmployeeLogin employeeLogin);

    public boolean updateEmployee(EmployeeLogin employeeLogin);

    public EmployeeLogin findByUsernameAndPassword(String username, String password, int clientId);

    public EmployeeLogin findById(int id);

}
