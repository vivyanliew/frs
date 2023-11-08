/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.AircraftType;
import javax.ejb.Local;

/**
 *
 * @author liewvivyan
 */
@Local
public interface AircraftTypeSessionBeanLocal {

    public AircraftType createNewAircraftType(AircraftType aircraftType);
    
}
