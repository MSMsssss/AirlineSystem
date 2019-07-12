package msm.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import msm.Main;

public class BeginSceneController {
	private Main mainapp;
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
	}
	
	@FXML
	private void initialize()
	{
		
	}
	
	@FXML
	private void clickGetTicket()
	{
		mainapp.showGetTicketScene();
	}
	
	@FXML
	private void clickUserLogin()
	{
		mainapp.showLoginScene();
	}
	
	@FXML
	private void clickAdminLogin(Event event)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/AdminLoginScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		AdminLoginSceneController controller = loader.getController();
		controller.getController(this.mainapp);
		
		Scene scene = new Scene(pane);
		Stage stage = new Stage();
		stage.setScene(scene);
		Stage ownerStage = (Stage)((ImageView)event.getSource()).getScene().getWindow();
		stage.initOwner(ownerStage);
		stage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
