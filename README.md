# TCP.service
wanted to create scp like service in linux for all os using java

NOTE:- project under development 

Requirements:- Java and CLI

tcp has to folder in it,
->1)ToGet
    which has two files java class and java code for listener(file reciver)
->2)ToSend
    which has two files java class and java code for sender(file sender)
    
This project is tested on windows using local network(without net on mobile hotspot)

usage:->step1)start tcplistener using java in CLI in "ToGet" folder from PC1(reciver pc)
        step2)(on PC2)open CLI at/in "ToSend" folder and type=>java tcpSender "fileName" "location@ipAddress" 
        
Sender side requires two arguments to run, first argument is "fileName" and second argument has two parts one "location"(in which folder you want to send file) and "ip address" of listener PC(PC1) to send

NOTE:- if you not provide "location" it will send file to "ToGet" folder(default location)
NOTE:- you have to specify path of file location for "fileName" if file is not in ToSend folder(better is to get file in "ToSend" file and then to use CLI to send)
