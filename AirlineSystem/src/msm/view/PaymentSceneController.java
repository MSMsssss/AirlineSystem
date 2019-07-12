package msm.view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.print.attribute.standard.PagesPerMinute;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import msm.Main;
import msm.MySqlConnect;
import msm.model.FlightInfo;
import msm.model.UserInfo;

public class PaymentSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	private UserInfo userInfo;
	private FlightInfo flightInfo;
	private String leaveCity;
	private String arriveCity;
	private final String ERROR = "error";
	private final String SUCCESS = "success";
	
	@FXML
	private Label userNameLabel;
	
	@FXML
	private TextField balanceField;
	@FXML
	private TextField passengerIdField;
	@FXML
	private TextField planeIdField;
	@FXML
	private TextField leaveTimeField;
	@FXML
	private TextField seatNumField;
	@FXML
	private TextField shouldPayField;
	@FXML
	private TextField phoneNumberField;
	@FXML
	private TextField passengerNameField;
	@FXML
	private TextField leaveAirportField;
	
	public MySqlConnect getSqlConnect()
	{
		return sqlConnect;
	}
	
	public UserInfo getUserInfo()
	{
		return userInfo;
	}
	
	public void flashBalanceField()
	{
		try {
			String commandString = "select balance from userinfo where name = ?;";
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userInfo.name);
			ResultSet rSet = preparedStatement.executeQuery();
			
			if(rSet.next())
			{
				Double balanceDouble = rSet.getDouble("balance");
				balanceField.setText(balanceDouble.toString());
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();// TODO: handle exception
		}
	}
	
	public void setUserInfo(UserInfo userInfo)
	{
		this.userInfo = userInfo;
	}
	
	public void setFlightInfo(FlightInfo flightInfo)
	{
		this.flightInfo = flightInfo;
	}
	
	public void setLeaveCity(String leaveCityString) {
		this.leaveCity = leaveCityString;
	}
	
	public void setArriveCity(String arriveCityString) {
		this.arriveCity = arriveCityString;
	}
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		this.sqlConnect = this.mainapp.getSqlConnect();
		
		userNameLabel.setText(userInfo.name);
		passengerIdField.setText(userInfo.people_id);
		flashBalanceField();
		
		planeIdField.setText(flightInfo.getId());
		leaveTimeField.setText(flightInfo.getLeaveTime());
		leaveAirportField.setText(flightInfo.leaveAirportProperty().get());
		seatNumField.setText(String.valueOf(flightInfo.getSeatNum()));
		shouldPayField.setText(String.valueOf(flightInfo.getPrice()));
		
		String commandString = "select name from peopleinfo where id = ?";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userInfo.people_id);
			ResultSet rSet = preparedStatement.executeQuery();
			
			if(rSet.next())
			{
				passengerNameField.setText(rSet.getString("name"));
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void initialize()
	{
		
	}
	
	@FXML
	private void clickBackButton()
	{
		mainapp.showBookingScene(userInfo, flightInfo.getLeaveDateTime().toLocalDate(), 
				leaveCity, arriveCity);
	}
	
	@FXML
	private void clickPayButton()
	{
		String passengerNameString = passengerNameField.getText();
		String passengerIdString = passengerIdField.getText();
		String phoneNumberString = phoneNumberField.getText();
		boolean passengerInfoLegal = false;
		
		String commandString = "select * from peopleinfo where id = ? and name = ?";
		
		try {
			PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
			preparedStatement.setString(1, passengerIdString);
			preparedStatement.setString(2, passengerNameString);
			ResultSet rSet = preparedStatement.executeQuery();
			
			if(rSet.next() && !phoneNumberString.isEmpty())
			{
				passengerInfoLegal = true;
			}
			else {
				passengerInfoLegal = false;
			}
			
			rSet.close();
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(passengerInfoLegal)
		{
			flashBalanceField();
			Double balanceDouble = Double.valueOf(balanceField.getText());
			if(balanceDouble >= flightInfo.getPrice())
			{
				commandString = "update seatinfo set sold = 1 where id = ? and "
						+ "leave_time = ? and seat_id = ?";
				
				try {
					PreparedStatement preparedStatement = 
							sqlConnect.connect.prepareStatement(commandString);
					preparedStatement.setString(1, flightInfo.getId());
					preparedStatement.setTimestamp(2, 
							java.sql.Timestamp.valueOf(flightInfo.getLeaveDateTime()));
					preparedStatement.setInt(3, flightInfo.getSeatNum());
					
					preparedStatement.executeUpdate();
					
					String msg1 = null;
					ResultSet rSet1 = sqlConnect.executeQuery("select @msg");
					if(rSet1.next())
					{
						msg1 = rSet1.getString("@msg");
					}
					
					rSet1.close();
					preparedStatement.close();
					
					if(msg1.equals(SUCCESS))
					{
						commandString = "insert into bookinginfo(plane_id, passenger_id, phone, "
								+ "leave_time, seat_id, returntag, order_time, pay) values"
								+ "(?, ?, ?, ?, ?, 0, (select now()), ?)";
						preparedStatement = sqlConnect.connect.prepareStatement(commandString);
						preparedStatement.setString(1, flightInfo.getId());
						preparedStatement.setString(2, passengerIdString);
						preparedStatement.setString(3, phoneNumberString);
						preparedStatement.setTimestamp(4, 
								java.sql.Timestamp.valueOf(flightInfo.getLeaveDateTime()));
						preparedStatement.setInt(5, flightInfo.getSeatNum());
						preparedStatement.setDouble(6, flightInfo.getPrice());
						
						if(preparedStatement.executeUpdate() > 0)
						{
							preparedStatement.close();
						
							ResultSet rSet = sqlConnect.executeQuery("select @neworder_id");
							int order_id = -1;
							if(rSet.next())
							{
								order_id = rSet.getInt("@neworder_id");
							}
							
							if(order_id!=-1)
							{
								commandString = "insert into billinfo(user_name, order_id) values(?, ?)";
								
								preparedStatement = sqlConnect.connect.prepareStatement(commandString);
								preparedStatement.setString(1, userInfo.name);
								preparedStatement.setInt(2, order_id);
								
								preparedStatement.executeUpdate();
								preparedStatement.close();
								
								commandString = "update userinfo set balance = balance - ? where name = ?";
								
								preparedStatement = sqlConnect.connect.prepareStatement(commandString);
								preparedStatement.setDouble(1, flightInfo.getPrice());
								preparedStatement.setString(2, userInfo.name);
								preparedStatement.executeUpdate();
								preparedStatement.close();
								flashBalanceField();
								
								String msg = "支付成功，订单编号：" + String.valueOf(order_id);
								Main.popup("提示信息", msg, AlertType.INFORMATION);
							}
							else {
								Main.popup("提示信息", "服务器错误", AlertType.ERROR);
							}
						}
						else {
							Main.popup("提示信息", "服务器错误", AlertType.ERROR);
						}
						
					}
					else {
						Main.popup("提示信息", "座位已被占用", AlertType.INFORMATION);
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else {
				Main.popup("提示信息", "账户余额不足，请充值！", AlertType.INFORMATION);
			}
			
		}
		else {
			Main.popup("提示信息", "请输入有效的身份证号码，姓名及联系方式！", AlertType.INFORMATION);
		}
		
	}
	
	@FXML
	private void clickRechargeButton(ActionEvent event)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/RechargeScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		RechargeSceneController controller = loader.getController();
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		Stage stage = new Stage();
		stage.setScene(scene);
		Stage ownerStage = (Stage)((Button)event.getSource()).getScene().getWindow();
		stage.initOwner(ownerStage);
		stage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
