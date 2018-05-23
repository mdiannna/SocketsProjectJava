package server;

import java.io.*;
import java.io.PrintWriter;
import static java.lang.Math.abs;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import players.*;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketsServer
{

    //port-ul server-ului
    private static final int PORT = 9999;

    //multimea numelor clientilor activi, pastrata sub forma unui HashSet 
    //deoarece numele acestora trebuie sa fie unice si 
    //pentru a verifica rapid daca un anumit nume este disponibil sau nu
    private static HashSet<String> numeUtilizatori = new HashSet<String>();

    //multimea fluxurilor de iesire ale server-ului catre clienti, 
    //folosita pentru a transmite mai usor un mesaj catre toti clientii
    private static HashSet<PrintWriter> fluxuriCatreUtilizatori = new HashSet<PrintWriter>();
    private static ArrayList<Integer> ClientsWaitingIDs = new ArrayList<Integer>();
//        private static ArrayList<Integer> ClientsWaiting = new ArrayList<Integer>();
    private static HashMap<Integer, Socket> clientSockets = new HashMap<Integer,Socket>();
    
    private static HashMap<Integer, Integer> clientSocketsCatLevels = new HashMap<Integer, Integer>();
    

    private static int clientsID = 0;
    
    private static HashMap<Integer, Integer> connectedPlayers = new HashMap<Integer, Integer>();


    public static void main(String[] args) throws Exception
    {
        //se creeaza server-ul folosind port-ul indicat
        try(ServerSocket ServerChat = new ServerSocket(PORT)) 
        {
            System.out.println("Server-ul a pornit!");            
            
            //server-ul asteapta ca un client sa se conecteze si apoi
            //creeaza o conexiune cu acesta pe un fir de executare separat
            while (true)
            {
               Socket cs = ServerChat.accept();
               
               clientSockets.put(clientsID, cs);
//               new FirUtilizator(cs, clientsID).start();
//                Thread thread = new Thread(new FirUtilizator(cs, clientsID));
//                thread.start();
                FirUtilizator firUtilizator = new FirUtilizator(cs, clientsID);
                boolean connectionInitialized = false;
                
                firUtilizator.addObserver(new Observer() {
                    
                    Socket otherClientSocket = null;
                    PrintWriter connectedClientOut = null;
                    Integer otherPlayerID = -1;
                    
                    public void update(Observable obj, Object arg) {
                      System.out.println("*" + connectedPlayers.get(clientsID+1));
                        String otherPlayerIDString = arg.toString().substring(0, arg.toString().indexOf("{}"));
                        arg = arg.toString().substring(arg.toString().indexOf("{}")+2);
                        try {
                            otherPlayerID = Integer.parseInt(otherPlayerIDString);
                        } catch(Exception e) {
                            System.out.println("No other player ID");
                        }
                        
                        if(arg==null) {
                            return;
                        }
                        if(otherPlayerID!=-1){
                             if(connectionInitialized==false) {
                                 try {
                                     otherClientSocket = clientSockets.get(otherPlayerID);
                                     connectedClientOut = new PrintWriter(otherClientSocket.getOutputStream() , true);
                                 } catch (Exception ex) {
                                     Logger.getLogger(SocketsServer.class.getName()).log(Level.SEVERE, null, ex);
                                 }
                             }
                             System.out.println("---obs1:--Received message from client: " + arg);
                             connectedClientOut.println(arg);
                        }
                        System.out.println("---obs2:--Received message from client: " + arg);

                    }
                });

                new Thread(firUtilizator).start();
                
                
               clientsID++;
            }
        }
    }

    //clasa interna prin care server-ul realizeaza conexiunea cu un client,
    //folosind un fir de executare separat
    private static class FirUtilizator extends Observable implements Runnable 
    {
        private String nume;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int id;

        public FirUtilizator(Socket socket, Integer id)
        {
            this.socket = socket;
            this.id = id;
        }

        //actiunile efectuate de server in momentul in care un client se conecteaza:
        //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
        //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
        //3. server-ul inregistreaza clientul
        //4. server-ul preia mesajele clientului si le transmite tuturor celorlalti clienti
        @Override
        public void run()
        {
            try
            {
                //se creeaza fluxurile de comunicare cu clientul
                in =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream() , true);

                //1. server-ul solicita clientului un nume pana cand acesta trimite unul neutilizat in acel moment
                while (true)
                {
                    out.println("Nume utilizator?");
                    nume = in.readLine();
                    
                    synchronized(this)
                    {
                        //server-ul verifica daca numele respectiv mai este utilizat sau nu
                        if (!numeUtilizatori.contains(nume))
                        {
                            numeUtilizatori.add(nume);
                            break;
                        }
                    }
                }

                //2. server-ul comunica clientului faptul ca a fost acceptat numele respectiv
                out.println("Nume acceptat!");
                
                // TODO:
                // Verific daca in coada de asteptare exista un client cu care se poate conecta
                // Daca nu, il adaug in coada de asteptare
                
                ClientsWaitingIDs.add(this.id);
                System.out.println("Clientul cu id-ul " + this.id + " a fost adaugat in coada de asteptare");
                out.println("Waiting for matching player...");
                
                out.println("_CAT_LEVEL?");
                
                String input = in.readLine();
                
                int timeout = 5;
                
                while ((!input.startsWith("_CAT_LEVEL:")) && timeout-- > 0 ) {
                    System.out.println(">>>" + input);
//                    out.println("please output cat level in format: '_CAT_LEVEL:'");
                    out.println("_CAT_LEVEL?");
             
                    input = in.readLine();
                }
                
                if(timeout == 0) {
                    return;
                }
                
                System.out.println("LEVEL:" + input.replace("_CAT_LEVEL:", ""));

                int catLevel = Integer.parseInt(input.replace("_CAT_LEVEL:", ""));
                System.out.println("Cat level:" + catLevel);
                System.out.println("ID:" + id);
                
                clientSocketsCatLevels.put(id,catLevel);
                
                System.out.println("Cat level:" + catLevel);
                 
                for (int i = 0; i < ClientsWaitingIDs.size(); i++) {
                   
                    if(i!=id && abs(clientSocketsCatLevels.get(i) - catLevel) < 4) {
                        connectedPlayers.put(i, id);
                        connectedPlayers.put(id, i);
                        break;
                    }
                }
                
                
//                ///////////////////////////////////////
//                //3. server-ul inregistreaza clientul
//                fluxuriCatreUtilizatori.add(out);
//                
//                System.out.println("Utilizatorul " + nume + " s-a conectat la server!");
//             
                while(connectedPlayers.get(id)==null){
                    input = in.readLine();
                    out.println("Waiting for other player to connect");
                }
                
                PrintWriter connectedClientOut = null;

                if(connectedPlayers.get(id)!=null){
                    Socket otherClientSocket = clientSockets.get(connectedPlayers.get(id));
                    connectedClientOut = new PrintWriter(otherClientSocket.getOutputStream() , true);
                }

                
//                TODO: Observer sa observe cand un client a trimis un mesaj si sa ii 
//                  trimita mesajul la celalalt client

//                while ( connectedPlayers.get(id)!=null)
                while (true)
                {
                    input = in.readLine();
                       if(input==null) {
                           break;
                       }
//                    String response = new Scanner(System.in).next();
                    setChanged();
                    notifyObservers(connectedPlayers.get(id) + "{}" + input);
//            
//                    out.println("Game in progress");
//                    System.out.println("Game in progress");
////                    Jocul propriu-zis
////                    De testat daca celalalt s-a deconectat?
////                        input = in.readLine();
//                        if (input.startsWith("_MESSAGE_OTHER_PLAYER: ")) {
//                            connectedClientOut.println(input.replace("_MESSAGE_OTHER_PLAYER:", ""));
//                        } else {
//                            System.out.println(nume + ": " + input);
//                        }
//                    
//                    if (input.startsWith("_STOP")) {
//                        break;
//                    }
//                        
//                    synchronized(this)
//                    {
//                        for (PrintWriter pw : fluxuriCatreUtilizatori)
//                            if(pw != out)
//                                pw.println(nume + ": " + input);
//                    }
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex);
            }
            finally
            {
                //clientul s-a deconectat, deci trebuie sa fie eliminat 
                //din lista clientilor activi
                
                System.out.println("Utilizatorul " + nume + " s-a deconectat de la server!");
                
                if (nume != null)
                {
                    numeUtilizatori.remove(nume);
                }
                if (out != null)
                {
                    fluxuriCatreUtilizatori.remove(out);
                }
                try
                {
                    socket.close();
                }
                catch (IOException ex)
                {
                    System.out.println(ex);
                }
            }
        }
    }
}
