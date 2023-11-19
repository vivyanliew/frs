/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Airport;
import javax.ejb.Local;
import javax.persistence.NoResultException;
import util.exception.AirportNotFoundException;

/**
 *
 * @author liewvivyan
 */
@Local
public interface AirportSessionBeanLocal {

    public Airport createNewAirport(Airport airport);

    public Airport retrieveAirportByIATACode(String code)  throws AirportNotFoundException;
    
}
