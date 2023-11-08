/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

  
    public Long createNewEmployee(Employee employee) {
        em.persist(employee);
        em.flush();
        return employee.getEmployeeId();
        
    }
    
    public Employee retrieveEmployeeByEmail(String email) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT E from Employee e WHERE e.email = :inputEmail");
        query.setParameter("inputEmail", email);
        
        try {
            return (Employee) query.getSingleResult();
        } catch(NoResultException ex) {
            throw new EmployeeNotFoundException("The given email is not registered with an employee account!");
        }
        
    }
    
    @Override
    public Employee employeeLogin(String email, String password) throws InvalidLoginCredentialException {
        
        try {
            Employee employee = retrieveEmployeeByEmail(email);
        
            if(employee.getPassword().equals(password)) {
                return employee;
            } else {
                throw new InvalidLoginCredentialException("Wrong password!");
            }
        } catch(EmployeeNotFoundException ex) {
            throw new InvalidLoginCredentialException("The given email is not registered with an employee account!");
        }
    }

    public void persist(Object object) {
        em.persist(object);
    }
    
}