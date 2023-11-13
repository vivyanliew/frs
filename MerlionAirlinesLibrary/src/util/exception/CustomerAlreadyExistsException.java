/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util.exception;

/**
 *
 * @author liewvivyan
 */
public class CustomerAlreadyExistsException extends Exception{

    public CustomerAlreadyExistsException() {
    }
    public CustomerAlreadyExistsException(String msg) {
        super(msg);
    }
    
}
