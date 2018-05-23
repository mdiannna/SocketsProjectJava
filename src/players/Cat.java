/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package players;

import java.util.Random;

// TODO: finish Decorator design pattern
// https://www.journaldev.com/1540/decorator-design-pattern-in-java-example

public interface Cat {
    public String getColor();
    public int getScore();
    public int getLevel();
    public void setColor(String color);
    public void setScore(int score);
    public void isAttacked(int points);
    public int attacks(Cat cat);
}
