package model;

import java.util.ArrayList;

/**
 * Classe che gestisce il modello della Mano del Player
 * @author gianmarcomottola
 *
 */
public class Mano{
	
	private ArrayList<Carta> mano;

	/**
	 * creo un ArrayList di 10 Carte dal deck
	 * @param deck
	 */
	public Mano(Deck deck) {
		mano = new ArrayList<Carta>();
		for(int i=0; i<10; i++) {
			mano.add(deck.distribuisciCarta(deck.getMazzo()));
		}
	}
	
	/**
	 * Costruttore con una sola carta per provare la vittora partita
	 * @param distribuisciCarta
	*/ 
	public Mano(Carta distribuisciCarta) {
		mano = new ArrayList<Carta>();
		mano.add(distribuisciCarta);
	}

	
	/**
	 * @return ArrayList con tutte le carte in mano del giocatore
	 */
	public ArrayList<Carta> getCarte(){
		return mano;
	}
	
	/**
	 * 
	 * @param indice
	 * @return la carta che si trova nell'indice passato
	 */
	public Carta getCarta(int indice) {
		return mano.get(indice);
	}
	
	/**
	 * @param indice
	 * @param carta
	 * Setto la carta passata nell'indice passato 
	 */
	public void setCarta(int indice, Carta carta) {
		mano.set(indice, carta);
	}

}
