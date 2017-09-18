/* CS 1501
 Primitive chat client. 
 This client connects to a server so that messages can be typed and forwarded
 to all other clients.  Try it out in conjunction with ImprovedChatServer.java.
 You will need to modify / update this program to incorporate the secure elements
 as specified in the Assignment sheet.  Note that the PORT used below is not the
 one required in the assignment -- for your SecureChatClient be sure to 
 change the port that so that it matches the port specified for the secure
 server.


 Ammended By: Joshua Rodstein
 1501 Alg Implementation
 Project 4
 jor94@pitt.edu
 */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
    Socket connection;

    private byte[] encryptName;
    ObjectOutputStream myWriter; 
    ObjectInputStream myReader;
    BigInteger E;
    BigInteger N;
    BigInteger key;
    BigInteger encryptKey;
    SymCipher cipher;
    String cipherType;

    public SecureChatClient() {
        try {

            // begin Handshaking
            myName = JOptionPane.showInputDialog(this, "Enter your user name: "); // grab user name input
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: "); // grab server name input
            InetAddress addr
                    = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT);   // Connect to server with new
            // Socket

            myWriter = new ObjectOutputStream(connection.getOutputStream()); // initialize myWriter to new OBjOutStream
            myWriter.flush(); // flush myWriter to avoid deadlocking

            myReader = new ObjectInputStream(connection.getInputStream()); // initialize myReader to read from server

            E = (BigInteger) myReader.readObject(); 
            N = (BigInteger) myReader.readObject();
            System.out.println("Key E: " + E + "\nKey N: " + N);

            cipherType = (String) myReader.readObject();  // grab desired encryption type (Add128 or Substitution)
            System.out.println("Encryption type: " + cipherType);

            if (cipherType.equalsIgnoreCase("sub")) {
                cipher = new Substitute();
            } else if (cipherType.equalsIgnoreCase("add")) {
                cipher = new Add128();
            }

            key = new BigInteger(1, cipher.getKey()); // grab key from SymCipher instance
            System.out.println("Symmetric Key: " + key);
            encryptKey = key.modPow(E, N); 

            myWriter.writeObject(encryptKey); // send key to server to use for encryption
            myWriter.flush(); // flush to avoid deadlocking

            encryptName = cipher.encode(myName); // encrypt username

            myWriter.writeObject(encryptName); // send encrypted username to server
            myWriter.flush(); // flush to avoid dealocking

            // HandShake complete
            this.setTitle(myName);      // Set title to identify chatter

            Box b = Box.createHorizontalBox();  // Set up graphical environment for
            outputArea = new JTextArea(8, 30);  // user
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));

            outputArea.append("Welcome to the Chat Group, " + myName + "\n");

            inputField = new JTextField("");  // This is where user will type input
            inputField.addActionListener(this);

            prompt = new JLabel("Type your messages below:");
            Container c = getContentPane();

            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);

            Thread outputThread = new Thread(this);  // Thread is to receive strings
            outputThread.start();                    // from Server

            addWindowListener(
                    new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            try {
                                myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
                                myWriter.flush();
                            } catch (IOException io) {
                                System.out.println("Problem closing client!");
                            }
                            System.exit(0);
                        }
                    }
            );

            setSize(500, 200);
            setVisible(true);

        } catch (Exception e) {
            System.out.println("Problem starting client!");
        }
    }

    public void run() {
        while (true) {
            try {
                byte[] cryptMsg = (byte[]) myReader.readObject(); // grab encrypted msg from server
                String currMsg = cipher.decode(cryptMsg); // pass to SymCipher object for decryption
                outputArea.append(currMsg + "\n");
                
                byte[] bytes = currMsg.getBytes(); // convert currMsg to string for output to console
                System.out.println("Recieved array of bytes: " + Arrays.toString(cryptMsg));
                System.out.println("Decrypted array of bytes: " + Arrays.toString(bytes));
                System.out.println("Corresponding string: " + currMsg);
            } catch (Exception e) {
                System.out.println(e + ", closing client!");
                break;
            }
        }
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        String currMsg = e.getActionCommand();      // Get input value
        inputField.setText("");

        try {
            currMsg = myName + ":" + currMsg; // build string with user name for chat window output
            byte[] byteMsg = cipher.encode(currMsg); // encode newly built message and send to SymCipher obj for encryption
            myWriter.writeObject(byteMsg); // send encrypted msg to server
            myWriter.flush(); // flush to avoid deadlock

            byte[] bytes = currMsg.getBytes(); 
            System.out.println("Original String Message: " + currMsg);
            System.out.println("Array of bytes: " + Arrays.toString(bytes));
            System.out.println("Encrypted array of bytes: " + Arrays.toString(byteMsg));

        } catch (IOException io) {
            System.err.println("Error: Failed to send message to server!");
        }

    }

    public static void main(String[] args) {
        SecureChatClient JR = new SecureChatClient();
        JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
}
