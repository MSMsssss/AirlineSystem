package msm.view;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import msm.Main;
import msm.MySqlConnect;

public class RegisterSceneController {
	private Main mainapp;
	private MySqlConnect sqlConnect;
	
	@FXML
	private Label messageLabel;
	
	@FXML
	private TextField userNameField;
	@FXML
	private TextField peopleIdField;
	
	@FXML 
	private PasswordField passwordField;
	@FXML 
	private PasswordField passwordAgainField;
	
	
	public void getController(Main amainapp)
	{
		this.mainapp = amainapp;
		sqlConnect = this.mainapp.getSqlConnect();
	}
	
	@FXML
	private void initialize()
	{
		
	}
	
	@FXML
	private void clickRegister(ActionEvent event)
	{
		String userNameString = userNameField.getText();
		String pwdString = passwordField.getText();
		String pwdAgainString = passwordAgainField.getText();
		String peopleIdString = peopleIdField.getText();
		
		/*��Ϣ������*/
		if(userNameString.isEmpty() || pwdString.isEmpty())
		{
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ע����Ϣ");
			alert.setHeaderText(null);
			alert.setContentText("��������Ϣ��");

			alert.showAndWait();
		}
		else {
			
			if(pwdString.equals(pwdAgainString))
			{
				String commandString = "select * from userinfo where name = ?";
				try {
					PreparedStatement preparedStatement = 
							sqlConnect.connect.prepareStatement(commandString);
					
					preparedStatement.setString(1, userNameString);
					ResultSet rSet = preparedStatement.executeQuery();
					
					if(rSet.next())
					{
						rSet.close();
						preparedStatement.close();
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("ע����Ϣ");
						alert.setHeaderText(null);
						alert.setContentText("�û����Ѵ��ڣ�");

						alert.showAndWait();
					}
					else {
						rSet.close();
						preparedStatement.close();
						
						commandString = "insert into userinfo(name, pwd, user_id, balance) "
								+ "values(?, ?, ?, 0)";
						
						preparedStatement = sqlConnect.connect.prepareStatement(commandString);
						preparedStatement.setString(1, userNameString);
						preparedStatement.setString(2, pwdString);
						
						/*�������֤����*/
						if(peopleIdString.isEmpty())
						{
							preparedStatement.setNull(3, java.sql.Types.CHAR);
						}
						else {
							preparedStatement.setString(3, peopleIdString);
						}
						
						int rtn = preparedStatement.executeUpdate();
						if(rtn > 0)
						{
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("ע����Ϣ");
							alert.setHeaderText(null);
							alert.setContentText("ע��ɹ���");

							alert.showAndWait();
						}
						else {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("ע����Ϣ");
							alert.setHeaderText(null);
							alert.setContentText("ע��ʧ�ܣ�");

							alert.showAndWait();
						}
						
						preparedStatement.close();
						
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else /*�������벻һ��*/
			{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("ע����Ϣ");
				alert.setHeaderText(null);
				alert.setContentText("�������벻һ�£�");

				alert.showAndWait();
			}
		}
	}
	
	@FXML
	private void clickExit(ActionEvent event)
	{
		((Stage)((Button)event.getSource()).getScene().getWindow()).close();
	}
}
