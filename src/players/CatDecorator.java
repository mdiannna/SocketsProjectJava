/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;

import java.util.Random;


public class CatDecorator implements Cat {
    protected Cat cat;
    
    public CatDecorator(Cat c) {
        this.cat = c;
    }

    @Override
    public String getColor() {
        return this.cat.getColor();
    }

    @Override
    public int getScore() {
        return this.cat.getScore();
    }

    @Override
    public int getLevel() {
        return this.cat.getLevel();
    }

    @Override
    public void setColor(String color) {
        this.cat.setColor(color);
    }

    @Override
    public void setScore(int score) {
        this.cat.setScore(score);
    }

    @Override
    public void isAttacked(int points) {
        this.cat.isAttacked(points);
    }

    @Override
    public int attacks(Cat cat) {
        return this.cat.attacks(cat);
    }

  
}
