/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;


public class SuperCat extends CatDecorator {
    protected int coefficient = 3;

    public SuperCat(Cat c) {
        super(c);
        System.out.println("A super cat was created");
        this.setColor("<no color defined>");
    }
}
