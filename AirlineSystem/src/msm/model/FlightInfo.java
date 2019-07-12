package msm.model;

import java.time.LocalDateTime;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlightInfo {
	private StringProperty idProperty;
	private StringProperty leaveAirportProperty;
	private StringProperty arriveAirportProperty;
	private StringProperty leaveTimeProperty;
	private StringProperty arriveTimeProperty;
	private DoubleProperty priceProperty;
	private LocalDateTime leaveDateTime;
	private Integer seatNum;
	
	public FlightInfo()
	{
		this(null, null, null, null, null, null, null);
	}
	
	public FlightInfo(String id, String leaveAirport, String arriveAirport,
			String leaveTime, String arriveTime, Double price, LocalDateTime leaveDateTime)
	{
		idProperty = new SimpleStringProperty(id);
		leaveAirportProperty = new SimpleStringProperty(leaveAirport);
		arriveAirportProperty = new SimpleStringProperty(arriveAirport);
		leaveTimeProperty = new SimpleStringProperty(leaveTime);
		arriveTimeProperty = new SimpleStringProperty(arriveTime);
		priceProperty = new SimpleDoubleProperty(price);
		this.leaveDateTime = leaveDateTime;
		this.seatNum = 0;
	}
	
	public StringProperty idProperty()
	{
		return this.idProperty;
	}
	
	public String getId()
	{
		return this.idProperty.get();
	}
	
	public StringProperty leaveAirportProperty()
	{
		return this.leaveAirportProperty;
	}
	
	public StringProperty arriveAirportProperty()
	{
		return this.arriveAirportProperty;
	}
	
	public StringProperty leaveTimeProperty()
	{
		return this.leaveTimeProperty;
	}
	
	public String getLeaveTime()
	{
		return this.leaveTimeProperty.get();
	}
	
	public StringProperty arriveTimeProperty()
	{
		return this.arriveTimeProperty;
	}
	
	public DoubleProperty priceProperty()
	{
		return this.priceProperty;
	}
	
	public Double getPrice()
	{
		return this.priceProperty.get();
	}
	
	public LocalDateTime getLeaveDateTime()
	{
		return leaveDateTime;
	}
	
	public Integer getSeatNum()
	{
		return seatNum;
	}
	
	public void setSeatNum(Integer seatNum)
	{
		this.seatNum = seatNum;
	}
}
