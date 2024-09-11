
import javafx.scene.image.Image;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class Contact {

    private String name;
    private String phoneNumber;
    private Image photo;

    public Contact(String name, String phoneNumber, Image photo) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Image getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return name;
    }
}