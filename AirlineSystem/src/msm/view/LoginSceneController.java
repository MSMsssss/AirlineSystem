package msm.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import msm.Main;
import msm.MySqlConnect;
import msm.model.UserInfo;

import java.sql.*;

public class LoginSceneController {
	private Main mainapp;
	private MySqlConnect sqlCon;
	
	@FXML
	private Label loginMessage;
	@FXML
	private TextField userEdit;
	@FXML
	private PasswordField passwordEdit;
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		this.sqlCon = this.mainapp.getSqlConnect();
	}
	
	@FXML
	private void initialize()
	{
		
	}
	
	@FXML
	private void clickLogin()
	{
		String userNameString = userEdit.getText();
		String pwdString = passwordEdit.getText();
		
		String commandString = "select * from userinfo where name = ? and pwd = ?";
		try {
			PreparedStatement preparedStatement = sqlCon.connect.prepareStatement(commandString);
			preparedStatement.setString(1, userNameString);
			preparedStatement.setString(2, pwdString);
			ResultSet rSet = preparedStatement.executeQuery();
			
			if(rSet.next())
			{
				UserInfo userInfo = new UserInfo();
				userInfo.name = userNameString;
				userInfo.password = pwdString;
				userInfo.people_id = rSet.getString("user_id");
				userInfo.balance = rSet.getDouble("balance");
				
				mainapp.showUserScene(userInfo);
			}
			else {
				loginMessage.setText("用户名或密码错误");
			}
			
			rSet.close();
			preparedStatement.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML
	private void clickBackButton() {
		mainapp.showBeginScene();
	}
	
	@FXML
	private void clickRegister(ActionEvent event)
	{
		try {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/RegisterScene.fxml"));
		AnchorPane pane = (AnchorPane)loader.load();
		
		RegisterSceneController controller = loader.getController();
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
		
}
