import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
public class mySystem {

	private final int MAX_DEPARTURES = 100000;
	private Server server;
	private ArrayList<Event> eventList;
	private double clock;
	private int qSize;
	private int departures;
	private ArrayList<Double> IATimes;
	private double[] departureTime;
	private int[] totalNumberinServer;
	private long waitingTime;
	private int PartBdelay;
	private ArrayList<Event> eventCaptured;


	public mySystem() {
		this.server = new Server();
		this.eventList = new ArrayList<Event>();
		this.IATimes= new ArrayList<>();
		this.clock = 0;
		this.qSize =0;
		this.departures=0;
		this.departureTime= new double[MAX_DEPARTURES] ;
		this.totalNumberinServer= new int[MAX_DEPARTURES];
		this.waitingTime=0;
		this.PartBdelay=0;
		this.eventCaptured= new ArrayList<>();
		}

	public void run(){
		initialize();
		while(!eventList.isEmpty()){ // while there is a next event
			Event e = eventList.get(0); // take the first
			//"future event"
			clock = e.getTime(); // progress clock
			e.setWaiting(System.nanoTime());
			/* more code to be added here */
			if(e.getType().equals("arrival")){ // if the event is an
				//arrival
				qSize++;
				if(!server.isBusy()){ // if server is idle
					server.setBusy(true); //set server busy
					Event ev = new Event("departure",
							server.getServiceTime()+clock); // create new departure event
					int pos=-1;

					for(Event event : eventList){ // insert new
						//departure event in the approriate position
						if(event.getTime() < ev.getTime()){
							pos = eventList.indexOf(event);
						}
					}
					eventList.add(pos+1,ev);
				}
				Event ev = new Event("arrival", getIATime()+clock); //
				//generate next arrival
				if(ev.getTime()-clock > 0){ // if maximum number of
					//arrivals is not reached, place the new arrival in the appropriate
					//position in the event list
					int pos=-1;
					for(Event event : eventList){
						if(event.getTime() < ev.getTime()){
							pos = eventList.indexOf(event);
						}
					}
					eventList.add(pos+1,ev);
				}
			}
			else if (e.getType().equals("departure")){
				
				// experimental 
				departureTime[departures]= e.getTime();
				
				//trying to capture when t= 500 , 1000, 10000
				if(e.getTime() == 500.00|| e.getTime() == 1000.00 | e.getTime()== 10000.00)
				{
					
					eventCaptured.add(e);
				}
				long rightnow= System.nanoTime();
				long delayed = rightnow-e.getDelayTime(); 
				if((delayed)>= 0.3){
					PartBdelay++;
				}
				
				// experimental section over
				qSize--;
				totalNumberinServer[departures] = qSize;
				departures++;
				if(departures == MAX_DEPARTURES){
					stop();
					return;
				}

				server.setBusy(false);
				if(qSize>0){
					Event ev = new Event("departure",
							server.getServiceTime()+clock);
					server.setBusy(true);
					int pos=-1;
					for(Event event : eventList){
						if(event.getTime() < ev.getTime()){
							pos = eventList.indexOf(event);

						}
					}
					eventList.add(pos+1,ev);

				}
			}
			eventList.remove(0);
		}
	}

	private void stop() {
		// TODO Auto-generated method stub
		System.out.println("Simulation is over");
		
	}

	public void initialize(){
		importTimes();
		Event e = new Event("arrival", 0);//the arrival of the first
		//customer is set to 0
		IATimes.remove(0);
		eventList.add(e);

	}

	public static void main(String args[]){
		
		mySystem m = new mySystem();
		int[] aTime = null;

		m.run();
		m.results();
		m.findPacketDelay();
	}
	
	public void findPacketDelay()
	{
		
	}
	public void results()
	{
		/* Declare array and assign values  {19, 3, 15, 7, 11, 9, 13, 5, 17, 1};*/
		int array[] = totalNumberinServer;
		String output = "Time\t Number of Packets \tHistogram";

		/* Format histogram */
		// For each array element, output a bar in histogram
		for ( int counter = 0; counter <= array.length; counter++ ) {
			
			if(counter!=array.length)
			{
			output += "\n" + departureTime[counter] + "\t" + array[ counter ] + "\t";

			// Print bar of asterisks                               
			for ( int stars = 0; stars < array[ counter ]; stars++ ) {
				output += "*";   
			}
			}else
			{
				output += "\n" + counter + "\t" +"part b total packets stayed in the system more than .3 second is "+ this.PartBdelay +" with porbability of "+calculateProbabilityB()+"\n";
				calculateC();
			}
		}

		/* Print histogram */
		System.out.println(output);
	}
	private String calculateProbabilityB() {
		// TODO Auto-generated method stub
		return null;
	}

	private String calculateC() {
		// TODO Auto-generated method stub
		return eventCaptured.get(0).getType()+"\t event Time: "+ eventCaptured.get(0).getTime()+ "\n"
				+ eventCaptured.get(1).getType()+ "\t event Time: "+ eventCaptured.get(1).getTime()+"\n"
				+eventCaptured.get(2).getType()+ "\t event Time:" + eventCaptured.get(2).getTime()+"\n";}

	public double getIATime(){
		if(IATimes.isEmpty())
			return -1; // if there are no more arrivals return -1
		return IATimes.remove(0);
	}

	public void importTimes(){

		Scanner scaS;
		Scanner scaIA ;
		String path = "src/files/";
		
		String service = "serviceTimes-100K.txt";
		String interArrivals = "interArrivalTimes-100K.txt";

		FileReader frS=null;
		FileReader frIA=null;

		try {
			frS = new FileReader(path + service);
			frIA = new FileReader(path + interArrivals);
		}

		catch(FileNotFoundException e){
			System.out.println("error opening file" + e);
		}
		scaS = new Scanner(frS);
		scaIA = new Scanner(frIA);
		ArrayList<Double> serviceTimes = new ArrayList<Double>();

		for (int i = 0; i < MAX_DEPARTURES; i++){
			serviceTimes.add(scaS.nextDouble());
			IATimes.add(scaIA.nextDouble());
		}
		server.setServiceTimes(serviceTimes);

		scaS.close();
		scaIA.close();
	}
}