package view;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import controller.ProfiloController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Avatar;

/**
 * Classe che gestisce il file fxml omonimo ( front-end )
 * @author gianmarcomottola
 *
 */
public class ProfiloView implements Observer, Initializable{
	@FXML
	private TextField nickNameTextField;
	@FXML
	private Label nickNameLabel;
	@FXML
	private Button buttonConfirmName;
	@FXML
	private Button buttonChangeName;
	@FXML
	private ProgressBar progressBarLV;
	// il BigDecimal da all'utente il controllo completo per l'approssimazione
	private BigDecimal progress;
	@FXML
	private GridPane gridAvatars;
	@FXML
	private GridPane gridAvatar;
	@FXML
	private Button buttonChangeAvatar;
	@FXML
	private Button buttonConfirmAvatar; 
	@FXML
	private Button buttonStartGame;
	@FXML
	private Button button2Players;
	@FXML
	private Button button3Players;
	@FXML
	private Button button4Players;
	@FXML
	private Button buttonLogout;
	@FXML
	private Label labelHowManyPlayer;
	@FXML
	private Label partiteGiocate;
	@FXML
	private Label partiteVinte;
	@FXML
	private Label percentualeBarra;
	@FXML
	private Label LV;
	
	private ProfiloController profiloController;
	
	/**
	 * Scrivo i dati sul txt
	 */
	@Override
	public void update(Observable o, Object arg) {
		profiloController.scriviDati();
		System.out.println("Entrato in update profilo");
	}
	
	/**
	 * Inizializzo il profilo leggendo i dati dal txt e settandoli a video
	 * utilizzo un grid per la scelta degli avatar
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// rendo la barra verde
		progressBarLV.setStyle("-fx-accent: #3D9140");
		profiloController = ProfiloController.getInstance();
		profiloController.leggiDati();
		
		// setto il progresso tramite partite giocate e vinte
		setProgress();
		
		profiloController.setProfiloView(this);
		profiloController.getProfilo().addObserver(this);
		profiloController.getProfilo().setProfiloController(profiloController);
		
		// setto il grid degli Avatar
		int idx=0;
		for(int row=0; row<2; row++) {
			for(int col=0; col<3; col++) {
				Avatar avatar = new Avatar(profiloController.getAvatars().get(idx));
				//ImageView imageView = new ImageView(profiloController.getAvatars().get(idx).getImage());
				idx++;
				avatar.getImageView().setOnMouseClicked(event -> profiloController.handleClickAvatar(event, avatar));

				if(avatar.getPath().equals(profiloController.getProfilo().getAvatar().getPath())) {
					avatar.getImageView().setFitHeight(160);
					avatar.getImageView().setFitWidth(140);
					gridAvatar.add(avatar.getImageView(), 0, 0);
					profiloController.confirmAvatar();
					profiloController.setAvatarTemp(avatar);
				}else {
					avatar.getImageView().setFitHeight(60);
					avatar.getImageView().setFitWidth(60);
					gridAvatars.add(avatar.getImageView(), col, row);					
				}
			}
		}
		
		if(profiloController.getNickName()!=null) {
			String newNickName = profiloController.getNickName();
			profiloController.setNickNameFrontEnd(newNickName);
		}
		setPartiteVinte();
		setPartiteGiocate();
		

	}
	
	/**
	 * Setto la barra del livello grazie alle partite giocate e vinte
	 * Aumento la barra di 0.3 per ogni vinta e 0.1 di ogni giocata
	 */
	public void setProgress() {
		// Converti gli interi in BigDecimal
		BigDecimal partiteGiocate = new BigDecimal(Integer.toString(profiloController.getProfilo().getPartiteGiocate()));
		BigDecimal partiteVinte = new BigDecimal(Integer.toString(profiloController.getProfilo().getPartiteVinte()));
		// Calcola i punteggi senza la divisione per 100 ( per ogni vinta + 0.3 , per ogni giocata + 0.1 )
		BigDecimal punteggioGiocate = partiteGiocate.multiply(new BigDecimal("0.1"));
		BigDecimal punteggioVinte = partiteVinte.multiply(new BigDecimal("0.3"));
		// Somma i punteggi
		progress = punteggioGiocate.add(punteggioVinte);
		// Dividi il risultato per 10 perche se progress = 350 con la divisione per 100 veniva 0.350, per 10 -> 3.50 ( cioe quello che mi serve)
		progress = progress.divide(new BigDecimal("10")); 
		
		if(progress.doubleValue()<1) {
			// aggiungo il decimale nella barra
			progressBarLV.setProgress(progress.doubleValue());

		} else {
			// reperisco la parte decimale e la moltiplico per 100 -> 3.50 -> 0.50 * 100 = 50
			double decimal = (progress.remainder(BigDecimal.ONE).doubleValue() * 100 );
			// setto la percentuale dentro la barra
			percentualeBarra.setText(Integer.toString((int)decimal) + "%");
			// setto il livello come il primo int del progresso -> progress = 3.50 = lv = 3 + 50%
			LV.setText(Integer.toString(progress.intValue()));
			// aggiugno il decimale nella barra
			progressBarLV.setProgress(progress.remainder(BigDecimal.ONE).doubleValue());
		}
	}

	
	public ProfiloController getProfiloController() {
		return profiloController;
	}
	
	 
	/**
	 * Azione bottone conferma nickname
	 */
	public void confirmNickName() {
		buttonConfirmName.setOnAction(event -> profiloController.confirmNickName());
	}
	
	/**
	 * Azione bottone cambia nickname
	 */
	public void changeNickName() {
		buttonChangeName.setOnAction(event -> profiloController.changeNickName());
	}
	
	/**
	 * Azione bottone cambia avatar
	 */
	public void changeAvatar() {
		buttonChangeAvatar.setOnAction(event -> profiloController.changeAvatar());
	}
	
	/**
	 * Azione bottone conferma avatar
	 */
	public void confirmAvatar() {
		buttonConfirmAvatar.setOnAction(event -> profiloController.confirmAvatar());
	}
	
	/**
	 * Azione bottone start game
	 * Passa il numero di player scelto
	 */
	public void startGame() {
		button2Players.setOnAction(event -> profiloController.startGame(event, 2));
		button3Players.setOnAction(event -> profiloController.startGame(event, 3));
		button4Players.setOnAction(event -> profiloController.startGame(event, 4));
	}
	
	/**
	 * Azione bottone inizia il gioco (rende visibile i bottone di scelta player)
	 */
	public void choosePlayer() {
		buttonStartGame.setOnAction(event -> profiloController.choosePlayer());
	}
	
	/**
	 * Azione bottone logout
	 */
	public void logout() {
		buttonLogout.setOnAction(event -> profiloController.logout(event));
	}
	
	public Button getButtonLogout() {
		return buttonLogout;
	}
	
	public Button getButtonStartGame() {
		return buttonStartGame;
	}
	
	public Button getButton2Player() {
		return button2Players;
	}
	
	public Button getButton3Player() {
		return button3Players;
	}
	
	public Button getButton4Player() {
		return button4Players;
	}

	public Label getLabelHowManyPlayer() {
		return labelHowManyPlayer;
	}
	
	/**
	 * Setto il label prendendo il valore dal Profilo
	 */
	public void setPartiteGiocate() {
		partiteGiocate.setText(""+(profiloController.getProfilo().getPartiteGiocate()));
	}
	
	/**
	 * Setto il label prendendo il valore dal Profilo
	 */
	public void setPartiteVinte() {
		partiteVinte.setText(""+(profiloController.getProfilo().getPartiteVinte()));
	}
	
	public TextField getNickNameTextField() {
		return nickNameTextField;
	}
	
	public Label getNickNameLabel() {
		return nickNameLabel;
	}
	
	public void setNickNameLabel(String text) {
		nickNameLabel.setText(text);
	}
	
	public Button getButtonConfirmName() {
		return buttonConfirmName;
	}
	
	public Button getButtonChangeName() {
		return buttonChangeName;
	}
	
	public Button getButtonChangeAvatar() {
		return buttonChangeAvatar;
	}
	
	public Button getButtonConfirmAvatar() {
		return buttonConfirmAvatar;
	}
	
	public GridPane getGridAvatars() {
		return gridAvatars;
	}
	
	public GridPane getGridAvatar() {
		return gridAvatar;
	}
}
