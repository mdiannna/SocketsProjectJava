/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;



public class BlackCat extends CatDecorator {
    public BlackCat(Cat c) {
        super(c);
        c.setColor("black");
        System.out.println("A black cat was created");
    }
    
    @Override
    public void setColor(String color) {        
        System.out.println("The color of black cat can't be changed");
    }
}
