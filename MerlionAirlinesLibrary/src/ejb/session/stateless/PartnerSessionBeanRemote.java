/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface PartnerSessionBeanRemote {
     public Partner createNewPartner(Partner partner);

    public Partner retrievePartnerByEmail(String email) throws PartnerNotFoundException;
         public Long partnerLogin(String email, String password) throws InvalidLoginCredentialException;
}
