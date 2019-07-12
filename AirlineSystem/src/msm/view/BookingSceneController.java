package msm.view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import msm.Main;
import msm.MySqlConnect;
import msm.model.FlightInfo;
import msm.model.UserInfo;

public class BookingSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	private UserInfo userInfo;
	private FlightInfo flightInfo;
	private LocalDate leaveDate;
	private String leaveCityString;
	private String arriveCityString;
	private ObservableList<FlightInfo> flightInfoList = FXCollections.observableArrayList();
	private Vector<Vector<RadioButton>> radioButtonList;
	
	private final int row = 5;
	private final int column = 4;
	
	@FXML
	ToggleGroup toggleGroup;
	@FXML
	GridPane gridPane0;
	@FXML
	GridPane gridPane1;
	
	@FXML
	TextField flightIdField;
	@FXML
	TextField leaveTimeField;
	@FXML
	TextField priceField;
	@FXML
	TextField seatIdField;
	
	@FXML
	TableView<FlightInfo> flightInfoView;
	@FXML
	TableColumn<FlightInfo, String> idColumn;
	@FXML
	TableColumn<FlightInfo, String> leaveTimeColumn;
	@FXML
	TableColumn<FlightInfo, String> arriveTimeColumn;
	@FXML
	TableColumn<FlightInfo, String> leaveAirportColumn;
	@FXML
	TableColumn<FlightInfo, String> arriveAirportColumn;
	@FXML
	TableColumn<FlightInfo, Double> priceColumn;
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		sqlConnect = this.mainapp.getSqlConnect();
		
		flashFlightInfo();
		showFlightInfo(null);
	}
	
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public void setLeaveDate(LocalDate leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	public void setLeaveCityString(String leaveCityString) {
		this.leaveCityString = leaveCityString;
	}
	
	public void setArriveCityString(String arriveCityString) {
		this.arriveCityString = arriveCityString;
	}
	
	private void showFlightInfo(FlightInfo flightInfo) {
		if(flightInfo == null)
		{
			flightIdField.clear();
			leaveTimeField.clear();
			priceField.clear();
		}
		else {
			flightIdField.setText(flightInfo.getId());
			leaveTimeField.setText(flightInfo.getLeaveTime());
			priceField.setText(String.valueOf(flightInfo.getPrice()));
		}
	}
	
	private void clearToggelGroup()
	{		
		for(int i=0;i<row;++i)
		{
			for(int j=0;j<column;++j)
			{
				radioButtonList.get(i).get(j).setToggleGroup(null);
			}
		}
		
		for(int i=0;i<row;++i)
		{
			for(int j=0;j<column;++j)
			{
				radioButtonList.get(i).get(j).setSelected(true);
				radioButtonList.get(i).get(j).setDisable(true);
			}
		}
	}
	
	private void showSeatDetail(FlightInfo flightInfo) {
		clearToggelGroup();
		
		if(flightInfo!=null)
		{
			String commandString = "select seat_id, sold from seatinfo where leave_time = ? and id = ?";
			
			try {
				PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(flightInfo.getLeaveDateTime()));
				preparedStatement.setString(2, flightInfo.getId());
				
				ResultSet rSet = preparedStatement.executeQuery();
				
				while(rSet.next())
				{
					int index = rSet.getInt("seat_id");
					int sold = rSet.getInt("sold");
					
					int i = (index - 1) / column;
					int j = (index - 1) % column;
					
					radioButtonList.get(i).get(j).setSelected(sold == 1);
					radioButtonList.get(i).get(j).setDisable(sold == 1);
					
					if(sold == 0)
						radioButtonList.get(i).get(j).setToggleGroup(toggleGroup);
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
	private void initialize()
	{
		toggleGroup = new ToggleGroup();
		radioButtonList = new Vector<Vector<RadioButton>>(row);
		
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
		leaveTimeColumn.setCellValueFactory(cellData -> cellData.getValue().leaveTimeProperty());
		arriveTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arriveTimeProperty());
		leaveAirportColumn.setCellValueFactory(cellData -> cellData.getValue().leaveAirportProperty());
		arriveAirportColumn.setCellValueFactory(cellData -> cellData.getValue().arriveAirportProperty());
		priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
		
		flightInfoView.setItems(flightInfoList);
		
		for(int i=0;i<row;++i)
		{
			radioButtonList.add(new Vector<RadioButton>(column));
			
			for(int j=0;j<column;++j)
			{
				RadioButton rButton = new RadioButton();
				rButton.setText(String.valueOf(i*column +j+1));
				rButton.setSelected(true);
				rButton.setDisable(true);
				
				radioButtonList.get(i).add(rButton);
				
				if(j<column / 2)
				{
					gridPane0.add(rButton, j, i);
				}
				else {
					gridPane1.add(rButton, j-2, i);
				}
			}
		}
		
		flightInfoView.getSelectionModel().selectedItemProperty().addListener(
	            (observable, oldValue, newValue) -> {
	            	showFlightInfo(newValue);
	            	showSeatDetail(newValue);
	            	flightInfo = newValue;
	            });
		
		toggleGroup.selectedToggleProperty().addListener(
			    (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> 
			    {
			    	if(toggleGroup.getSelectedToggle()!=null)
			    		seatIdField.setText(((RadioButton)toggleGroup.getSelectedToggle()).getText());
			    	else {
						seatIdField.setText("");
					}
			    });
	}
	
	@FXML
	private void flashFlightInfo()
	{
		flightInfoList.clear();
		
		int year = leaveDate.getYear();
		int month = leaveDate.getMonthValue();
		int day = leaveDate.getDayOfMonth();
		
		String commandString =
				"select * from flightinfo where Year(leave_time) = ? and "
				+ "Month(leave_time) = ? and Day(leave_time) = ? and leave_city = ? and "
				+ "arrive_city = ?";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			
			preparedStatement.setInt(1, year);
			preparedStatement.setInt(2, month);
			preparedStatement.setInt(3, day);
			
			preparedStatement.setString(4, leaveCityString);
			preparedStatement.setString(5, arriveCityString);
			
			ResultSet rSet = preparedStatement.executeQuery();
			
			while(rSet.next())
			{
				String idTempString = rSet.getString("id");
				java.sql.Timestamp leaveTimestamp = rSet.getTimestamp("leave_time");
				java.sql.Timestamp arriveTimestamp = rSet.getTimestamp("arrive_time");
				String leaveAirportTempString = rSet.getString("leave_airport");
				String arriveAirportTempString = rSet.getString("arrive_airport");
				Double priceTempDouble = rSet.getDouble("price");
				
				DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("yyyy年MM月dd日  HH:mm:ss a");
				String leaveTimeTempString = leaveTimestamp.toLocalDateTime().format(dFormat);
				String arriveTimeTempString = arriveTimestamp.toLocalDateTime().format(dFormat);
				
				flightInfoList.add(new FlightInfo(idTempString, leaveAirportTempString, 
						arriveAirportTempString, leaveTimeTempString, arriveTimeTempString, 
						priceTempDouble, leaveTimestamp.toLocalDateTime()));
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void clickBooking() {
		if(flightInfo!=null)
		{
			if(seatIdField.getText().length()!=0)
			{
				Integer seatNumInteger = Integer.valueOf(seatIdField.getText());
				flightInfo.setSeatNum(seatNumInteger);
				
				mainapp.showPaymentScene(userInfo, flightInfo, leaveCityString, arriveCityString);
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("提示");
				alert.setHeaderText(null);
				alert.setContentText("请选择座位！");

				alert.showAndWait();
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("提示");
			alert.setHeaderText(null);
			alert.setContentText("请选择航班！");

			alert.showAndWait();
		}
	}
	
	@FXML
	private void clickBack()
	{
		mainapp.showUserScene(userInfo);
	}
}
