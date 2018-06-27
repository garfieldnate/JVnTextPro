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

package edu.vnu.jvntext.jvntextpro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

import edu.vnu.jvntext.jvntextpro.JVnTextPro;

public class Session extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    //------------------------
    // Data
    //------------------------
    /**
     * The textpro.
     */
    final JVnTextPro textpro;

    /**
     * The incoming.
     */
    private Socket incoming;

    //-----------------------
    // Methods
    //-----------------------

    /**
     * Instantiates a new session.
     *
     * @param textpro the textpro
     */
    public Session(JVnTextPro textpro) {
        this.textpro = textpro;
    }

    /**
     * Sets the socket.
     *
     * @param s the new socket
     */
    public synchronized void setSocket(Socket s) {
        this.incoming = s;
        notify();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public synchronized void run() {
        while (true) {
            try {
                if (incoming == null) {
                    wait();
                }

                logger.info("Opening session socket...");
                BufferedReader in = new BufferedReader(new InputStreamReader(incoming.getInputStream(), "UTF-8"));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(incoming.getOutputStream(), "UTF-8"));

                StringBuilder content = new StringBuilder();

                while (true) {
                    int ch = in.read();
                    if (ch == 0) //end of string
                        break;

                    content.append((char) ch);
                }

                String tagged = textpro.process(content.toString());

                out.write(tagged.trim());
                out.write((char) 0);
                out.flush();
            } catch (InterruptedIOException e) {
                logger.error("Session connection was interrupted", e);
            } catch (Exception e) {
                // catching all exceptions because we don't want the server to crash
                logger.error("Exception caught while running session thread", e);
            }

            //update pool
            //go back in wait queue if there is fewer than max
            this.setSocket(null);
            Vector<Session> pool = TaggingService.pool;
            synchronized (pool) {
                if (pool.size() >= TaggingService.maxNSession) {
                    /* too many threads, exit this one*/
                    return;
                } else {
                    pool.addElement(this);
                }
            }
        }
    }
}
