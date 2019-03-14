

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

class tcpListener {
    public static void main(String args[]){
            /*
            port 2226
            listen for connection
            get file name(and location)
            location 0->default
            check location exists
            send 1->if exists
            send 0->if not exists
            get file
            */
            //int port=2226;
            ServerSocket ss = null;
            //change here for linux and windows
        try {    
            ss = new ServerSocket(2226);
        } catch (Exception ex) {
            System.out.println("error in socketc creation");
            Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(true){
            Socket s = null;
            InputStream is = null;
            OutputStream os = null;
            File f1 = null;
            FileOutputStream fos = null;
            int t1;       //temporary
            String tmp;   //temporary
            byte[] buf = null;
            byte[] b;
            int loopL = 0;
            String defaultPath=System.getProperty("user.dir");
            //System.out.println("while started");
            try {
                System.out.println("\nlisting for new file...");
                s=ss.accept();
            } catch (Exception ex) {
                System.out.println("error in connection accept");
                Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            //getting streams
            try {
                os=s.getOutputStream();
                is=s.getInputStream();
            } catch (IOException ex) {
                Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            //getting file location;
            System.out.println("getting file location");
            try {
                t1=is.read();
                os.write(1);
                buf = new byte[t1];
                is.read(buf);
                //checking file location
                tmp=new String(buf);
                System.out.println(tmp+":");
                if(tmp.length()==0){
                    System.out.println("default path selected\npath=>"+defaultPath);
                    os.write(1);
                }else{
                    f1 = new File(tmp);
                    if(f1.isDirectory()){
                        defaultPath=f1.getAbsoluteFile().toString();
                        System.out.println("directory exists");
                        os.write(1);
                    }else{
                        System.out.println("directory does not exists");
                        os.write(0);
                        s.close();
                        continue;
                    }   
                }
            } catch (Exception ex) {
                System.out.println("error in getting file location");
                //Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            //getting fille name
            System.out.println("getting file name");
                try {
                    t1=is.read();
                    //System.out.println("t1 we got for file name=>"+t1);
                    os.write(1);
                    buf = new byte[t1];
                    is.read(buf);
                    tmp= new String(buf);
                    System.out.println("file name=>"+tmp);
                    if(defaultPath.contains("/")){
                        defaultPath=defaultPath+"/"+tmp;
                    }else{
                        defaultPath=defaultPath+"\\"+tmp;
                    } 
                    f1= new File(defaultPath);
                    if(f1.createNewFile()){
                        os.write(1);
                        System.out.println("file created");
                    }else{
			System.out.println("file alreay exists");
                        os.write(0);
			s.close();
                        continue;
                    }
                } catch (Exception ex) {
		    System.out.println("cannot create file");
                    try {
                        os.write(0);
                        s.close();
                        continue;
                    } catch (IOException ex1) {
                        Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                 //getting looping length
                 buf = new byte[5];
                try { 
                    is.read(buf);
                    tmp = new String(buf);
                    //System.out.println(tmp+" and "+tmp.length());           
                    loopL=Integer.parseInt(tmp);
                    //System.out.println("got looping length ="+loopL);
                    os.write(1);
                } catch (Exception ex) {
                    System.out.println("error in getting looping length");
                        try {
                            s.close();
                            //System.out.println(ex);
			    continue;
                            //Logger.getLogger(shareR.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex1) {
                           Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                }
                //getting packet
                try {
                    fos = new FileOutputStream(f1);
                } catch (Exception ex) {
                    System.out.println("error in file output stream ");
                }
                System.out.println("getting file.");
                for(int i=0;i<=loopL;i++){
		    System.out.print(".");
                //getting length of packet
                    try { 
                        is.read(buf);
                        t1=Integer.parseInt(new String(buf));
                        b = new byte[t1];
                        //System.out.println("got t1 as ="+t1);
                        os.write(i);
                        //getting packet
                        is.read(b);
                        fos.write(b);
                        os.write(i);
                        //System.out.println("no.of loop completed="+i);
                    } catch (Exception ex) {
                        //Logger.getLogger(shareR.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("error in getting packet");
                    try {
                        s.close();
                        fos.close();
                    } catch (Exception ex1) {
                        Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
            try {
                fos.close();
                os.close();
                is.close();
            } catch (Exception ex) {
                System.out.println("error in closing");
        }
            try {
                //connection close
                s.close();
            } catch (Exception ex) {
                System.out.println("error in closeing socket");
                Logger.getLogger(tcpListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println("while ended");
        }
    }
}
