/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Employee;
import entity.Partner;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeUserRole;

/**
 *
 * @author liewvivyan
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    
    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class,1l)==null) {
            employeeSessionBeanLocal.createNewEmployee(new Employee("margaret","margaret123@gmail.com","123456789",EmployeeUserRole.FLEETMANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("bob","bob123@gmail.com","123456789",EmployeeUserRole.ROUTEPLANNER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("max","max123@gmail.com","123456789",EmployeeUserRole.SALESMANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("anne","anne123@gmail.com","123456789",EmployeeUserRole.SCHEDULEMANAGER));
        }
        
        if (em.find(Partner.class,1l)==null) {
            partnerSessionBeanLocal.createNewPartner(new Partner("chloe","chloe123@gmail.com", "123456789"));
        }
        
    }
}
