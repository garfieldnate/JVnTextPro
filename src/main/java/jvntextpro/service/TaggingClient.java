/*
 Copyright (C) 2010 by
 *
 * 	Cam-Tu Nguyen
 *  ncamtu@ecei.tohoku.ac.jp or ncamtu@gmail.com
 *
 *  Xuan-Hieu Phan
 *  pxhieu@gmail.com
 *
 *  College of Technology, Vietnamese University, Hanoi
 * 	Graduate School of Information Sciences, Tohoku University
 *
 * JVnTextPro-v.2.0 is a free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JVnTextPro-v.2.0 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  JVnTextPro-v.2.0); if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package jvntextpro.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TaggingClient {
    //-----------------------
    // Data
    //-----------------------
    /**
     * The host.
     */
    final String host;

    /**
     * The port.
     */
    final int port;

    /**
     * The in.
     */
    private BufferedReader in;

    /**
     * The out.
     */
    private BufferedWriter out;

    /**
     * The sock.
     */
    private Socket sock;

    //-----------------------
    // Methods
    //-----------------------

    /**
     * Instantiates a new tagging client.
     *
     * @param host the host
     * @param port the port
     */
    public TaggingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Connect.
     */
    public void connect() throws IOException {
        sock = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream(), "UTF-8"));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
    }

    /**
     * Process.
     *
     * @param data the data
     * @return the string
     */
    public String process(String data) throws IOException {
        out.write(data);
        out.write((char) 0);
        out.flush();

        //Get data from server
        StringBuilder tagged = new StringBuilder();
        while (true) {
            int ch = in.read();

            if (ch == 0) break;
            tagged.append((char) ch);
        }
        return tagged.toString();
    }

    /**
     * Close.
     */
    public void close() {
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //----------------------------------
    // main method, testing this client
    //---------------------------------

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("TaggingClient [inputfile] [outputfile]");
            return;
        }

        // Create a tagging client, open connection
        TaggingClient client = new TaggingClient("localhost", 2929);

        // read data from file
        // process data, save into another file
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));

        client.connect();
        String line;
        StringBuilder input = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            input.append(line).append("\n");
        }

        String tagged = client.process(input.toString());
        writer.write(tagged + "\n");

        client.close();
        reader.close();
        writer.close();
    }

}
