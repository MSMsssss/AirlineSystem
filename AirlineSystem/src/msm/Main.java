package msm;
	
import java.time.LocalDate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import msm.view.AdminSceneController;
import msm.view.BeginSceneController;
import msm.view.BookingSceneController;
import msm.view.GetTicketSceneController;
import msm.view.LoginSceneController;
import msm.view.PaymentSceneController;
import msm.view.UserSceneController;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import msm.model.*;

public class Main extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private MySqlConnect sqlConnect;
    public static final String adminName = "admin";
    
    public Main()
    {
    	sqlConnect = new MySqlConnect();
    }
    
	public static void popup(String title, String msg, Alert.AlertType type)
	{		
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
	
	@Override
	public void start(Stage aprimaryStage) {
		try {
			this.primaryStage = aprimaryStage;
			this.primaryStage.setTitle("飞机票预定系统");
			
			showBeginScene();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	public void showBeginScene()
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/BeginScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		BeginSceneController controller = loader.getController();
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showGetTicketScene()
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/GetTicketScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		GetTicketSceneController controller = loader.getController();
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showLoginScene()
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/LoginScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		LoginSceneController controller = loader.getController();
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showUserScene(UserInfo userInfo)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/UserScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		UserSceneController controller = loader.getController();
		controller.setUserInfo(userInfo);
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showBookingScene(UserInfo userInfo, LocalDate leaveDate, 
			String leaveCityString, String arriveCityString)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/BookingScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		BookingSceneController controller = loader.getController();
		controller.setUserInfo(userInfo);
		controller.setLeaveDate(leaveDate);
		controller.setArriveCityString(arriveCityString);
		controller.setLeaveCityString(leaveCityString);
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showPaymentScene(UserInfo userInfo, FlightInfo flightInfo,
			String leaveCity, String arriveCity)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/PaymentScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		PaymentSceneController controller = loader.getController();
		controller.setArriveCity(arriveCity);
		controller.setLeaveCity(leaveCity);
		controller.setFlightInfo(flightInfo);
		controller.setUserInfo(userInfo);
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showAdminScene()
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/AdminScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		AdminSceneController controller = loader.getController();
		controller.getController(this);
		
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public MySqlConnect getSqlConnect()
	{
		return this.sqlConnect;
	}
}

