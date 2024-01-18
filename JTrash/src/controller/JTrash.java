package controller;
	
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Queue;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Carta;
import model.Deck;
import model.Player;
import view.PlayerView;
import view.ProfiloView;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Main del gioco, inizia caricando il profilo e gestisce il turno del bot
 * @author gianmarcomottola
 *
 */
public class JTrash extends Application {
	private ArrayList<Player> players;
	private int idxCurrentPlayer;
	//private PlayerControllerProve playerController;
	private PlayerView playerView;
	private ProfiloView profiloView;
	private Stage stagePlayer;
	
	/**
	 * Carica la scena del Profilo Utente
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			// carico la scena del profilo
			FXMLLoader fxmlLoaderProfile = new FXMLLoader(getClass().getResource("/view/profilo.fxml"));
			Parent profileRoot = fxmlLoaderProfile.load();
			ProfiloView profiloView = fxmlLoaderProfile.getController();
			setProfiloView(profiloView);
			Scene sceneProfilo = new Scene(profileRoot, 380, 500);
			
			profiloView.getProfiloController().setMainController(this);
			
			// Ottieni le dimensioni dello schermo
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			
			 // Imposta la dimensione della finestra in base alle dimensioni dello schermo
			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setWidth(bounds.getWidth());
			primaryStage.setHeight(bounds.getHeight());
			
			primaryStage.setScene(sceneProfilo);
			primaryStage.setTitle("JTrash - Profile");
					
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Lancia l'applicazione
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Operazioni iniziali di inizio gioco, richiamato da profiloController quando inizio il game. 
	 * @param PlayerView
	 * @param Stage
	 */
	public void startGame(PlayerView playerView, Stage stagePartita) {
		stagePartita.setTitle("JTrash - Game");
		setPlayerView(playerView);	
		stagePlayer=stagePartita;
		
		PlayerController pc = playerView.getPlayerController();
		// se non è il primo turno pulisco le mani dei giocatori e inizio un nuovo match
		if(!pc.isPrimoTurno()) {
			pc.svuotaEPopolaMani();
			// pulisco tutti i grid
			playerView.getGridCartaPescata().getChildren().clear();
			playerView.getGridDeck().getChildren().clear();
			for(GridPane grid:playerView.getPanesPlayers()) {
				grid.getChildren().clear();
			}
			playerView.setMatch();
		} else {
			idxCurrentPlayer=0;			
			pc.setMainController(this);
		}
		Deck deck = pc.getDeck();
 		players = pc.getPlayers();
		playNextTurn(pc, deck);
	}

	/**
	 * Operazioni iniziali di Inizio Round, 
	 *  prendo il player che deve giocare,
	 *  pesco una carta (aggiornando il front-end),
	 *  controllo se è un bot e automatizzo
	 * @param pc
	 * @param deck
	 */
	public void playNextTurn(PlayerController pc, Deck deck) {
	
			
		System.out.println("indice Player in azione ->"+idxCurrentPlayer);
		
		Player currentPlayer = players.get(idxCurrentPlayer);
			
		// prendo il player corrente
		currentPlayer=pc.getPlayer(idxCurrentPlayer);
			
		// setta cartaPescata e aggiorna il front end
		Carta cartaPescataDalDeck = pescaEAggiornaCarta(pc);
			
		if(currentPlayer.isBot()) {
			nextBotMove(pc, currentPlayer, cartaPescataDalDeck);
		} 
	}
	

	/**
	 * Automatisto del Bot
	 * Controllo se la carta pescata sia in una posizione valida e la aggiugno alle carteDaScambiare, se la trovo continuo con le mosse del bot
	 * Una volta terminato aggiorno il front end scorrendo le cartaDaScambiare, pesco un'altra carta e controllo se il bot ha vinto il round
	 * @param PlayerController
	 * @param Player
	 * @param Carta
	 */
	public void nextBotMove(PlayerController pc, Player currentPlayer, Carta cartaPescataDalDeck) {
		System.out.println("indice BOT in azione ->"+idxCurrentPlayer);
		boolean continua=false; 
		final Player finalCurrentPlayer = players.get(idxCurrentPlayer);
		Queue<Carta> carteScambiate = new LinkedList<>();
		//pc.setBottoneTerminaTurnoPremuto(false);
		
		while(idxCurrentPlayer!=0) {		
			System.out.println("e' un Bot, carta -> "+cartaPescataDalDeck.toString());
			// itero sulla mano del bot fino a trovare una posizione valida
			if(pc.getCartaPescata().getValore().getValue()!=0) {				
				for(int i=0;i<currentPlayer.getSizeMano();i++) {
					if(pc.validateMove(currentPlayer.getMano().getCarta(i), i+1)) {
						System.out.println("Scambio effettuato, carta aggiunta -> "+pc.getCartaPescata().toString()+" in "+(i+1)+" posizione");
													
						// salvo la carta in mano coperta al posto della carta pescata del deck
						cartaPescataDalDeck=currentPlayer.getMano().getCarta(i);
						
						// setto la carta pescata dal mazzo
						currentPlayer.getMano().setCarta(i, pc.getCartaPescata());
						currentPlayer.getMano().getCarta(i).setMoved(true);
						System.out.println(currentPlayer.toString());
						
						// aggiungo la carta alle carte scambiate
						carteScambiate.add(pc.getCartaPescata());
						
						// setto cartaPescata di playerContoller
						pc.setImageCartaDaGiocare(cartaPescataDalDeck);
						
						continua = true;
						break;			
					}
				}
									
				// se ho trovato una posizione buona non cambio player
				if (continua) {
					continua=false;
					continue;
				}
				
			}
			
			// AGGIORNO IL FRONT END
			Platform.runLater(() -> {
				GridPane grid = pc.getGridFromPlayer(finalCurrentPlayer);
				// Imposta un colore di sfondo evidenziato
				grid.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
				
				// scorro le carte scambiate
			    while (!carteScambiate.isEmpty()) {
			        Carta cartaDaAggiornare = carteScambiate.poll();
			        
			        // Esegui l'aggiornamento dell'interfaccia utente per questa carta
			        pc.updateUIAfterCardExchange(cartaDaAggiornare, finalCurrentPlayer);
			        //sleepThread();
			    }
			    grid.setStyle("-fx-background-color: transparent;");
			    grid.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

			});
																		
			System.out.println("TURNO BOT FINITO");
				
			// setta cartaPescata con una nuova carta e il front end si aggiorna da solo
			Carta carta = pescaCarta(pc);
			
			pc.checkHoVinto();					   
			
			// se ha giocato l'ultimo giocatore
			if(idxCurrentPlayer==players.size()-1) {
				// sta al giocatore umano
				idxCurrentPlayer=0;
			} else {
				// sta al prossimo bot
				idxCurrentPlayer++;
				nextBotMove(pc, pc.getPlayer(idxCurrentPlayer), carta);
			}  
		}
	}
	
	/**
	 * Pesca una carta dal mazzo
	 * Setta cartaPescata
	 * Aggiorna il front end
	 * ritorna la carta 
	 * @param PlayerController
	 * @return Carta
	 */
	public Carta pescaEAggiornaCarta(PlayerController pc) {
		Carta cartaPescataDalDeck = pescaCarta(pc);
				
		// Effettua l'aggiornamento dell'interfaccia utente per mostrare la carta pescata
	    pc.updateUIAfterCardDraw();
	    
	    return cartaPescataDalDeck;
	}
	

	/**
	 * Pesca una carta dal mazzo
	 * Setta cartaPescata
	 * non aggiorna il front end 
	 * (andrebbe in conflitto con updateUIAfterCardDraw in pescaEAggiornaCarta perche richiamato alla fine del turno del bot e il front end si aggiorna dopo il back) 
	 * @param PlayerController
	 * @return Carta
	 */
	public Carta pescaCarta(PlayerController pc) {
		Carta cartaPescataDalDeck = pc.getDeck().distribuisciCarta(pc.getDeck().getMazzo());			
		
		if(cartaPescataDalDeck==null) {
			if(pc.getDeck().isHaSecondoMazzo()) {
				cartaPescataDalDeck = pc.getDeck().distribuisciCarta(pc.getDeck().getSecondoMazzo());
			}else {
				pc.getDeck().rimescolaMazzoFinito();
				cartaPescataDalDeck = pc.getDeck().distribuisciCarta(pc.getDeck().getMazzo());				
			}
		}
		
		// setto cartaPescata di playerContoller
		pc.setImageCartaDaGiocare(cartaPescataDalDeck);
		
		return cartaPescataDalDeck;
	}
	
	/**
	 * Setta la view (front-end) al main
	 * @param PlayerView
	 */
	public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }
	
	/**
	 * 
	 * @return PlayerView
	 */
	public PlayerView getPlayerView() {
		return playerView;
	}
	
	/**
	 * Usato per cambiare Player 
	 * @param idx
	 */
	public void setIdxPlayer(int idx) {
		System.out.println("idxPlayer ->"+idx);
		idxCurrentPlayer=idx;
	}
	
	/**
	 * Ritorna l'indice del giocatore, 0 è il giocare reale
	 * @return int
	 */
	public int getIdxPlayer() {
		return idxCurrentPlayer;
	}
	
	/**
	 * Mando a dormire il thread per 1 secondo
	 */
	public void sleepThread() {
		try {
			Thread.sleep(1000); // mando a dormire il thread per un secondo
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return ProfiloView
	 */
	public ProfiloView getProfiloView() {
		return profiloView;
	}

	/**
	 * 
	 * @param profiloView
	 */
	public void setProfiloView(ProfiloView profiloView) {
		this.profiloView = profiloView;
	}

	/**
	 * Uso lo stage della partita quando devo tornare a menu per chiuderlo
	 * (caricaMenu - playerController)
	 */
	public Stage getStagePlayer() {
		return stagePlayer;
	}
}
