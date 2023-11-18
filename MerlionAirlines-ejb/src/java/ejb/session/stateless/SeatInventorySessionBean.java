/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.SeatInventory;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author liewvivyan
 */
@Stateless
public class SeatInventorySessionBean implements SeatInventorySessionBeanRemote, SeatInventorySessionBeanLocal {

    @PersistenceContext(unitName = "MerlionAirlines-ejbPU")
    private EntityManager em;

    @Override
    public boolean checkIfReserved(SeatInventory seatInventory, int cabinClassId, String seatNumber) {
        SeatInventory s = em.find(SeatInventory.class, seatInventory.getSeatInventoryId());
        List<String> reservedSeats = s.getReservedSeats().get(cabinClassId);
        for (String seat: reservedSeats) {
            if (seat.equals(seatNumber)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setReserved(SeatInventory seatInventory, int cabinClassId, List<String> seatSelection) {
        SeatInventory s = em.find(SeatInventory.class, seatInventory.getSeatInventoryId());
        List<String> availSeats = s.getAvailableSeats().get(cabinClassId);
        List<String> reservedSeats = s.getReservedSeats().get(cabinClassId);
        for (String seatNum : seatSelection) {
            availSeats.remove(seatNum);
            reservedSeats.add(seatNum);
        }
    }
}
