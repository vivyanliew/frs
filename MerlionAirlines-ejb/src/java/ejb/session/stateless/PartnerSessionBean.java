/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Partner createNewPartner(Partner partner) {
        em.persist(partner);
        em.flush();
        return partner;
    }
    @Override
   public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p from Partner p WHERE p.email = :inputEmail");
        query.setParameter("inputEmail", email);
        
        try {
            return (Partner) query.getSingleResult();
        } catch(NoResultException ex) {
            throw new PartnerNotFoundException("The given email is not registered with a partner account!");
        }
        
    }
    @Override
    public Long partnerLogin(String email, String password) throws InvalidLoginCredentialException {
        
        try {
            Partner partner = retrievePartnerByEmail(email);
        
            if(partner.getPassword().equals(password)) {
                return partner.getPartnerId();
            } else {
                throw new InvalidLoginCredentialException("Wrong password!");
            }
        } catch(PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("The given email is not registered with an employee account!");
        }
    }
}
