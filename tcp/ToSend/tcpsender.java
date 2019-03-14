

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class tcpSender {
    /*
    port 2225,ip,file
    check file exist
        1->go further
        0->file does not exist
    ping to ip(connet to ip)
        1->go further
        0->not in local network(null)(no response)(connection problem)
    send file name
        1->send file
        0->path does not exist
    send file
    exit
    */
    public static void main(String args[]){
        InetAddress ip = null;
        Socket s = null;
        String tmp;
        int t1,loopL;        
        byte[] buf;
        byte[] b;
        InputStream is = null;
        OutputStream os = null;
        FileInputStream fis;
        int port=2226;//server side port
        Scanner sc = new Scanner(System.in);
	//System.out.println("port=>"+port);
        if(args.length!=2){
            System.out.println("input is not valid\nInput Format:-tcp \"fileName\" \"location@ipAddress\"");
            return;
        }
	if(!args[1].contains("@")){
            System.out.println("input is not valid\nInput Format:-tcp \"fileName\" \"location@ipAddress\"");
            return;
        }
        //System.out.println("args[0]->"+args[0]);
        //System.out.println("args[1]->"+args[1]);
        File f1 = new File(args[0]);
        /*
        if(!f1.exists()){
            System.out.println("file does not exists");
            return;
        }else{
            System.out.println("file exists");
        }
        */
        try {
            fis = new FileInputStream(f1);
        } catch (FileNotFoundException ex) {
            System.out.println("file not found ");
            // error in file input stream
            return;
            //Logger.getLogger(shareS.class.getName()).log(Level.SEVERE, null, ex);
        }
        String fLOnLS=args[1].substring(0, args[1].indexOf("@"));//file loaction on listner side
        String ipToConnect=args[1].substring(args[1].indexOf("@")+1);
        try {
            ip =InetAddress.getByName(ipToConnect);
        } catch (UnknownHostException ex) {
            System.out.println("error in ip address");
            //Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("file location On Listener Side=>"+fLOnLS+"\nipToConnect=>"+ip.getHostAddress()+"\nfile name is=>"+args[0]);
        //connect to Host
        try {
            System.out.println("socket connecting...");
            s= new Socket(ip.getHostAddress(),port);
        } catch (Exception ex) {
            System.out.println("socket connection error");
            Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        //getting streams
        try {
            os=s.getOutputStream();
            is=s.getInputStream();
        } catch (Exception ex) {
            System.out.println("error in getting stream");
            Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        //sending file location
        //System.out.println("sending file location");
        t1=fLOnLS.length();
        try {
            os.write(t1);
            t1=is.read();
           // System.out.println("location length ACK=>"+t1);
            os.write(fLOnLS.getBytes());
            t1=is.read();
            //System.out.println("t1=>"+t1);
            if(t1==0){
                System.out.println("directory does not exist");
                s.close();
                return;
            }
            /*else{
                System.out.println("File location Ack=>"+t1);
            }*/
        } catch (Exception ex) {
            System.out.println("error in sending file location");
            Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
            try {
                s.close();
            } catch (Exception ex1) {
                Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        }
        //sending file name
        //System.out.println("sending file name");
        t1=args[0].length();
        //System.out.println("t1 for file name=>"+t1);
        try {
            os.write(t1);
            t1=is.read();
            //System.out.println("file name length ACK=>"+t1);
            os.write(args[0].getBytes());
            t1=is.read();
            //System.out.println("t1=>"+t1);
            if(t1==0){
                System.out.println("file already exists or you dont have permission to create file in that folder");
                s.close();
                return;
            }
            /*else{
                System.out.println("File name Ack=>"+t1);
            }*/
        } catch (Exception ex) {
            System.out.println("error in sending file name");
            Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
            try {
                s.close();
            } catch (Exception ex1) {
                Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        }
        //sending file
        //sending loop length
        //System.out.println("f1.length ="+f1.length()+"\n t1 (int)="+(int) f1.length());
        t1=(int) f1.length();
        loopL=t1/65536;
        //System.out.println("lenght of loop is ="+loopL);
        try {
            tmp=String.valueOf(loopL);
                if(tmp.length()<5){
                    switch(tmp.length()){
                        case 1:tmp="0000"+tmp;
                            break;
                        case 2:tmp="000"+tmp;
                            break;
                        case 3:tmp="00"+tmp;
                            break;
                        case 4:tmp="0"+tmp;
                            break;
                    }
                }    
            os.write(tmp.getBytes());
            t1=is.read();
            //System.out.println("after sending looping length "+t1);
        } catch (Exception ex) {
            //Logger.getLogger(shareS.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in sending loop lenght");
            try {
                s.close();
            } catch (IOException ex1) {
                Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return;
        }
        System.out.println("sending file.");
        b = new byte[65536]; //32768
        //reading file
        for(int i=0;i<=loopL;i++){
			System.out.print(".");
            try {
                //file read
                t1=fis.read(b);
                //sending length
                //System.out.println("t1="+t1);
                tmp=String.valueOf(t1);
                if(tmp.length()<5){
                    switch(tmp.length()){
                        case 1:tmp="0000"+tmp;
                            break;
                        case 2:tmp="000"+tmp;
                            break;
                        case 3:tmp="00"+tmp;
                            break;
                        case 4:tmp="0"+tmp;
                        break;
                    }
                }
                //System.out.println("temp="+tmp);
                buf=tmp.getBytes();
                os.write(buf);
                t1=is.read();
                //System.out.println("got length ="+t1);
                //sending packet
                os.write(b);
                t1=is.read();
                //System.out.println("got response for packet="+t1);
            } catch (Exception ex) {
                Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("error in file reader // file cannot be read");
                try {
                    s.close();
                } catch (IOException ex1) {
                    Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex1);
                }
                return;
            }
        }
        //connection close
        try {
            s.close();
        } catch (Exception ex) {
            System.out.println("error in closing socket");
            Logger.getLogger(tcpSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\nBye");
    }
}
