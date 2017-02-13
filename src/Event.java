public class Event {
 private String type;
 private double time;
 private long delayTime;

 public Event(String type, double time) {
 this.type = type;
 this.time = time;
 this.delayTime=0;
 }

 public String getType() {
 return type;
 }
 public void setType(String type) {
 this.type = type;
 }

 public double getTime() {
 return time;
 }
 public void setTime(double time) {
 this.time = time;
 }

 public void print(){
 System.out.println(type + " @ " + time);
 }
 public long getWaiting(long l){
	 
	 return (l-this.delayTime);
 }

public void setWaiting(long currentTimeMillis) {
	this.delayTime = currentTimeMillis;
	// TODO Auto-generated method stub
	
}
public long getDelayTime()
{
	return this.delayTime;
}
}