/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proiectsockets;

import java.io.*;
import java.net.*;
import java.util.*;
import server.SocketsServer;
import client.SocketsClient;
import java.util.logging.Level;
import java.util.logging.Logger;
import players.*;

public class ProiectSockets {
  public static void main(String[] args) throws Exception
  {
    // TODO: pattern creational care creeaza mai multi playeri random - Factory?
//      Cat cat = new Cat();
//      cat.setColor("red");
      Cat cat = new GrayCat(new BasicCat());
      new FirClient(cat).start();
  }
}

class FirClient extends Thread {
    private Cat cat;
    
    public FirClient(Cat cat) {
      this.cat = cat;  
    }
    
    public void run() {
      
      SocketsClient client1 = new SocketsClient(cat);  
    }
}
