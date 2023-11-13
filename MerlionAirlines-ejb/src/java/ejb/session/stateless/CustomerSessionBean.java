/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @Override
    public Customer createCustomer(Customer customer) throws CustomerAlreadyExistsException {
        
        try {
            em.persist(customer);
            em.flush();
            return customer;
        } catch (EntityExistsException ex) {
            throw new CustomerAlreadyExistsException("Given email is already registered to a customer");
        }
        
    }

    @Override
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c from Customer c WHERE c.email = :inputEmail");
        query.setParameter("inputEmail", email);
        try {
            return (Customer) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new CustomerNotFoundException("The given email is not registered with a customer account!");
        }
    }

    @Override
    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException {
        try {
            Customer customer = retrieveCustomerByEmail(email);
            if (customer.getPassword().equals(password)) {
                return customer;
            } else {
                throw new InvalidLoginCredentialException("Wrong password!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("The given email is not registered with a customer account!");
        }
    }
}
