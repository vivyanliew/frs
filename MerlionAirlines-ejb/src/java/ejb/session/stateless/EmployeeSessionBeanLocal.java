/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Long createNewEmployee(Employee employee);
    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException;

    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException;
    
}
