package server;

import java.io.*;
import static java.lang.Math.abs;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketsServer {

    //port-ul server-ului
    private static final int PORT = 9999;

    //multimea numelor clientilor activi, pastrata sub forma unui HashSet
    //deoarece numele acestora trebuie sa fie unice si
    //pentru a verifica rapid daca un anumit nume este disponibil sau nu
    private static HashSet<String> numeUtilizatori = new HashSet<String>();

    private static HashSet<Integer> ClientsWaitingIDs = new HashSet<Integer>();
    private static HashMap<Integer, Socket> clientSockets = new HashMap<Integer, Socket>();

    private static HashMap<Integer, Integer> clientSocketsCatLevels = new HashMap<Integer, Integer>();

    private static int clientsID = 0;

    private static HashMap<Integer, Integer> connectedPlayers = new HashMap<Integer, Integer>();

    public static void main(String[] args) throws Exception {
        //se creeaza server-ul folosind port-ul indicat
        try (ServerSocket ServerChat = new ServerSocket(PORT)) {
            System.out.println("Server-ul a pornit!");

            while (true) {
                Socket cs = ServerChat.accept();

                clientSockets.put(clientsID, cs);
                FirUtilizator firUtilizator = new FirUtilizator(cs, clientsID);
                boolean connectionInitialized = false;

                firUtilizator.addObserver(new Observer() {

                    Socket otherClientSocket = null;
                    PrintWriter connectedClientOut = null;
                    Integer otherPlayerID = -1;

                    public void update(Observable obj, Object arg) {
                        System.out.println("*" + connectedPlayers.get(clientsID ));
                        String otherPlayerIDString = arg.toString().substring(0, arg.toString().indexOf("{}"));
                        arg = arg.toString().substring(arg.toString().indexOf("{}") + 2);
                        try {
                            otherPlayerID = Integer.parseInt(otherPlayerIDString);
                        } catch (Exception e) {
                            System.out.println("No other player ID");
                        }

                        if (arg == null) {
                            return;
                        }
                        if (otherPlayerID != -1) {
                            if (connectionInitialized == false) {
                                try {
                                    otherClientSocket = clientSockets.get(otherPlayerID);
                                    connectedClientOut = new PrintWriter(otherClientSocket.getOutputStream(), true);
                                } catch (Exception ex) {
                                    Logger.getLogger(SocketsServer.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            connectedClientOut.println(arg);
                        }
                        System.out.println("---Received message from client: " + arg);

                    }
                });

                new Thread(firUtilizator).start();

                clientsID++;
            }
        }
    }

    // Observer Design PAttern: https://en.wikipedia.org/wiki/Observer_pattern
    private static class FirUtilizator extends Observable implements Runnable {

        private String nume;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int id;
        PrintWriter connectedClientOut = null;


        public FirUtilizator(Socket socket, Integer id) {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                //se creeaza fluxurile de comunicare cu clientul
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("Nume utilizator?");
                    nume = in.readLine();

                    synchronized (this) {
                        //server-ul verifica daca numele respectiv mai este utilizat sau nu
                        if (!numeUtilizatori.contains(nume)) {
                            numeUtilizatori.add(nume);
                            break;
                        }
                    }
                }

                out.println("Nume acceptat!");


                out.println("_CAT_LEVEL?");

                String input = in.readLine();

                int timeout = 5;

                while ((!input.startsWith("_CAT_LEVEL:")) && timeout-- > 0) {
                    out.println("_CAT_LEVEL?");

                    input = in.readLine();
                }

                if (timeout == 0) {
                    return;
                }

                System.out.println("LEVEL:" + input.replace("_CAT_LEVEL:", ""));

                int catLevel = Integer.parseInt(input.replace("_CAT_LEVEL:", ""));
                System.out.println("Cat level:" + catLevel);
                System.out.println("ID:" + id);

                clientSocketsCatLevels.put(id, catLevel);

                System.out.println("Cat level:" + catLevel);
                
                
                while(true) {
                    if(!connectedPlayers.containsKey(id)) {
                        // Verific daca in coada de asteptare exista un client cu care se poate conecta
                        // Daca nu, il adaug in coada de asteptare
                        if(!ClientsWaitingIDs.contains(this.id)) {
                            ClientsWaitingIDs.add(this.id);
                            System.out.println("Clientul cu id-ul " + this.id + " a fost adaugat in coada de asteptare");
                            out.println("Waiting for matching player to connect...");
                        }
                       
                        for (int i = 0; i < ClientsWaitingIDs.size(); i++) {
                            if (i != id && abs(clientSocketsCatLevels.get(i) - catLevel) < 4) {
                                connectedPlayers.put(i, id);
                                connectedPlayers.put(id, i);
                                Socket otherClientSocket = clientSockets.get(connectedPlayers.get(id));
                                connectedClientOut = new PrintWriter(otherClientSocket.getOutputStream(), true);
                                ClientsWaitingIDs.remove(this.id);
                                out.println("Connected with player" + i + " Game starts.");
                                break;
                            }
                        }
                    }
                
//
//                while (connectedPlayers.get(id) == null) {
//                    input = in.readLine();
//                    out.println("Waiting for other player to connect");
//                }

//                while (true) {
                    input = in.readLine();
                    
                    if (input != null) {
                        setChanged();
                        notifyObservers(connectedPlayers.get(id) + "{}" + input);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            } finally {
                // TODO: elimina din clientSockets si connected Players
                //clientul s-a deconectat, deci trebuie sa fie eliminat
                //din lista clientilor activi
//                clientSockets.remove(this.id);
            
                connectedPlayers.remove(connectedPlayers.get(id));
                connectedPlayers.remove(this.id);

                System.out.println("Utilizatorul " + nume + " s-a deconectat de la server!");

                if (nume != null) {
                    numeUtilizatori.remove(nume);
                }
                try {
                    socket.close();
                    clientSockets.get(connectedPlayers.get(id)).close();
                    System.out.println("Utilizatorul " + nume + " s-a deconectat de la server!");
                    connectedClientOut.println("The other player is disconnected. Game ends.");
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}
