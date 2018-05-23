package client;

import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import players.Cat;


public class SocketsClient extends Frame implements ActionListener
{
    private Cat cat;
    private Label sendLabel;    
    private TextField writeMessage;
    private TextArea messages; 
    private Button sendBtn;
   
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
        setSize(700,300);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    
    public SocketsClient(Cat cat)
    {
        
        this.cat = cat;
        System.out.println("Cat level:"+cat.getLevel());
        
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
        
        //clientul incearca sa se conecteze la server
        BufferedReader in;
        PrintWriter out;
        try
        {
            Socket socket = new Socket(adresaServer, portServer);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream() , true);

            while(true)
            {
                //clientul citeste mesajul de la server
		String mesajServer = in.readLine();

                //clientul citeste de la tastatura si transmite server-ului un nume de utilizator 
                //pana cand numele transmis este diferit de numele tuturor utilizatorilor conectati in acel moment
                if (mesajServer.startsWith("Nume utilizator?"))
                {
                    System.out.print("Nume utilizator: ");
                    String nume = sc.nextLine();
                    out.println(nume);
                } else  if (mesajServer.startsWith("Waiting for matching player..."))
                    {
                        System.out.print("Waiting for matching player...");
                    } else
                        //server-ul ii transmite clientului faptul ca numele de utilizator a fost acceptat
                        if (mesajServer.startsWith("Nume acceptat!"))
                        {
                            System.out.println("Conectare reusita la server!");
                            break;
                        }
            }
			
            String mesajServer = "";
			//comunicarea cu server-ul (la fel ca in cazul chat-ului simplu)

            //TODO:threaduri separate pentru trimis si primit mesaje dupa ce s-a conectat? 
            // TODO: Observer?
            while (true)
            {
                 mesajServer = in.readLine();

                if (mesajServer.startsWith("_CAT_LEVEL?")) {
                    out.println("_CAT_LEVEL:" + cat.getLevel());
                } else {
                    System.out.println(mesajServer);

                    messages.append(mesajServer + "\n");
//                    out.println(mesajServer);
                    System.out.print("Mesaj: ");
                    String message = sc.nextLine();
                    out.println(message);

                }
            }
        }
        catch (IOException ex)
        {
            System.out.println("Conectarea esuata la server: " + ex);
        }
        catch(Exception e) {
            System.out.println("Exception. Somwthing went wrong");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newMessage = writeMessage.getText();
        writeMessage.setText("");
        System.out.println(">>:" + newMessage + "\n");
//        messages.setForeground(Color.red);
        messages.append(newMessage + "\n");
    }
}
