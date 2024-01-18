package controller;



import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Carta;
import model.Deck;
import model.Player;
import view.PlayerView;
import view.ProfiloView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



/**
 * Classe che gestisce gli eventi che arrivano dal front end(view/PlayerView) e che aggiorna il back end(model/Player) 
 * @author gianmarcomottola
 *
 */
public class PlayerController{

	private Player playerReal;
	private Player playerBot1;
	private Player playerBot2;
	private Player playerBot3;
	private ArrayList<Player> players;
	
	private Deck deck;
	
	private Carta cartaPescata;
    
    private PlayerView playerView;
    
    private JTrash mainController;

	private boolean primoTurno=true;
	
	private AudioManager audioManager;
	
	private static PlayerController instance;
	
	public static PlayerController getInstance() {
		if (instance == null)
			instance = new PlayerController();
		return instance;
	}
	
	private PlayerController() {
		
	}
	
    /**
     * Setto la carta pescata e la aggiungo al deck scartato
     * @param Carta
     */
	public void setImageCartaDaGiocare(Carta cartaPescata) {
		cartaPescata.setCoperta(false);
		ImageView c = new ImageView(cartaPescata.getImage());
	    cartaPescata.setImageViewCarta(c);
		this.cartaPescata=cartaPescata;
		deck.aggiungiAScartato(cartaPescata);
	}
        
    /**
     *  Metodo che gestisce il click della carta in mano.
     *  Controlla se la carta Pescata è valida per quella posizione e le scambia
     *  @param MouseEvent 
     */
    public void handleCartaDaGiocare(MouseEvent e) {
    	Platform.runLater(()->{
    		// reperisco indice della carta cliccata
	    	ImageView cartaCliccataInMano = (ImageView)e.getSource();
	    	int indexCarta=0;
	    	int col = GridPane.getColumnIndex(cartaCliccataInMano);
	    	int row = GridPane.getRowIndex(cartaCliccataInMano);
	    	if(row>0) {
	    		int rowTemp=5; // se la carta si trova sulla seconda riga
	    		indexCarta = rowTemp+col;
	    	}else {		    		
	    		indexCarta = row+col;
	    	}
	    	
	    	
	    	Carta cartaInMano = playerReal.getMano().getCarta(indexCarta);
	    	// se la carta è nella posizione giusta le scambia
	    	if(validateMove(cartaInMano, indexCarta+1)) {
	   			System.out.println("carta In mano da scambiare dopo il click -> "+cartaPescata.toString());
	       		cartaPescata.getImageViewCarta().setFitWidth(60); 
	       		cartaPescata.getImageViewCarta().setFitHeight(80); 
	    		 
	       		// Rimuovi la carta scelta dal gridPlayerReal e aggiungi la carta pescata
	   	    	playerView.getGridPlayer().getChildren().remove(cartaCliccataInMano);
	   	    	playerView.getGridPlayer().add(cartaPescata.getImageViewCarta(), col, row);
	   	    	cartaPescata.setMoved(true);
	   	    	
	   	        // chiamo update di playerView (da dentro Player)
	   	        playerReal.notifyMove(cartaInMano);
				// setto la cartaPescata(cioe quella da giocare) come la carta appena scambiata
				cartaPescata = cartaInMano;
				audioManager.play("src/model/sound/taking-card.wav");
				return;	
	    	}
    	});
    }
    
    /** 
     * Se il valore della carta pescata è uguale all'indice in cui si trova 
     * o la carta pescata è un jolly torna vero
     * Se la carta in mano che vogliamo scambiare si è gia stata spostata, la mossa non è valida
     * @param Carta, indice
     * @return boolean
     */
	public boolean validateMove(Carta carta, int indice) {
		if (getCartaPescata().getValore().getValue() == indice || getCartaPescata().getValore().getValue() == 11) {
			if(carta.isMoved()) {				
				System.out.println("Si e MOSSA");
				return false;
			}
			System.out.println("MOSSA VALIDA, Carta Pescata -> "+getCartaPescata());
			return true;
		}
		return false;
	}
    
    /**
     * Metodo che effettua aggiornamento dell'interfaccia utente per mostrare
     * la carta pescata dal deck
     *
     */
    public void updateUIAfterCardDraw() {
    	Platform.runLater(() -> {
    		try {
    			getCartaPescata().getImageViewCarta().setFitWidth(80);;
    			getCartaPescata().getImageViewCarta().setFitHeight(100); 
    			playerView.getGridDeck().add(getCartaPescata().getImageViewCarta(), 0, 0);
    			System.out.println("Carta aggiunta a deck Scartato FRONT ENd -> "+getCartaPescata().toString());
    		}catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
    	});
    }
    
    /**
     * Metodo per aggiornare li FRONT end del bot mostrando a video
     * la/e carta/e scambiata/e nella mano
     * (Richiamato in JTrash-nextBotMove a fine turno del bot)
     * @param Carta, Player
     * @return front end bot aggiornato
     */
	public void updateUIAfterCardExchange(Carta cartaDaAggiornare, Player currentPlayer) {
	    	// reperisco il grid del bot
	    	GridPane grid = getGridFromPlayer(currentPlayer);
	    	grid.setStyle("-fx-background-color: yellow;");
	    	// reperisco indice, col e row dalla carta
			int indice = currentPlayer.getIndiceFromCarta(cartaDaAggiornare);
	    	int row=0;
	    	int col=0;
	    	// trova col e row della carta
	    	if(indice!=-1) {
	    		if(indice>4) {
	    			row=1;
	    			col=indice-5;
	    		} else {
	    			col=indice;
	    		}
	    	}
	    	try{
	    		// setto e inserisco immagine nella mano del player
	    		cartaDaAggiornare.setCoperta(false);
	    		ImageView imageView = new ImageView(cartaDaAggiornare.getImage());
	    		cartaDaAggiornare.setImageViewCarta(imageView);
	    		imageView.setFitWidth(50);
	    		imageView.setFitHeight(70);
	    		grid.add(imageView,col,row);
	    		playerView.muoviCarta(col, row, cartaDaAggiornare);
	    		//mainController.sleepThread();
			}catch(IllegalArgumentException e) {
				e.printStackTrace();
			}
	}
    
    
    /**
     *  AZIONE BOTTONE TERMINA TURNO
     */
    public void terminaTurno() {
    	System.out.println("TERMINA TURNO");
    	// aggiungi la carta in 0x0 nel deck scartato
    	getDeck().aggiungiAScartato(getCartaPescata());
    	// sta al primo bot dopo il giocatore reale
    	mainController.setIdxPlayer(1);
    	mainController.playNextTurn(this, getDeck());
    }
    
    /**
     * AZIONE BOTTONE HO VINTO 
     * Controllo se tutte le carte si sono mosse
     */
    public void checkHoVinto() {
    	Player player = getPlayer(mainController.getIdxPlayer());
        
    	for (Carta carta: player.getMano().getCarte()) {
     		// se la carta non è stata scambiata
    		if( !carta.isMoved() ) {
    			// se non è un bot faccio un alert di NON vittoria round
    			if(!player.isBot()) {
    				showAlertNonVittoria(player);
    			}
    			return;
    		}
    	}
    	
    	// se ho solo una carta in mano ho vinto la partita
    	if(player.getSizeMano()==1) {
    		decidiAlertVittoria(player);
    		return;
    	}
        
        // se arrivo qui il player ha vinto il round
        System.out.println("HAI VINTO IL ROUND");
        System.out.println(player.getMano().getCarte());
        // scritta a video del player vincente
        mainController.sleepThread();
        showAlertVittoriaRound(player);
        
    }
    
    /**
     * Se ha vinto il giocatore Reale aggiorno le partite vinte
     * Mostra a video l'alert vittoria Partita
     * @param Player
     */
    public void decidiAlertVittoria(Player player) {
    	if(!player.isBot()) {
    		int partiteVinte = mainController.getProfiloView().getProfiloController().getProfilo().getPartiteVinte();
    		mainController.getProfiloView().getProfiloController().getProfilo().setPartiteVinte(partiteVinte+1);
    	}
    	showDialogVittoriaPartita(player);
    }
    
    /**
     * Ricostruisce le mani dei giocatori
     */
    public void svuotaEPopolaMani() {
    	deck = new Deck();
    	// per ogni player
    	players.forEach(player -> {
    		// per ogni indice carta
    		for(int i=0; i<player.getSizeMano(); i++) {
    			// pesco dal mazzo
    			Carta carta = deck.distribuisciCarta(deck.getMazzo());
    			// setto la carta
    			player.getMano().setCarta(i, carta);
    			
    		}    		
    	});
    	
    }
    
	/**
	 * Ricominci la partita.
	 * Inizia chi ha vinto 
	 * @param Player
	 */
	public void haiVinto(Player player) {
		//mainController.setIdxPlayer(player.getIndice());
		mainController.setIdxPlayer(0);
		setPrimoTurno(false);
		mainController.startGame(getPlayerView(), mainController.getStagePlayer());
	}
	
	/**
	 * Mostro a video l'alert di vittoria del round
	 * @param String, Player
	 */
    public void showAlertVittoriaRound(Player player) {
    	
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.setTitle("VINCITORE");
	    alert.setHeaderText("Hai vinto il Round, GRANDE");
	    alert.setContentText(player.toString()+" ha vinto il round");
	    // quando chiudo l'alert
	    alert.setOnCloseRequest(event -> haiVinto(player));
	        
	    // rimuovi ultima carta
	    player.removeLastCard();
	        
	    alert.show();
    	
    }
    
    /**
	 * Mostro a video l'alert di non vittoria del round
	 * @param String, Player
	 */
    public void showAlertNonVittoria(Player player) {
    	Platform.runLater(() -> {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("NON HAI VINTO");
	        alert.setHeaderText("Cerchi di fare il furbo?");
	        alert.setContentText(player.toString()+" non hai vinto...");
	        alert.show();
    	});
    }

    /**
     * Mostro a video l'alert di vittoria partita con doppia scelta:
     * - ricominciare la partita
     * - tornare al menu principale 
     * @param String, Player
     */
    public void showDialogVittoriaPartita(Player player) {
    	Platform.runLater(() -> {	    		
	    	// Crea un nuovo dialog personalizzato
	        Dialog<Void> dialog = new Dialog<>();
	        dialog.setTitle("Vittoria Partita");
	        dialog.setHeaderText("Complimenti!");
	
	        // Aggiungi i pulsanti personalizzati
	        Button ricominciaButton = new Button("Ricomincia partita");
	        Button tornaAlMenuButton = new Button("Torna al menu principale");
	
	        // Aggiungi i pulsanti al dialog
	        dialog.getDialogPane().setContent(new HBox(ricominciaButton, tornaAlMenuButton));
	     
	        // Gestisci gli eventi dei pulsanti
	        ricominciaButton.setOnAction(e -> {
	            // Ristarto la partita
	        	mainController.getStagePlayer().close();
	            mainController.getProfiloView().getProfiloController().startGame(e, playerView.getNumberOfPlayers());
	            dialog.close();
	        });
	
	        tornaAlMenuButton.setOnAction(e -> {
	            // Il giocatore ha scelto di tornare al menu principale
	            caricaProfilo(e);
	            dialog.close();
	        });
	        
	     // Mostra il dialog
	        dialog.show();    
    	});
    }
    
    /**
     * Carica profilo.fxml e setta la scena
     * @param ActionEvent
     */
    public void caricaProfilo(ActionEvent event) {
    	caricaMenu(event);
    }
    
	/**
	 *  Azione bottone Torna A Menu
	 */
    public void tornaAMenu(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Menu");
		alert.setHeaderText("Stai per tornare a Menu");
		alert.setContentText("Sei sicuro di volerci tornare?");
		
		if(alert.showAndWait().get()== ButtonType.OK) {
			caricaMenu(event);
		}
    }
    
    /**
     * Chiudo la partita e richiamo lo start iniziale
     * @param ActionEvent
     */
    public void caricaMenu(ActionEvent event) {
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	mainController.getStagePlayer().close();
    	mainController.start(stage);  
    }
    
    /**
     * Controlla quali player stanno giocando e 
     * li aggiunge alla lista se fosse
     */
	public ArrayList<Player> getPlayers(){
		players = new ArrayList<Player>();
		players.add(playerReal);
		players.add(playerBot1); // almeno in due si gioca
		if(playerBot2!=null) {
			if(playerBot2.isPlaying()) {
				players.add(playerBot2);
			}			
		}
		if(playerBot3!=null) {
			if(playerBot3.isPlaying()) {
				players.add(playerBot3);
			}			
		}
		return players;
	}
	
	/**
	 * ritorna il player dato l'indice (0 playerReal)
	 * @param int 
	 * @return il giocatore con specifico indice 
	 */
	public Player getPlayer(int indice) {
		return players.get(indice);
	}
	
	/**
	 * Tramite il player trovo il grid(la mano) associata
	 * @param Player
	 * @return GridPane del player passato - tramite indice
	 */
	public GridPane getGridFromPlayer(Player player) {
		int indiceGrid = player.getIndice();
		if(indiceGrid==0) {
			return playerView.getGridPlayer();
		} else if(indiceGrid==1) {
			return playerView.getGridPlayerBot1();
		} else if(indiceGrid==2) {
			return playerView.getGridPlayerBot2();
		} else if(indiceGrid==3) {
			return playerView.getGridPlayerBot3();
		}
		return null;
	}
	
	/**
	 * 
	 * @param AudioManager
	 */
	public void setAudioManager(AudioManager a) {
		audioManager = a;
	}
	
	/**
	 * 
	 * @return PlayerView
	 */
	public PlayerView getPlayerView() {
		return playerView;
	}

	/**
	 * 
	 * @param playerView
	 */
	public void setPlayerView(PlayerView playerView) {
		this.playerView = playerView;
	}
	
	/**
	 * Ritorna il giocate Reale
	 * @return Player
	 */
	public Player getPlayerReal() {
		return playerReal;
	}
	
	/**
	 * Ritorna la carta da Scambiare
	 * @return Carta
	 */
	public Carta getCartaPescata() {
		return cartaPescata;
	}
	
	/**
	 * 
	 * @return Deck
	 */
	public Deck getDeck() {
		return deck;
	}
	
	/**
	 * Setta un giocatore
	 * @param Player
	 */
	public void setPlayerReal(Player p) {
		playerReal = p;
	}
	
	/**
	 * Setta un giocatore
	 * @param Player
	 */
	public void setPlayerBot1(Player p) {
		playerBot1 = p;
	}
	
	/**
	 * Setta un giocatore
	 * @param Player
	 */
	public void setPlayerBot2(Player p) {
		playerBot2 = p;
	}
	
	/**
	 * Setta un giocatore
	 * @param Player
	 */
	public void setPlayerBot3(Player p) {
		playerBot3 = p;
	}
	
	/**
	 * Setta il deck
	 * @param Deck
	 */
	public void setDeck(Deck d) {
		deck=d;
	}
	
	/**
	 * Setta Jtrash - main del gioco
	 * @param MainController
	 */
	public void setMainController(JTrash main) {
		mainController=main;
	}
	
	/**
	 * Ritorna il controller del main
	 * @return JTrash
	 */
	public JTrash getMainController() {
		return mainController;
	}

	/**
	 *  Setta la carta da giocare
	 * @param Carta
	 */
	public void setCartaPescata(Carta carta) {
		cartaPescata = carta;
	}

	/**
	 * è il primo turno?
	 * @return boolean
	 */
	public boolean isPrimoTurno() {
		return primoTurno;
	}

	/**
	 * Setta se è o meno il primo turno
	 * @param primoTurno
	 */
	public void setPrimoTurno(boolean primoTurno) {
		this.primoTurno = primoTurno;
	}
	
}

