package msm.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FlightStatisticInfo {
	private StringProperty idProperty;
	private StringProperty leaveTimeProperty;
	private StringProperty leaveAirportProperty;
	private DoubleProperty fullRateProperty;
	private LocalDateTime leaveDateTime;
	private DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("yyyyƒÍMM‘¬dd»’  HH:mm:ss a");
	
	public FlightStatisticInfo()
	{
		
	}
	
	public FlightStatisticInfo(String planeId, LocalDateTime leaveDateTime, String leaveAirport, Double rate)
	{
		idProperty = new SimpleStringProperty(planeId);
		leaveAirportProperty = new SimpleStringProperty(leaveAirport);
		fullRateProperty = new SimpleDoubleProperty(rate);
		this.leaveDateTime = leaveDateTime;
		leaveTimeProperty = new SimpleStringProperty(this.leaveDateTime.format(dFormat));
		
	}
	
	public StringProperty leaveAirportProperty()
	{
		return this.leaveAirportProperty;
	}
	
	public StringProperty leaveTimeProperty()
	{
		return this.leaveTimeProperty;
	}
	
	public StringProperty idProperty()
	{
		return this.idProperty;
	}
	
	public DoubleProperty fullRateProperty()
	{
		return fullRateProperty;
	}
	
	public LocalDateTime getLeaveDateTime()
	{
		return leaveDateTime;
	}
}
