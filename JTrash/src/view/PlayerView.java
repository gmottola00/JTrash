package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import controller.AudioManager;
import controller.PlayerController;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.Carta;
import model.Deck;
import model.Mano;
import model.Player;

/**
 * Classe che gestisce il file fxml omonimo ( front-end )
 *  
 * @author gianmarcomottola
 *
 */
public class PlayerView implements Observer, Initializable{
	@FXML
	private AnchorPane myAnchor;
	@FXML
    private GridPane gridPlayerBot1;
	@FXML
    private GridPane  gridPlayerBot2;
	@FXML
    private GridPane  gridPlayerBot3;
	@FXML
    private GridPane  gridPlayerReal;
	@FXML
    private GridPane  gridDeck;
	@FXML
	private GridPane gridCartaPescata;
	@FXML
	private Button bottoneTerminaTurno;
	@FXML
	private Button bottoneHoVinto;
	@FXML
	private Button bottoneTornaAMenu;
	
	private PlayerController playerController;
	
	private List<GridPane> gridPanesPlayers;
	
	private int numberOfPlayers;

	private AudioManager audioManager;
	
	/**
	 * Costruisce l'oggetto con i player che giocano, passati in profiloController
	 * @param numberOfPlayers
	 */
	public PlayerView(int numberOfPlayers) {
	     this.setNumberOfPlayers(numberOfPlayers);
	     audioManager = AudioManager.getInstance();
	}
	
	/**
	 * Aggiorno il back-end
	 * itero su tutte le carte in mano del player selezionato
	 * se una carta corrisponde alla carta passata allora la setto nel back end.
	 * 
	 */
	@Override 
	public void update(Observable obs, Object msg) {
		if(msg==null) {
			return;
		}
		if(msg instanceof Carta) {
			Player player = ((Player)obs);
			// carta in mano al player da scambiare
			Carta carta = ((Carta)msg);
			
			// scorro la mano del player
			for(int i=0; i<player.getSizeMano(); i++) {
				// se trovo la carta da scambiare la scambio con quella pescata
				if (player.getMano().getCarta(i).isEqual(carta)) {
					System.out.println("Scambio effettuato, carta aggiunta -> "+playerController.getCartaPescata().toString()+" in "+(i+1)+" posizione");
					player.getMano().setCarta(i, playerController.getCartaPescata());
						    	
		   	    	// setto l'immagine da inserire nel grid 0x0
		   	    	carta.setCoperta(false);
		   	    	ImageView imageView = new ImageView(carta.getImage());
		   	    	carta.setImageViewCarta(imageView);
		   	    	carta.getImageViewCarta().setFitWidth(80); 
		   	    	carta.getImageViewCarta().setFitHeight(100);
		   	        getGridCartaPescata().add(carta.getImageViewCarta(), 0, 0);
					
					return;
				}
			}
			System.out.println(player.toString());
		}
	}


	/**
	 * Inizializzo la scena di inizio partita
	 * Controllo quanti player ci sono e setto il deck
	 * Setto i player e le loro mani
	 * aggiungo l'observer al playerReale 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		System.out.println("INIZIALIZZO");
			
		gridPanesPlayers = new ArrayList<GridPane>();
			
		// inizializzazione deck e giocatori (sicuramente si gioca in due)
		Deck deck = new Deck();
		if(numberOfPlayers>2) {
			deck.giocaConDueMazzi();
		}
	    audioManager.play("src/model/sound/shuffling-cards-02.wav");
		
		playerController = PlayerController.getInstance();
		
		playerController.setAudioManager(audioManager);
		//Player playerReal = new Player(new Mano(deck), false, true, 0);
		// per prove vittoria partita ( do solo una carta in mano player Vero ) 
		Mano mano = new Mano(deck.distribuisciCarta(deck.getMazzo()));
		Player playerReal = new Player(mano, false, true, 0);
		Player playerBot1 = new Player(new Mano(deck), true, true, 1);
		
		// setto il playerController
		playerController.setPlayerReal(playerReal);
		playerController.setPlayerBot1(playerBot1);
		
		gridPanesPlayers.add(gridPlayerReal); // posizione 0 playerReal
		gridPanesPlayers.add(gridPlayerBot1);
		
		// controllo se ci sono piu player
		if(numberOfPlayers>3) {
			Player playerBot2 = new Player(new Mano(deck), true, true, 2);
			Player playerBot3 = new Player(new Mano(deck), true, true, 3);
			playerController.setPlayerBot2(playerBot2);
			playerController.setPlayerBot3(playerBot3);
			gridPanesPlayers.add(gridPlayerBot2);
			gridPanesPlayers.add(gridPlayerBot3);
		} else if(numberOfPlayers==3) {
			Player playerBot2 = new Player(new Mano(deck), true, true, 2);
			playerController.setPlayerBot2(playerBot2);
			gridPanesPlayers.add(gridPlayerBot2);
		}
		
		playerController.setPlayerView(this);
			
		// aggiungo il controller come osservatore del model
		playerReal.addObserver(this);
		
		// imposto il controller nel modello
		playerReal.setPlayerController(playerController);			
		
		setMatch();
		
		playerController.setDeck(deck);
	}
	
	/**
	 * Itero sui gridPanes e setto il front end dei player uno alla volta
	 */
	public void setMatch() {
		// imposto carte coperte del deck
		impostaCarteCoperte();
			
		// imposto un giocatore alla volta
		for(int i=0; i<gridPanesPlayers.size(); i++) {
			
			gridPanesPlayers.get(i).setHgap(10);
			gridPanesPlayers.get(i).setVgap(10);
			Player currentPlayer = playerController.getPlayers().get(i);
			
			// itero sul grid pane del player Corrente 
			int idxCarta=0;
			for (int row = 0; row < 2; row++) {
	            for (int col = 0; col < 5; col++) {
	            	
	            	// controllo quante carte ha il player
	            	if(currentPlayer.getSizeMano()>idxCarta) {
	            		
		            	Carta currentCarta = currentPlayer.getMano().getCarta(idxCarta);
		            	idxCarta++;
		            	// setto image della carta e la aggiungo al giocatore
			            //cartaReal.setCoperta(false); 
			            ImageView imageView = new ImageView(currentCarta.getImage());
			            currentCarta.setImageViewCarta(imageView);
			            
			            if(currentPlayer.isBot()) {
			            	currentCarta.getImageViewCarta().setFitWidth(50); 
			            	currentCarta.getImageViewCarta().setFitHeight(70); 			            	
			            } else {
			            	currentCarta.getImageViewCarta().setFitWidth(60); 
			            	currentCarta.getImageViewCarta().setFitHeight(80); 			            	
			            }
			            gridPanesPlayers.get(i).add(currentCarta.getImageViewCarta(), col, row);
			            
			            currentCarta.getImageViewCarta().setOnMouseClicked(event -> playerController.handleCartaDaGiocare(event));
			    
			            muoviCarta(col, row, currentCarta);
			            
	            	} else {
	            		System.out.println("Carta non inserita");
	            		break;
	            	}
		            
				}	
			}
		}
	
	}
	
	/**
	 * Animazione delle carte
	 * @param col
	 * @param row
	 * @param carta
	 */
	public void muoviCarta(int col, int row, Carta carta) {
		TranslateTransition transition = new TranslateTransition(Duration.seconds(1), carta.getImageViewCarta());
		transition.setToX(col);
		transition.setToY(row);
		transition.play();
	}

	/**
	 * Imposto le carte coperte del deck
	 */
	public void impostaCarteCoperte() {
		Carta cartaCoperta = new Carta(null, null);
		ImageView c = new ImageView(cartaCoperta.getImage());
		c.setFitWidth(80); 
		c.setFitHeight(100);
		gridDeck.add(c, 1, 0);
		
		
		Carta cartaCoperta2 = new Carta(null, null);
		ImageView c2 = new ImageView(cartaCoperta2.getImage());
		c2.setFitWidth(80); 
		c2.setFitHeight(100);
		gridDeck.add(c2, 0, 0);
		
	}
	
	/**
	 * Azione bottone termina turno
	 */
	public void terminaTurno() { 
		bottoneTerminaTurno.setOnAction(event -> playerController.terminaTurno());	
	}
	
	/**
	 * Azione bottone ho vinto
	 */
	public void hoVinto() {
		bottoneHoVinto.setOnAction(event -> playerController.checkHoVinto());
	}
	
	/**
	 * Azione bottone torna a menu
	 */
	public void tornaAMenu() {
		bottoneTornaAMenu.setOnAction(event -> playerController.tornaAMenu(event));
	}
	
	public void sleepThread() {
		try {
			Thread.sleep(1000); // mando a dormire il thread per un secondo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return GridPane del deck
	 */
	public GridPane getGridDeck() {
		return gridDeck;
	}
	
	public GridPane getGridCartaPescata() {
		return gridCartaPescata;
	}
	
	public PlayerController getPlayerController() {
		return playerController;
	}

	public void setPlayerController(PlayerController playerController) {
		this.playerController = playerController;
	}
	
	public GridPane getGridPlayer() {
		return gridPlayerReal;
	}
	
	public GridPane getGridPlayerBot1() {
		return gridPlayerBot1;
	}
	
	public GridPane getGridPlayerBot2() {
		return gridPlayerBot2;
	}
	
	public GridPane getGridPlayerBot3() {
		return gridPlayerBot3;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}	
	
	public List<GridPane> getPanesPlayers(){
		return gridPanesPlayers;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}
}

