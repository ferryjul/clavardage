package clavardage;

import java.net.InetAddress;
import java.io.IOException;

public class TestConversation {


public static void main(String argv[]) {

ConversationManager CM = new ConversationManager();
Thread t = new Thread(CM);
t.start();
Conversation conv = CM.createConversation(InetAddress.getLoopbackAddress(),8042);

conv.sendMsg("Bonjour je suis Quentin");





}





}
