/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factory;

import java.util.Random;
import players.*;

/**
 *
 * @author mdiannna
 */
public class CatFactory {
    // Tipurile de pisici
    final String CAT_TYPES[] = {"black", "white", "gray", "super"};
    
    public Cat getCat(String catType) {
        // Daca nu e specificat tipul, genereaza un player random
        if(catType==null) {
            catType = CAT_TYPES[new Random().nextInt(CAT_TYPES.length)];
        }
        
        if(catType.equalsIgnoreCase("black")){
            return new BlackCat(new BasicCat());
        }
        // else
        if(catType.equalsIgnoreCase("white")){
            return new WhiteCat(new BasicCat());
        }
        // else
        if(catType.equalsIgnoreCase("gray")){
            return new GrayCat(new BasicCat());
        }
        // else
        if(catType.equalsIgnoreCase("super")){
            return new SuperCat(new BasicCat());
        }
        
        return new BasicCat();
    }
}
