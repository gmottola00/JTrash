package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;

import controller.PlayerController;

/**
 * La classe Player costruisce un giocatore con una Mano di 10 carte, se è un bot e se sta giocando ( se ha indice 0 è il giocatore Reale)
 * @Return Player
 * @author gianmarcomottola
 *
 */
public class Player extends Observable{
	private Mano mano;
	private final boolean isBot;
	private final boolean isPlaying;
	private final int indice;
	private PlayerController playerController; // usato in PlayerView nel metodo update
	
	/**
	 * 
	 * @param mano
	 * @param isBot
	 * @param isPlaying
	 * @param indice
	 */
	public Player(Mano mano, boolean isBot, boolean isPlaying, int indice) {
		this.mano=mano;
		this.isBot=isBot;
		this.isPlaying=isPlaying;
		this.indice=indice;
		playerController=null;
	}
	
	/**
	 * Imposta il playerController
	 */
	public void setPlayerController(PlayerController pc) {
		playerController=pc;
	}
	
	/**
	 * @return PlayerController
	 */
	public PlayerController getPlayerController() {
		return playerController;
	}
	
	/**
	 * @return Mano del player
	 */
	public Mano getMano() {
		return mano;
	}
	
	public void setMano(Mano m) {
		mano=m;
	}
	
	/**
	 * Il giocatore è un bot?
	 * @return boolean
	 */
	public boolean isBot() {
		return isBot;
	}
	
	/**
	 * Il giocatore sta giocando?
	 * @return boolean
	 */
	public boolean isPlaying() {
		return isPlaying;
	}
	
	/**
	 * Notifica il front-end con la carta in mano da Scambiare
	 * @param cartaDaScambiare
	 */
	public void notifyMove(Carta cartaDaScambiare) {
		setChanged();
		notifyObservers(cartaDaScambiare);
	}
	
	/**
	 * La dimensione della mano del giocatore
	 * @return int
	 */
	public int getSizeMano() {
		return mano.getCarte().size();
	}
	
	/**
	 * Rappresentazione del giocatore in Stringa (se è l'utente reperisce il nome)
	 * @return String
	 */
	public String toString() {
		if(isBot())
			return "Bot "+getIndice();
		else
			return playerController.getMainController().getProfiloView().getProfiloController().getProfilo().getNickName();
	}
	
	/**
	 * Rimuove l'ultima carta in mano al giocatore (per quando vince un round)
	 */
	public void removeLastCard() {
		mano.getCarte().remove(getSizeMano()-1);
	}

	/**
	 * L'indice del giocatore
	 * @return int
	 */
	public int getIndice() {
		return indice;
	}
	
	/**
	 * Se la carta si trova in mano al player ritorna l'indice, altrimenti torn -1
	 * @return int
	 */
	public int getIndiceFromCarta(Carta cartaDaTrovare) {
		int indice=0;
		for(Carta carta:getMano().getCarte()) {
			if(carta.equals(cartaDaTrovare)) 
				return indice;
			indice++;
		}
		return -1;
	}
}
