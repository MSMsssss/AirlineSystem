package msm.view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import msm.Main;
import msm.MySqlConnect;
import msm.model.FlightStatisticInfo;

public class AdminSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	
	private FlightStatisticInfo selectedFlight = null;
	private ObservableList<FlightStatisticInfo> flightInfoList = FXCollections.observableArrayList();
	private ToggleGroup toggleGroup;
	private ArrayList<RadioButton> rButtonList;
	
	private final int row = 5;
	private final int column = 4;
	private final String SOLD = "X";
	private final String UNSOLD = "O";
	
	@FXML
	private TextField flightIdField;
	@FXML
	private TextField passengerIdField;
	@FXML
	private TextField passengerNameField;
	@FXML
	private TextField seatIdField;
	
	@FXML
	private DatePicker leaveTimePicker;
	@FXML
	private GridPane gridPane0;
	@FXML
	private GridPane gridPane1;
	
	@FXML
	private TableView<FlightStatisticInfo> flightTableView;
	@FXML
	private TableColumn<FlightStatisticInfo, String> planeIdColumn;
	@FXML
	private TableColumn<FlightStatisticInfo, String> leaveTimeColumn;
	@FXML
	private TableColumn<FlightStatisticInfo, String> leaveAirportColumn;
	@FXML
	private TableColumn<FlightStatisticInfo, Double> fullRateColumn;
	
	@FXML
	private void initialize()
	{
		toggleGroup = new ToggleGroup();
		rButtonList = new ArrayList<RadioButton>(row * column);
		
		planeIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		leaveTimeColumn.setCellValueFactory(cellData -> cellData.getValue().leaveTimeProperty());
		leaveAirportColumn.setCellValueFactory(cellData -> cellData.getValue().leaveAirportProperty());
		fullRateColumn.setCellValueFactory(cellData -> cellData.getValue().fullRateProperty().asObject());
		
		flightTableView.setItems(flightInfoList);
		
		for(int i=0;i<row;++i)
		{	
			for(int j=0;j<column;++j)
			{
				RadioButton rButton = new RadioButton();
				rButton.setText(UNSOLD);
				rButton.setUserData(Integer.valueOf(i*column +j+1));
				rButton.setSelected(false);
				rButton.setDisable(true);
				rButton.setToggleGroup(toggleGroup);
				
				rButtonList.add(rButton);
				
				if(j<column / 2)
				{
					gridPane0.add(rButton, j, i);
				}
				else {
					gridPane1.add(rButton, j-2, i);
				}
			}
		}
		
		flightTableView.getSelectionModel().selectedItemProperty().addListener(
				(Observable, oldValue, newValue) ->
				{
					showSeatInfo(newValue);
					selectedFlight = newValue;
				}
				);
		
		toggleGroup.selectedToggleProperty().addListener(
			    (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> 
			    {
			    	if(toggleGroup.getSelectedToggle()!=null)
			    	{
			    		showPassengerInfo(selectedFlight, (Integer)toggleGroup.getSelectedToggle().getUserData());
			    		seatIdField.setText(String.valueOf((Integer)toggleGroup.getSelectedToggle().getUserData()));
			    	}
			    	else 
			    	{
						showPassengerInfo(selectedFlight, null);
						seatIdField.setText("");
					}
			    });
	}
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		sqlConnect = this.mainapp.getSqlConnect();
	}
	
	@FXML
	private void searchFlightInfo()
	{
		flightInfoList.clear();
		
		String planeIdKeyString = flightIdField.getText();
		LocalDate leaveDate = leaveTimePicker.getValue();
		
		String commandString = null;
		
		try {
			PreparedStatement preparedStatement;
			if(planeIdKeyString.isEmpty() && leaveDate == null)
			{
				commandString = "select id, leave_time, leave_airport from flightinfo";
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			}
			else if(!planeIdKeyString.isEmpty() && leaveDate == null)
			{
				commandString = "select id, leave_time, leave_airport from flightinfo "
						+ "where id = ?";
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setString(1, planeIdKeyString);
			}
			else if (planeIdKeyString.isEmpty() && leaveDate!=null)
			{
				commandString = "select id, leave_time, leave_airport from flightinfo "
						+ "where Year(leave_time) = ? and Month(leave_time) = ? and Day(leave_time) = ?";
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setInt(1, leaveDate.getYear());
				preparedStatement.setInt(2, leaveDate.getMonthValue());
				preparedStatement.setInt(3, leaveDate.getDayOfMonth());
			}
			else 
			{
				commandString = "select id, leave_time, leave_airport from flightinfo "
						+ "where Year(leave_time) = ? and Month(leave_time) = ? and Day(leave_time) = ? "
						+ "and id = ?";
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setInt(1, leaveDate.getYear());
				preparedStatement.setInt(2, leaveDate.getMonthValue());
				preparedStatement.setInt(3, leaveDate.getDayOfMonth());
				preparedStatement.setString(4, planeIdKeyString);
			}
			
			ResultSet rSet = preparedStatement.executeQuery();
			
			while(rSet.next())
			{
				String planeIdString = rSet.getString("id");
				java.sql.Timestamp timestamp = rSet.getTimestamp("leave_time");
				String leaveAirportString = rSet.getString("leave_airport");
				Double fullRateDouble = null;
				
				commandString = "call countFullRate(?, ?, @fullrate)";
				PreparedStatement preparedStatement2 = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement2.setString(1, planeIdString);
				preparedStatement2.setTimestamp(2, timestamp);
				preparedStatement2.executeUpdate();
				ResultSet rSet2 = sqlConnect.executeQuery("select @fullrate");
				if(rSet2.next())
				{
					fullRateDouble = rSet2.getDouble("@fullrate");
				}
				
				flightInfoList.add(new FlightStatisticInfo(planeIdString,
						timestamp.toLocalDateTime(), 
						leaveAirportString,
						fullRateDouble*100));
				
				rSet2.close();
				preparedStatement2.close();
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void showSeatInfo(FlightStatisticInfo flightInfo)
	{		
		if(flightInfo == null)
		{
			for(RadioButton rButton: rButtonList)
			{
				rButton.setText(UNSOLD);
				rButton.setDisable(true);
				rButton.setSelected(false);
			}
		}
		else {
			String commandString = "select seat_id, sold from seatinfo "
					+ "where id = ? and leave_time = ?";
			try {
				PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setString(1, flightInfo.idProperty().get());
				preparedStatement.setTimestamp(2, 
						java.sql.Timestamp.valueOf(flightInfo.getLeaveDateTime()));
				ResultSet rSet = preparedStatement.executeQuery();
				
				while(rSet.next())
				{
					Integer seatIdInteger = rSet.getInt("seat_id");
					Integer soldInteger = rSet.getInt("sold");
					
					rButtonList.get(seatIdInteger-1).setDisable(false);
					rButtonList.get(seatIdInteger-1).setText(soldInteger == 0 ? UNSOLD : SOLD);
					rButtonList.get(seatIdInteger-1).setSelected(false);
				}
				
				rSet.close();
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void showPassengerInfo(FlightStatisticInfo flightInfo, Integer seatId)
	{
		if(seatId == null || flightInfo == null)
		{
			passengerIdField.clear();
			passengerNameField.clear();
		}
		else {
			String commandString = "select passenger_id, name from bookinginfo, peopleinfo "
					+ "where passenger_id = id and plane_id = ? and leave_time = ? and seat_id = ?";
			
			try {
				PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setString(1, flightInfo.idProperty().get());
				preparedStatement.setTimestamp(2, 
						java.sql.Timestamp.valueOf(flightInfo.getLeaveDateTime()));
				preparedStatement.setInt(3, seatId);
				ResultSet rSet = preparedStatement.executeQuery();
				
				if(rSet.next())
				{
					passengerIdField.setText(rSet.getString("passenger_id"));
					passengerNameField.setText(rSet.getString("name"));
				}
				else {
					passengerIdField.setText("нч");
					passengerNameField.setText("нч");
				}
				
				rSet.close();
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	@FXML
	private void clickBackButton()
	{
		mainapp.showBeginScene();
	}
}
