/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.FlightRoute;
import javax.ejb.Remote;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface FlightRouteSessionBeanRemote {
        public FlightRoute createNewFlightRoute(FlightRoute flightRoute);
        public void setReturnRoute(FlightRoute fr1, FlightRoute fr2);
}
