/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.exception;

/**
 *
 * @author liewvivyan
 */
public class CustomerNotFoundException extends Exception{

    public CustomerNotFoundException() {
    }
    public CustomerNotFoundException(String msg) {
        super(msg);
    }
}
