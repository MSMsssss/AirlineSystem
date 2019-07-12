package msm.view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import msm.Main;
import msm.MySqlConnect;
import msm.model.OrderItem;

public class GetTicketSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	
	private ObservableList<OrderItem> orderList = FXCollections.observableArrayList();
	private OrderItem selectedItem = null;
	
	@FXML
	private TextField passengerIdField;
	
	@FXML
	private TableView<OrderItem> orderTableView;
	@FXML
	private TableColumn<OrderItem, String> planeIdColumn;
	@FXML
	private TableColumn<OrderItem, String> leaveAirportColumn;
	@FXML
	private TableColumn<OrderItem, String> arriveAirportColumn;
	@FXML
	private TableColumn<OrderItem, Integer> seatNumColumn;
	@FXML
	private TableColumn<OrderItem, String> leaveTimeColumn;
	@FXML
	private TableColumn<OrderItem, String> passengerNameColumn;
	@FXML
	private TableColumn<OrderItem, Integer> orderIdColumn;
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		sqlConnect = this.mainapp.getSqlConnect();
	}
	
	@FXML
	private void initialize()
	{
		planeIdColumn.setCellValueFactory(cellData -> cellData.getValue().planeIdProperty());
		leaveAirportColumn.setCellValueFactory(cellData -> cellData.getValue().leaveAirportProperty());
		arriveAirportColumn.setCellValueFactory(cellData -> cellData.getValue().arriveAirportProperty());
		seatNumColumn.setCellValueFactory(cellData -> cellData.getValue().seatNumProperty().asObject());
		leaveTimeColumn.setCellValueFactory(cellData -> cellData.getValue().leaveTimeProperty());
		passengerNameColumn.setCellValueFactory(cellData -> cellData.getValue().passengerNameProperty());
		orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty().asObject());
		
		orderTableView.setItems(orderList);
		
		orderTableView.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) ->
				{
					selectedItem = newValue;
				}
				);
	}
	
	@FXML
	void clickSearchButton()
	{
		orderList.clear();
		String passengerIdString = passengerIdField.getText();
		
		if(passengerIdString.isEmpty())
		{
			Main.popup("提示信息", "请输入身份证号码！", AlertType.INFORMATION);
		}
		else {
			String commandString = "select bookinginfo.order_id, plane_id, leave_airport, arrive_airport, "
					+ "flightinfo.leave_time, seat_id, name, flightinfo.arrive_time "
					+ "from bookinginfo, flightinfo, peopleinfo "
					+ "where bookinginfo.plane_id = flightinfo.id and "
					+ "bookinginfo.leave_time = flightinfo.leave_time and "
					+ "peopleinfo.id = passenger_id and passenger_id = ? and "
					+ "returntag=0 and not exists("
					+ "select * from ticketinginfo  where ticketinginfo.order_id = bookinginfo.order_id)";
			try {
				PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setString(1, passengerIdString);
				ResultSet rSet = preparedStatement.executeQuery();
				
				while(rSet.next())
				{
					java.sql.Timestamp timestamp = rSet.getTimestamp("flightinfo.leave_time");
					DateTimeFormatter dFormat = DateTimeFormatter.ofPattern("yyyy年MM月dd日  HH:mm:ss a");
					java.sql.Timestamp timestamp2 = rSet.getTimestamp("flightinfo.arrive_time");
					
					orderList.add(new OrderItem(
							rSet.getString("plane_id"),
							rSet.getString("leave_airport"),
							rSet.getString("arrive_airport"), 
							rSet.getInt("seat_id"), 
							timestamp.toLocalDateTime().format(dFormat), 
							rSet.getString("name"), 
							false,
							rSet.getInt("bookinginfo.order_id"),
							passengerIdString,
							timestamp2.toLocalDateTime().format(dFormat)
							));
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
	void clickGetButton(ActionEvent event)
	{
		if(selectedItem!=null)
		{
			String commandString = "insert into ticketinginfo(order_id, time) values(?, (select now()))";
			PreparedStatement preparedStatement;
			try {
				preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setInt(1, selectedItem.orderIdProperty().get());
				if(preparedStatement.executeUpdate()>0)
				{
					showTicket(event);
					orderList.remove(selectedItem);
				}
				else {
					Main.popup("提示信息", "取票失败", AlertType.INFORMATION);
				}
				
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			Main.popup("提示信息", "请选择机票", AlertType.INFORMATION);
		}
	}
	
	private void showTicket(ActionEvent event) 
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/TicketScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		TicketSceneController controller = loader.getController();
		controller.setOrderItem(selectedItem);
		controller.getController(this.mainapp);
		
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
	
	@FXML
	private void clickBackButton()
	{
		mainapp.showBeginScene();
	}
}
