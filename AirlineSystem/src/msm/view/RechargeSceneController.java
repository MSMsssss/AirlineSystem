package msm.view;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import msm.Main;
import msm.MySqlConnect;
import msm.model.UserInfo;

public class RechargeSceneController {
	private PaymentSceneController payApp;
	private MySqlConnect sqlConnect;
	
	@FXML
	private ComboBox<Double> rechargeValueBox;
	
	public void getController(PaymentSceneController amainapp)
	{
		this.payApp = amainapp;
		sqlConnect = this.payApp.getSqlConnect();
	}
	
	@FXML
	private void initialize()
	{
		rechargeValueBox.getItems().addAll(100.0, 200.0, 500.0, 1000.0, 2000.0);
	}
	
	@FXML
	private void clickBackButton(ActionEvent event)
	{
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
	
	@FXML
	private void clickSureButton()
	{
		Double rechargeValueDouble = rechargeValueBox.getValue();
		if(rechargeValueDouble!=null)
		{
			String commandString = "update userinfo set balance = balance + ? where name = ?";
			
			try {
				PreparedStatement preparedStatement = sqlConnect.connect.prepareStatement(commandString);
				preparedStatement.setDouble(1, rechargeValueDouble);
				preparedStatement.setString(2, payApp.getUserInfo().name);
				
				if(preparedStatement.executeUpdate() > 0)
				{
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("充值信息");
					alert.setHeaderText(null);
					alert.setContentText("充值成功！");

					alert.showAndWait();
				}
				else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("充值信息");
					alert.setHeaderText(null);
					alert.setContentText("充值失败！");

					alert.showAndWait();
				}
				
				payApp.flashBalanceField();
				
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("充值信息");
			alert.setHeaderText(null);
			alert.setContentText("请选择金额！");

			alert.showAndWait();
		}
	}
}
