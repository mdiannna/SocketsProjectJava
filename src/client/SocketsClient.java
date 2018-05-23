package client;

import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import players.Cat;
import server.SocketsServer;

public class SocketsClient extends Frame implements ActionListener {

    private Cat cat;
    private Label sendLabel;
    private TextField writeMessage;
    private TextArea messages;
    private Button sendBtn;
    private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;

    public Cat getCat() {
        return this.cat;
    }

    public void initLayout() {
        setLayout(new FlowLayout());

        messages = new TextArea();
        messages.setEditable(false);       // set to read-only
        add(messages);

        sendLabel = new Label("Send");
        add(sendLabel);

        writeMessage = new TextField("", 20);
        add(writeMessage);

        sendBtn = new Button("Send");
        add(sendBtn);

        sendBtn.addActionListener(this);
        setVisible(true);

        setTitle("Cat game");
        setSize(700, 300);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public SocketsClient(Cat cat) {

        this.cat = cat;
        System.out.println("Cat level:" + cat.getLevel());

        initLayout();
        System.out.println("I am a " + cat.getColor() + " cat");
        Scanner sc = new Scanner(System.in);

//        System.out.print("Adresa server: ");
//        String adresaServer = sc.nextLine();
        String adresaServer = "localhost";

//        System.out.print("Port server: ");
//        int portServer = sc.nextInt();
        int portServer = 9999;

        sc.nextLine();

        BufferedReader in;
        PrintWriter out;
        try {
            this.socket = new Socket(adresaServer, portServer);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                //clientul citeste mesajul de la server
                String mesajServer = in.readLine();

                if (mesajServer.startsWith("Nume utilizator?")) {
                    System.out.print("Nume utilizator: ");
                    String nume = sc.nextLine();
                    out.println(nume);
                } else if (mesajServer.startsWith("Waiting for matching player...")) {
                    System.out.print("Waiting for matching player...");
                } else //server-ul ii transmite clientului faptul ca numele de utilizator a fost acceptat
                {
                    if (mesajServer.startsWith("Nume acceptat!")) {
                        System.out.println("Conectare reusita la server!");
                        break;
                    }
                }
            }

            String mesajServer;
            while (true) {
                mesajServer = in.readLine();

                if (mesajServer.startsWith("_CAT_LEVEL?")) {
                    out.println("_CAT_LEVEL:" + cat.getLevel());
                    break;
                }
            }

            MessageThread messageThread = new MessageThread(socket);

            messageThread.addObserver(new Observer() {
                @Override
                public void update(Observable obj, Object arg) {
                    messages.append(arg + "\n");
                    System.out.println(arg + "\n");
                }
            });

            new Thread(messageThread).start();

        } catch (IOException ex) {
            System.out.println("Conectarea esuata la server: " + ex);
        } catch (Exception e) {
            System.out.println("Exception. Somwthing went wrong");
        }
    }

    class MessageThread extends Observable implements Runnable {

        private Socket socket;

        public MessageThread(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("I am running");
            } catch (IOException ex) {
                Logger.getLogger(MessageThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            String input = "";

            while (true) {
                try {
                    input = in.readLine();
                    if (input == null) {
                        break;
                    }
                    System.out.println("Input:" + input);
                    setChanged();
                    notifyObservers(input);
                } catch (IOException ex) {
                    Logger.getLogger(MessageThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newMessage = writeMessage.getText();
        writeMessage.setText("");
        System.out.println(">>:" + newMessage + "\n");
        out.println(newMessage);
        messages.append("me:" + newMessage + "\n");
    }
}
