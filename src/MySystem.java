import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class MySystem {

	private final int MAX_DEPARTURES = 100000;
	private Server server;
	private Server server2;
	private ArrayList<Event> eventList;
	private double clock;
	private int qSize;
	private int departures;
	private ArrayList<Double> IATimes = new ArrayList<>();

	private HashMap<Integer, Integer> serverQueue = new HashMap<>();
	private double departTime = 0;
	private double prevTime = 0;
	private double arrivalTime = 0;

	private int numOver = 0;

	private double avgArrivalTime = 0;
	private double avgServiceTime = 0;
	
	private int numPackets = 0;
	
	private double IATime2 = 0;
	
	public MySystem() {
		this.server = new Server();
		server2 = new Server();
		this.eventList = new ArrayList<Event>();
		this.clock = 0;
	}

	public void run() {
		initialize();
		while (!eventList.isEmpty()) { // while there is a next event
			Event e = eventList.get(0); // take the first "future event"
			clock = e.getTime(); // progress clock
			
			/* more code to be added here */
			
			if (e.getType().equals("arrival")) { // if the event is an arrival
				arrivalTime = e.getTime();
				avgArrivalTime += arrivalTime;
				numPackets++;
				eventArrives();
			} else if (e.getType().equals("departure")) {
				departTime = e.getTime();
				avgServiceTime += departTime;
				eventDeparts();
				if (departTime - arrivalTime > 0.3) {
					numOver++;
				}
				prevTime = clock;
			} else if (e.getType().equals("print")) {
				System.out.println("~~~~~~~~" + e.getTime() + "~~~~~~~~~~");
				eventList.forEach(event -> {
					if (event.getType().equals("arrival") ||
							event.getType().equals("departure")) {
						System.out.println(event.getType());
						System.out.println(event.getTime());
					}
				});
				
				avgArrivalTime /= numPackets;
				avgServiceTime /= numPackets;
				System.out.println("arrival rate = " + 1/avgArrivalTime);
				System.out.println("service rate = " + 1/avgServiceTime);
				System.out.println((1/avgArrivalTime) / (1/avgServiceTime));
				
				System.out.println("is server busy? " + server.isBusy());
				avgArrivalTime = 0;
				avgServiceTime = 0;
				numPackets = 0;
			}
			eventList.remove(0);
		}
		stop();
	}

	public void eventArrives() {
		qSize++;
		if (!server.isBusy()) { // if server is idle
			server.setBusy(true); // set server busy
			Event ev = new Event("departure", server.getServiceTime() + clock); // create new departure event
			int pos = -1;
			for (Event event : eventList) { // insert new departure event in the approriate position
				if (event.getTime() < ev.getTime()) {
					pos = eventList.indexOf(event);
				}
			}
			eventList.add(pos + 1, ev);
		}
		Event ev = new Event("arrival", getIATime() + clock); // generate next arrival
		if (ev.getTime() - clock > 0) { // if maximum number of arrivals is not reached, place the new arrival in the appropriate position in the event list
			int pos = -1;
			for (Event event : eventList) {
				if (event.getTime() < ev.getTime()) {
					pos = eventList.indexOf(event);
				}
			}
			eventList.add(pos + 1, ev);
		}
	}
	
	public void eventDeparts() {
		
		if (serverQueue.containsKey(qSize)) {
			int value = serverQueue.get(qSize)+1;
			serverQueue.put(qSize, value);
		} else {
			serverQueue.put(qSize,1);
		}
		qSize--;
		departures++;

		server.setBusy(false);
		if (qSize > 0) {
			Event ev = new Event("departure", server.getServiceTime() + clock);
			server.setBusy(true);
			int pos = -1;
			for (Event event : eventList) {
				if (event.getTime() < ev.getTime()) {
					pos = eventList.indexOf(event);

				}
			}
			eventList.add(pos + 1, ev);
		}
	}
	
	public void initialize(){
		importTimes();
		Event e = new Event("arrival", 0);//the arrival of the first customer is set to 0
		Event e1 = new Event("print", 500);
		Event e2 = new Event("print", 5000);
		Event e3 = new Event("print", 10000);
		IATimes.remove(0);
		eventList.add(e);
		eventList.add(e1);
		eventList.add(e2);
		eventList.add(e3);
	}
	
	public void stop() {
		System.out.println("Server queue\n" + serverQueue);
		double probability = (double)numOver / (double)MAX_DEPARTURES;
		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println("Probability of being over 0.3: " + df.format(probability * 100) + "%");
	}

	public static void main(String args[]) {
		MySystem m = new MySystem();
		m.run();
	}

	public double getIATime() {
		if (IATimes.isEmpty())
			return -1; // if there are no more arrivals return -1
		return IATimes.remove(0);
	}

	public void importTimes() {

		Scanner scaS;
		Scanner scaS2;
		Scanner scaIA;
		String path = "src/files/";
		String service = "serviceTimes-100K.txt";
		String interArrivals = "interArrivalTimes-100K.txt";
		String service2 = "serviceTimes2-100K.txt";

		FileReader frS = null;
		FileReader frIA = null;
		FileReader frS2 = null;

		try {
			frS = new FileReader(path + service);
			frIA = new FileReader(path + interArrivals);
			frS2 = new FileReader(path + service2);
		}

		catch (FileNotFoundException e) {
			System.out.println("error opening file" + e);
		}
		scaS = new Scanner(frS);
		scaIA = new Scanner(frIA);
		scaS2 = new Scanner(frS2);
		ArrayList<Double> serviceTimes = new ArrayList<Double>();
		ArrayList<Double> serviceTimes2 = new ArrayList<>();

		for (int i = 0; i < MAX_DEPARTURES; i++) {
			serviceTimes.add(scaS.nextDouble());
			serviceTimes2.add(scaS2.nextDouble());
			IATimes.add(scaIA.nextDouble());
		}
		server.setServiceTimes(serviceTimes);
		server2.setServiceTimes(serviceTimes2);
		scaS.close();
		scaS2.close();
		scaIA.close();
	}
}
