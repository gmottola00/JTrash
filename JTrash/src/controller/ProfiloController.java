package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Avatar;
import model.Profilo;
import view.PlayerView;
import view.ProfiloView;

/**
 * Classe che gestisce gli eventi che arrivano dal front end(view/ProfiloView) e che aggiorna il back end(model/Profilo)
 * Costruisce gli avatar.
 * @author gianmarcomottola
 *
 */
public class ProfiloController {
	
	private Profilo profilo;
	private ProfiloView profiloView;
	private List<Avatar> avatars;
	private JTrash main;
	private Avatar avatarTemp;
	private static ProfiloController instance;
	
	/**
	 * Singleton
	 * @return l'unica instanza della classe
	 */
	public static ProfiloController getInstance() {
		if(instance==null)
			instance = new ProfiloController();
		return instance;
	}
	
	/**
	 * Inizializza variabili e setta gli avatar
	 */
	private ProfiloController() {
		profilo=new Profilo();
		avatars = new ArrayList<Avatar>();
		String pathAvatar= "/model/avatar/Avatar";
		// carico gli avatar
		for(int i=0; i<6; i++) {
			avatars.add(determinaPercorsoAvatar(pathAvatar+(i+1)+".png"));
		}
	}
	
	/**
	 * CHIAMATO CON BOTTONE START GAME
	 * inizia il gioco caricando player.fxml e settando la scena
	 */
	public void startGame(ActionEvent event, int howManyPlayers) {
		try {
			
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/player.fxml"));
			PlayerView playerView = new PlayerView(howManyPlayers); // Passa il valore del bottone

			fxmlLoader.setController(playerView); // Imposta il controller personalizzato
			Parent root = fxmlLoader.load();
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
			stage.setResizable(false);
			
			// Ottieni le dimensioni dello schermo
	        Screen screen = Screen.getPrimary();
	        Rectangle2D bounds = screen.getVisualBounds();
	        
	        // Imposta la dimensione della finestra in base alle dimensioni dello schermo
	        stage.setX(bounds.getMinX());
	        stage.setY(bounds.getMinY());
	        stage.setWidth(bounds.getWidth());
	        stage.setHeight(bounds.getHeight());
			
			Scene scene = new Scene(root);
			stage.setScene(scene);
			
			stage.show();
			
			main.startGame(playerView, stage);
			
			getProfilo().setPartiteGiocate(getProfilo().getPartiteGiocate()+1);
		
		} catch (IOException e) {	
			e.printStackTrace();
		}            
	}
	
	/**
	 * Azione bottone logout
	 * Esce dal gioco
	 */
	public void logout(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Logout");
		alert.setHeaderText("Stai per uscire dal Gioco");
		alert.setContentText("Sei sicuro di voler uscire?");
		
		if(alert.showAndWait().get()== ButtonType.OK) {
			Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			stage.close();			
		}
	}
	
	/**
	 * Rendo visibili i pulsanti per scegliere con quanti player giocare
	 */
	public void choosePlayer() {
		profiloView.getButtonStartGame().setVisible(false);
		profiloView.getButtonLogout().setVisible(false);
		profiloView.getButton2Player().setVisible(true);
		profiloView.getButton3Player().setVisible(true);
		profiloView.getButton4Player().setVisible(true);
		profiloView.getLabelHowManyPlayer().setVisible(true);
	}
	
	/**
	 * Scrive i dati del profilo in un txt
	 * Richiamato alla fine del game e in update profiloView
	 */
	public void scriviDati() {
		//Scrivo i dati del profilo in un file di testo
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("profilo.txt"))) {
			writer.write(getProfilo().getNickName() + "," + getProfilo().getPartiteVinte() + "," +
					     getProfilo().getPartiteGiocate()+ "," + getProfilo().getAvatar().getPath()
					     );
		    
		    // Scrivi altre informazioni del profilo
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	/**
	 * leggo il txt con i dati del profilo
	 */
	public void leggiDati() {
		// Esempio di lettura dei dati del profilo da un file di testo
		try (BufferedReader reader = new BufferedReader(new FileReader("profilo.txt"))) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	System.out.println(line);
		    	 // Analizzo le linee del file e setto il profilo
		    	String[] elementi = line.split(",");
		    	setNickName(elementi[0]);
		    	setPartiteVinte(elementi[1]);
		    	setPartiteGiocate(elementi[2]);
		    	getProfilo().setAvatar(determinaPercorsoAvatar(elementi[3]));
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	/**
	 * BOTTONE CONFERMA NICKNAME
	 * rende invisibile il textField(dove si scrive) 
	 * e lo trasforma in label(rendendolo visibile)
	 */
	public void confirmNickName() {
		System.out.println("Entrato in confirm nickanme");
		String newNickName = profiloView.getNickNameTextField().getText();
		if (!newNickName.equals("")) {
			setNickNameFrontEnd(newNickName);
		}
	}
	
	/**
	 * Setta il label con il nome inserito
	 * Rende visibile il label e invisibile il textField(dove si scrive)
	 * @param newNickName
	 */
	public void setNickNameFrontEnd(String newNickName) {
		// setto il label con il nome inserito nel field
		profiloView.setNickNameLabel(newNickName);
					
		// rendo visibile il label e invisibile il textField
		profiloView.getNickNameTextField().setVisible(false);
		profiloView.getNickNameLabel().setVisible(true);
				
		profiloView.getButtonConfirmName().setVisible(false);
		profiloView.getButtonChangeName().setVisible(true);
		// entra in update della view
		setNickName(newNickName);
	}
	
	/**
	 * Rende visibile il textField del nickName (dove si scrive)
	 */
	public void changeNickName() {
		System.out.println("Entrato in change nickname");
		// setto visible il text field
		profiloView.getNickNameLabel().setVisible(false);
		profiloView.getNickNameTextField().setVisible(true);
		
		profiloView.getButtonConfirmName().setVisible(true);
		profiloView.getButtonChangeName().setVisible(false);
	}
	
	/**
	 * Rende visibili tutti gli avatar selezionabili
	 */
	public void changeAvatar() {
		profiloView.getButtonChangeAvatar().setVisible(false);
		profiloView.getGridAvatars().setVisible(true);
		
		profiloView.getButtonConfirmAvatar().setVisible(true);
	}
	
	/**
	 * Rende invisibili tutti gli avatar e setto l'avatar selezionato
	 */
	public void confirmAvatar() {
		System.out.println("Entrato in conferma avatar");
		profiloView.getButtonChangeAvatar().setVisible(true);
		profiloView.getGridAvatars().setVisible(false);
		
		profiloView.getButtonConfirmAvatar().setVisible(false);
		
		if(getAvatarTemp()!=null)
			getProfilo().setAvatar(avatarTemp);
	}
	
	/**
	 * Gestisco il click del Avatar rendendolo visibile in formato grande
	 */
	public void handleClickAvatar(MouseEvent event, Avatar avatarCliccato) {
		System.out.println("Entrato in click avatar");
		int col = GridPane.getColumnIndex(avatarCliccato.getImageView());
		int row = GridPane.getRowIndex(avatarCliccato.getImageView());
		
		// imposto avatar in formato grande
		if(getAvatarTemp()!=null) {
			getAvatarTemp().getImageView().setFitHeight(60);
			getAvatarTemp().getImageView().setFitWidth(60);
			profiloView.getGridAvatars().add(getAvatarTemp().getImageView(), col, row);
		}
		
		avatarCliccato.getImageView().setFitHeight(160);
		avatarCliccato.getImageView().setFitWidth(140);
		profiloView.getGridAvatar().add(avatarCliccato.getImageView(), 0, 0);
		setAvatarTemp(avatarCliccato);
		
	}
	
	/**
	 * Setto gli Avatar passando il percorso
	 * @param pathAvatar
	 * @return Avatar
	 */
	public Avatar determinaPercorsoAvatar(String pathAvatar) {
		
		Avatar avatar=new Avatar();
		try {
			Image image =  new Image(getClass().getResourceAsStream(pathAvatar));
			avatar.setImage(image);
			avatar.setImageView(new ImageView(image));
			avatar.setPath(pathAvatar);
		} catch(NullPointerException e) {
			System.out.println(pathAvatar+" -> Non Trovato");
		}
		return avatar;
	}
	
	/**
	 * Setto il mainController
	 * @param main
	 */
	public void setMainController(JTrash main) {
		this.main=main;
	}
	
	/**
	 * @return String, il nickname del profilo
	 */
	public String getNickName() {
		return profilo.getNickName();
	}
	
	public void setNickName(String name) {
		profilo.setNickName(name);
	}
	
	/**
	 * Setto le partite Giocate, il metodo in Profilo aggiorna il front end
	 * @param String
	 */
	public void setPartiteGiocate(String p) {
		int i = Integer.parseInt(p);
		profilo.setPartiteGiocate(i);
	}
	
	/**
	 * Setto le partite Vinte, il metodo in Profilo aggiorna il front end
	 * @param String
	 */
	public void setPartiteVinte(String p) {
		int i = Integer.parseInt(p);
		profilo.setPartiteVinte(i);
	}
	
	/**
	 * Setta la view del profilo
	 * @param ProfiloView
	 */
	public void setProfiloView(ProfiloView p) {
		profiloView = p;
	}

	/**
	 * Ritorna la lista con tutti gli avatar 
	 * @return List<Avatar>
	 */
	public List<Avatar> getAvatars() {
		return avatars;
	}
	
	/**
	 * Ritorna il modello del profilo
	 * @return Profilo
	 */
	public Profilo getProfilo() {
		return profilo;
	}

	/**
	 * Ritorna l'avatar Temporaneo ( prima di settarlo nel database )
	 * @return
	 */
	public Avatar getAvatarTemp() {
		return avatarTemp;
	}

	/**
	 * Setta l'avatar Temporaneo ( prima di settarlo nel database)
	 * @param avatarTemp
	 */
	public void setAvatarTemp(Avatar avatarTemp) {
		this.avatarTemp = avatarTemp;
	}
	
}
