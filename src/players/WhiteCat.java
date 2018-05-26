/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;

public class WhiteCat extends CatDecorator {
    public WhiteCat(Cat c) {
        super(c);
        c.setColor("white");
        System.out.println("A white cat was created");
    }
    
    @Override
    public void setColor(String color) {        
        System.out.println("The color of white cat can't be changed");
    }
}
