package msm.model;

import java.time.LocalDateTime;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderItem {
	private IntegerProperty orderIdProperty;
	private StringProperty planeIdProperty;
	private StringProperty leaveAirportProperty;
	private StringProperty arriveAirportProperty;
	private IntegerProperty seatNumProperty;
	private StringProperty leaveTimeProperty;
	private StringProperty arriveTimeProperty;
	private StringProperty passengerNameProperty;
	private StringProperty isGottenProperty;
	private StringProperty passengerIdProperty;
	private DoubleProperty paymentProperty;
	private LocalDateTime leaveDateTime;
	private boolean returnTag = false;
	private boolean gottenTag = false;
	
	public OrderItem()
	{
		this(null, null, null, null, null, null, false, null, null, null);
	}
	
	public OrderItem(String planeId, String leaveAirport, String arriveAirport, Integer seatNum,  
			String leaveTime, String passengerName, boolean isGotten, Integer orderId, String passengerId,
			String arriveTime)
	{
		planeIdProperty = new SimpleStringProperty(planeId);
		leaveAirportProperty = new SimpleStringProperty(leaveAirport);
		arriveAirportProperty = new SimpleStringProperty(arriveAirport);
		seatNumProperty = new SimpleIntegerProperty(seatNum);
		leaveTimeProperty = new SimpleStringProperty(leaveTime);
		passengerNameProperty = new SimpleStringProperty(passengerName);
		isGottenProperty = new SimpleStringProperty(isGotten ? "��ȡƱ" : "δȡƱ");
		orderIdProperty = new SimpleIntegerProperty(orderId);
		passengerIdProperty = new SimpleStringProperty(passengerId);
		arriveTimeProperty = new SimpleStringProperty(arriveTime);
	}
	
	public OrderItem(String planeId, String leaveAirport, String arriveAirport, Integer seatNum,  
			String leaveTime, String passengerName, Double payment, String arriveTime,
			Integer orderId, String passengerId)
	{
		this(planeId, leaveAirport, arriveAirport, seatNum, leaveTime, passengerName, false, orderId,
				passengerId, arriveTime);	
		paymentProperty = new SimpleDoubleProperty(payment);
	}
	
	public LocalDateTime getLeaveDateTime()
	{
		return leaveDateTime;
	}
	
	public void setLeaveDateTime(LocalDateTime value)
	{
		leaveDateTime = value;
	}
	
	public boolean getReturntag()
	{
		return returnTag;
	}
	
	public boolean getGottenTag()
	{
		return gottenTag;
	}
	
	public void setReturnTag(boolean value)
	{
		returnTag = value;
	}
	
	public void setGottenTag(boolean value)
	{
		gottenTag = value;
	}
	
	public DoubleProperty paymentProperty()
	{
		return paymentProperty;
	}
	
	public StringProperty arriveTimeProperty()
	{
		return arriveTimeProperty;
	}
	
	public StringProperty passengerIdProperty()
	{
		return passengerIdProperty;
	}
	
	public StringProperty planeIdProperty()
	{
		return planeIdProperty;
	}
	
	public StringProperty leaveAirportProperty()
	{
		return leaveAirportProperty;
	}
	
	public StringProperty arriveAirportProperty()
	{
		return arriveAirportProperty;
	}
	
	public IntegerProperty seatNumProperty()
	{
		return seatNumProperty;
	}
	
	public StringProperty leaveTimeProperty()
	{
		return leaveTimeProperty;
	}
	
	public StringProperty passengerNameProperty()
	{
		return passengerNameProperty;
	}
	
	public IntegerProperty orderIdProperty()
	{
		return orderIdProperty;
	}
	
	public StringProperty isGottenProperty()
	{
		return isGottenProperty;
	}
}
