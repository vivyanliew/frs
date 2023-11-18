/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.SeatInventory;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author liewvivyan
 */
@Remote
public interface SeatInventorySessionBeanRemote {
        public boolean checkIfReserved(SeatInventory seatInventory, int cabinClassId, String seatNumber);
    public void setReserved(SeatInventory seatInventory, int cabinClassId, List<String> seatSelection);

}
