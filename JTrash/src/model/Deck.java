package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import model.Carta.Seme;
import model.Carta.Valore;

/**
 * Un Deck ha un mazzo , un secondo Mazzo per quando ci sono piu di 2 Player e un mazzoScartato 
 * per tutte le carte scartate
 * @author gianmarcomottola
 *
 */
public class Deck{
	
	private ArrayList<Carta> mazzo;
	private ArrayList<Carta> secondoMazzo;
	private ArrayList<Carta> mazzoScartato;
	private boolean haSecondoMazzo;
	
	/**
	 * Inizizializza i mazzi e ne crea uno, il principale
	 */
	public Deck() {
		mazzo = new ArrayList<Carta>();
		mazzoScartato = new ArrayList<Carta>();
		secondoMazzo = new ArrayList<Carta>();
		haSecondoMazzo = false;
		
		creaMazzo(mazzo);
	}
	
	
	/** Pesca una carta dal mazzo scelto e la rimouve
	 * @param mazzoScelto
	 * @return Carta
	 */
	public Carta distribuisciCarta(ArrayList<Carta> mazzoScelto) {
	    if (mazzoScelto.isEmpty()) {
	        return null;
	    }

	    Random random = new Random();
	    int randomIndex = random.nextInt(mazzoScelto.size());

	    return mazzoScelto.remove(randomIndex);
	}
	
	/**
	 * Se il metodo è stato chiamato creo il secondo Mazzo
	 * Da chiamare prima di dare tutte le carte
	 */
	public void giocaConDueMazzi() {
		haSecondoMazzo = true;
		creaMazzo(secondoMazzo);
	}
	
	/**
	 * Creo un mazzo scorrendo tra tutti i semi e i valori
	 * Setto anchel'immagine
	 * @param mazzoDaCreare
	 */
	public void creaMazzo(ArrayList<Carta> mazzoDaCreare) {
		for(Seme seme:Seme.values()) {
			for(Valore valore:Valore.values()) {
				Carta carta = new Carta(seme, valore);
				carta.determinaPercorsoImg(seme, valore);
				mazzoDaCreare.add(carta);
			}
		}
		shuffle(mazzoDaCreare);
	}
	
	/**
	 * Mischia il mazzo scelto
	 * @param mazzo
	 */
	public void shuffle(ArrayList<Carta> mazzo) {
		Collections.shuffle(mazzo);
	}
	
	/**
	 * Ritorna il mazzo principale
	 * @return ArrayList<Carta>
	 */
	public ArrayList<Carta> getMazzo(){
		return mazzo;
	}
	
	/**
	 * Aggiunge una carta al deck scartato
	 * @param c
	 */
	public void aggiungiAScartato(Carta c) {
		mazzoScartato.add(c);
	}
	
	/**
	 * Quando finisco le carte aggiungo le carte Scartate al deck originale
	 */
	public void rimescolaMazzoFinito() {
		mazzo=mazzoScartato;
		mazzoScartato=new ArrayList<Carta>();
	}

	/**
	 * Il secondo Mazzo
	 * @return ArrayList<Carta>
	 */
	public ArrayList<Carta> getSecondoMazzo() {
		return secondoMazzo;
	}

	/**
	 * Setta il secondo mazzo
	 * @param secondoMazzo
	 */
	public void setSecondoMazzo(ArrayList<Carta> secondoMazzo) {
		this.secondoMazzo = secondoMazzo;
	}

	/**
	 * Ha un secondo mazzo
	 * @return boolean
	 */
	public boolean isHaSecondoMazzo() {
		return haSecondoMazzo;
	}

	/**
	 * 
	 * @param haSecondoMazzo
	 */
	public void setHaSecondoMazzo(boolean haSecondoMazzo) {
		this.haSecondoMazzo = haSecondoMazzo;
	}
	
	
}
