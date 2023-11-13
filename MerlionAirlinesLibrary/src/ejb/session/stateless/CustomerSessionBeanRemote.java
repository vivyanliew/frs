/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Remote;
import util.exception.CustomerAlreadyExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface CustomerSessionBeanRemote {
    public Customer createCustomer(Customer customer) throws CustomerAlreadyExistsException;
    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;
    public Customer customerLogin(String email, String password) throws InvalidLoginCredentialException;
}
