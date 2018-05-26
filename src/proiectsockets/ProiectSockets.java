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
import factory.CatFactory;

public class ProiectSockets {

    public static void main(String[] args) throws Exception {
//      Cat cat = new Cat();
//      Cat cat = new GrayCat(new BasicCat());

        CatFactory catFactory = new CatFactory();
        Cat cat1 = catFactory.getCat(null);
        FirClient firClient1 = new FirClient(cat1);
        firClient1.start();
        firClient1.join();
        firClient1.sleep(1000);

        Cat cat2 = catFactory.getCat(null);
        FirClient firClient2 = new FirClient(cat2);
        firClient2.start();
        firClient2.join();
        firClient2.sleep(1000);

        Cat cat3 = catFactory.getCat(null);
        FirClient firClient3 = new FirClient(cat3);
        firClient3.start();
        firClient3.join();
        firClient3.sleep(1000);

        Cat cat4 = catFactory.getCat(null);
        FirClient firClient4 = new FirClient(cat4);
        firClient4.start();
        firClient4.join();
        firClient4.sleep(1000);

        Cat cat5 = catFactory.getCat(null);
        FirClient firClient5 = new FirClient(cat5);
        firClient5.start();

        firClient5.join();
        firClient5.sleep(1000);

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
