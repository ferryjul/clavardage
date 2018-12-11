package clavardage;
import java.lang.Thread;
public class debugOnlineUsersManager {

	public static void main(String argv[]) {
		System.out.println("Attempt from port " + argv[0] + " to port " + argv[1]);
		OnlineUsersManager networkDiscovery = new OnlineUsersManager(Integer.parseInt(argv[0]), Integer.parseInt(argv[1]));
		Thread networkDiscoveryThread = new Thread(networkDiscovery);
		networkDiscoveryThread.start();

		System.out.println("Attempt from port " + argv[1] + " to port " + argv[0]);
		OnlineUsersManager networkDiscovery2 = new OnlineUsersManager(Integer.parseInt(argv[1]), Integer.parseInt(argv[0]));
		Thread networkDiscoveryThread2 = new Thread(networkDiscovery2);
		networkDiscoveryThread2.start();
	}

}
