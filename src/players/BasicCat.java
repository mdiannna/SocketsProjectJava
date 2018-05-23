/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;

import java.util.Random;


public class BasicCat implements Cat {
    protected String color;
    protected int level;
    protected int score;
    protected int coefficient = 1;
    
    public BasicCat() {
    }
    
    @Override
    public String getColor() {
        return this.color;
    }
    
    @Override
    public int getScore() {
        return  this.score;
    }
    
    @Override
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public void setColor(String color) {
        this.color = color;
    }
    
    @Override
    public void setScore(int score) {
        this.score = score;
    }
    
    @Override
    public void isAttacked(int points) {
        this.score -= points;
        this.level = this.score % 100;
    }
    
    @Override
    public int attacks(Cat cat) {
        Random r = new Random();

        int attackForce = r.nextInt(this.level-1) + 1;
        int attackPoints = this.coefficient * attackForce;
        
        // TODO: test
        cat.isAttacked(attackPoints);
        return attackPoints;
    }
   
}

