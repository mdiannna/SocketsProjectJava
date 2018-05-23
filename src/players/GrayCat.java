/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;


public class GrayCat extends CatDecorator {
    public GrayCat(Cat c) {
        super(c);
        this.setColor("gray");
    }
}