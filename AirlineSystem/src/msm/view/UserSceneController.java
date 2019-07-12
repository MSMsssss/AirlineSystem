package msm.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import msm.Main;
import msm.MySqlConnect;
import msm.model.OrderItem;
import msm.model.UserInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class UserSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	private UserInfo userInfo;
	private SimpleDateFormat sFormat;
	private ArrayList<String> cityList;
	private ObservableList<OrderItem> orderList = FXCollections.observableArrayList();
	private OrderItem selectedOrderItem = null;
	
	@FXML
	private Label userNameLabel;
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label passengerNameLabel;
	@FXML
	private Label passengerIdLabel;
	@FXML
	private Label paymentLabel;
	@FXML
	private Label leaveTimeLabel;
	@FXML
	private Label arriveTimeLabel;
	@FXML
	private Label returnLabel;
	@FXML
	private Label gottenLabel;
	
	@FXML
	private TextField planeIdField;
	@FXML
	private TextField seatIdField;
	@FXML
	private TextField leaveAirportField;
	@FXML
	private TextField arriveAirportField;
	
	@FXML
	private ComboBox<String> leaveCityBox;
	@FXML 
	private ComboBox<String> arriveCityBox;
	
	@FXML
	private CheckBox showGottenTicketBox;
	@FXML
	private CheckBox showReturnTicketBox;
	
	
	@FXML
	DatePicker leaveTimePicker;
	@FXML
	TableView<OrderItem> orderTableView;
	@FXML
	TableColumn<OrderItem, Integer> orderIdColumn;
	@FXML
	TableColumn<OrderItem, String> planeIdColumn;
	@FXML
	TableColumn<OrderItem, String> leaveTimeColumn;
	@FXML
	TableColumn<OrderItem, String> passengerNameColumn;
	
	@FXML
	private Button returnTicketButton;
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		sqlConnect = this.mainapp.getSqlConnect();
		
		userNameLabel.setText(userInfo.name);
		
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				Platform.runLater(()->{
					Date date = new Date();
					currentTimeLabel.setText("当前时间：" + sFormat.format(date));
				});
			}
		};
		timer.schedule(timerTask, 0, 1000);
		
		String commandString = "select distinct leave_city as city from flightinfo union "
				+ "select distinct arrive_city as city from flightinfo";
		ResultSet rSet = sqlConnect.executeQuery(commandString);
		try {
			while(rSet.next())
			{
				cityList.add(rSet.getString("city"));
			}
			
			rSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setUserInfo(UserInfo aUserInfo) {
		this.userInfo = aUserInfo;
	}
	
	private void showOrderDetails(OrderItem item) {
		if(item==null)
		{
			planeIdField.clear();
			seatIdField.clear();
			leaveAirportField.clear();
			arriveAirportField.clear();
			
			leaveTimeLabel.setText("");
			arriveTimeLabel.setText("");
			passengerNameLabel.setText("");
			passengerIdLabel.setText("");
			paymentLabel.setText("");
			returnLabel.setText("");
			gottenLabel.setText("");
			
			returnTicketButton.setDisable(true);
		}
		else {
			planeIdField.setText(item.planeIdProperty().get());
			seatIdField.setText(String.valueOf(item.seatNumProperty().get()));
			leaveAirportField.setText(item.leaveAirportProperty().get());
			arriveAirportField.setText(item.arriveAirportProperty().get());
			
			leaveTimeLabel.setText(item.leaveTimeProperty().get());
			arriveTimeLabel.setText(item.arriveTimeProperty().get());
			passengerNameLabel.setText(item.passengerNameProperty().get());
			passengerIdLabel.setText(item.passengerIdProperty().get());
			paymentLabel.setText(String.valueOf(item.paymentProperty().get()));
			returnLabel.setText(item.getReturntag() ? "已退票" : "");
			gottenLabel.setText(item.getGottenTag() ? "已取票" : "");
			
			if(item.getGottenTag() || item.getReturntag())
				returnTicketButton.setDisable(true);
			else 
				returnTicketButton.setDisable(false);
		}
	}
	
	@FXML
	private void initialize()
	{
		sFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss a");
		cityList = new ArrayList<String>();
		returnTicketButton.setDisable(true);
		
		orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
		planeIdColumn.setCellValueFactory(cellData -> cellData.getValue().planeIdProperty());
		leaveTimeColumn.setCellValueFactory(cellData -> cellData.getValue().leaveTimeProperty());
		passengerNameColumn.setCellValueFactory(cellData -> cellData.getValue().passengerNameProperty());
		
		orderTableView.setItems(orderList);
		
		orderTableView.getSelectionModel().selectedItemProperty().addListener(
				(Observable, oldValue, newValue) ->
				{
					showOrderDetails(newValue);
					selectedOrderItem = newValue;
				}
				);
	}
	
	@FXML
	private void clickExchange()
	{
		String leaveCityString = leaveCityBox.getValue();
		String arriveCityString = arriveCityBox.getValue();
		
		leaveCityBox.setValue(arriveCityString);
		arriveCityBox.setValue(leaveCityString);
	}
	
	@FXML
	private void searchTickets()
	{
		String leaveCityString = leaveCityBox.getValue();
		String arriveCityString = arriveCityBox.getValue();
		LocalDate leaveDate = leaveTimePicker.getValue();
		
		if(leaveCityString == null || arriveCityString == null || leaveDate == null)
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("检索失败");
			alert.setHeaderText(null);
			alert.setContentText("请填写出发城市，目的城市，以及出发时间！");

			alert.showAndWait();
		}
		else {
			mainapp.showBookingScene(userInfo, leaveDate, leaveCityString, arriveCityString);
		}
	}
	
	@FXML
	private void onShowingCity(Event event)
	{
		ComboBox<String> comboBox = (ComboBox<String>)event.getSource();
		comboBox.getItems().clear();
		
		String cityString = comboBox.getValue();
		if(cityString!=null)
		{
			for(String s:cityList)
			{
				if(s.contains(cityString))
					comboBox.getItems().add(s);
			}
		}
		else {
			for(String s:cityList)
			{
				comboBox.getItems().add(s);
			}
		}
	}
	
	@FXML
	private void SearchOrder() {
		orderList.clear();
		
		ArrayList<Integer> gottenOrderIdList = new ArrayList<Integer>();
		ArrayList<Integer> returnOrderIdList = new ArrayList<Integer>();
		
		String commandString = "select billinfo.order_id from billinfo, ticketinginfo "
				+ "where billinfo.order_id = ticketinginfo.order_id and user_name = ?";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userInfo.name);
			ResultSet rSet = preparedStatement.executeQuery();
			
			while(rSet.next())
			{
				gottenOrderIdList.add(rSet.getInt("billinfo.order_id"));
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		commandString = "select bookinginfo.order_id from bookinginfo, billinfo "
				+ "where bookinginfo.order_id = billinfo.order_id and user_name = ? and "
				+ "returntag = 1";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userInfo.name);
			ResultSet rSet = preparedStatement.executeQuery();
			
			while(rSet.next())
			{
				returnOrderIdList.add(rSet.getInt("bookinginfo.order_id"));
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		commandString = "select * from bookinginfo, billinfo, flightinfo, peopleinfo "
				+ "where bookinginfo.order_id = billinfo.order_id and user_name = ? and "
				+ "bookinginfo.passenger_id = peopleinfo.id and flightinfo.leave_time = bookinginfo.leave_time and "
				+ "flightinfo.id = bookinginfo.plane_id";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userInfo.name);
			ResultSet rSet = preparedStatement.executeQuery();
			
			while(rSet.next())
			{
				java.sql.Timestamp timestamp = rSet.getTimestamp("flightinfo.leave_time");
				DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("yyyy年MM月dd日  HH:mm:ss a");
				
				Integer orderIdInteger = rSet.getInt("bookinginfo.order_id");
				LocalDateTime dateTime = rSet.getTimestamp("flightinfo.leave_time").toLocalDateTime();
				
				OrderItem newItem = new OrderItem(
						rSet.getString("bookinginfo.plane_id"),
						rSet.getString("flightinfo.leave_airport"),
						rSet.getString("flightinfo.arrive_airport"),
						rSet.getInt("bookinginfo.seat_id"),
						dateTime.format(dFormat),
						rSet.getString("peopleinfo.name"),
						rSet.getDouble("bookinginfo.pay"),
						rSet.getTimestamp("flightinfo.arrive_time").toLocalDateTime().format(dFormat),
						orderIdInteger,
						rSet.getString("bookinginfo.passenger_id")
						);
				
				newItem.setGottenTag(gottenOrderIdList.contains(newItem.orderIdProperty().get()));
				newItem.setReturnTag(returnOrderIdList.contains(newItem.orderIdProperty().get()));
				newItem.setLeaveDateTime(dateTime);
				
				boolean unshowtag = ((!showGottenTicketBox.isSelected())&&newItem.getGottenTag()) ||
						((!showReturnTicketBox.isSelected())&&newItem.getReturntag());
				
				if(!unshowtag)
					orderList.add(newItem);
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void returnTicket() 
	{
		String commandString = "update bookinginfo set returntag = 1 where order_id = ?";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setInt(1, selectedOrderItem.orderIdProperty().get());
			
			if(preparedStatement.executeUpdate() > 0)
			{
				preparedStatement.close();
				commandString = "update seatinfo set sold = 0 "
						+ "where id = ? and leave_time = ? and seat_id = ?";
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setString(1, selectedOrderItem.planeIdProperty().get());
				preparedStatement.setTimestamp(2, 
						java.sql.Timestamp.valueOf(selectedOrderItem.getLeaveDateTime()));
				preparedStatement.setInt(3, selectedOrderItem.seatNumProperty().get());
				
				if(preparedStatement.executeUpdate()>0)
				{
					preparedStatement.close();
					commandString = "update userinfo set balance = balance + ? where name = ?";
					preparedStatement = sqlConnect.connect.prepareStatement(commandString);
					preparedStatement.setDouble(1, selectedOrderItem.paymentProperty().get());
					preparedStatement.setString(2, userInfo.name);
					if(preparedStatement.executeUpdate()>0)
					{
						preparedStatement.close();
						SearchOrder();
						Main.popup("提示信息", "退票成功，退款金额已到帐！", AlertType.INFORMATION);
					}
					else {
						preparedStatement.close();
						Main.popup("提示信息", "服务器出错！", AlertType.ERROR);
					}
				}
				else {
					preparedStatement.close();
					Main.popup("提示信息", "服务器出错！", AlertType.ERROR);
				}
			}
			else {
				preparedStatement.close();
				Main.popup("提示信息", "服务器出错！", AlertType.ERROR);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private void clickBack()
	{
		mainapp.showLoginScene();
	}
}
