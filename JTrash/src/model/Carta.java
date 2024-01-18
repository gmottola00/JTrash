package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Carte comparabili per valori. Hanno un image, un imageView, un seme e un valore.
 * @author gianmarcomottola
 *
 */
public class Carta implements Comparable<Carta>{
	private Seme seme;
	private Valore valore;
	private Image imageCarta;
	private ImageView imageViewCarta;
	
	private boolean moved;
	private boolean coperta;
	
	/**
	 * Costruisce l'oggetto Carta
	 * @param seme
	 * @param valore
	 */
	public Carta(Seme seme, Valore valore) {
		coperta = true;
		imageCarta = null;
		setMoved(false);
		this.seme = seme;
		this.valore = valore;
	}
	
	/**
	 * Semi delle carte da Poker
	 * @author gianmarcomottola
	 *
	 */
	public enum Seme {
	    CLUBS, DIAMONDS, HEARTS, SPADES
	}

	/**
	 * Ogni carta ha un suo valore in intero
	 * @author gianmarcomottola
	 *
	 */
	public enum Valore{
	    ACE(1), DUE(2), TRE(3), QUATTRO(4), CINQUE(5), SEI(6), SETTE(7), OTTO(8), NOVE(9), DIECI(10), JACK(0), QUEEN(0), KING(11);
	    
		final int value;
		private Valore(int value) {
			this.value=value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	/**
	 * Costuisce l'immagine della carta partendo dal seme e dal valore
	 * @param seme
	 * @param valore
	 */
	public void determinaPercorsoImg(Seme seme, Valore valore) {
		String nomeFile = valore.name().toLowerCase() + "_of_" + seme.name().toLowerCase()+".png";
		String path = "img/"+nomeFile;
		
		try {
			imageCarta = new Image(getClass().getResourceAsStream(path));
			
		} catch(NullPointerException e) {
			System.out.println(path+" -> Non Trovato");
		}
		
	}

	/**
	 * Metodo per comparare le carte tramite valore
	 * @return 0 se carte uguale, 1 se la prima carta > carta passata e -1 se prima carta < carta passata
	 */
	@Override
	public int compareTo(Carta o) {
		return Integer.compare(this.getValore().getValue(), o.getValore().getValue());
	}
	
	/**
	 * Compara seme e valore
	 * @param otherCarta
	 * @return true se le carte hanno seme e valori uguali
	 */
	public boolean isEqual(Carta otherCarta) {
	    return this.seme == otherCarta.seme && this.valore == otherCarta.valore;
	}
	
	/**
	 * Rappresentazione della carta in stringa
	 * @return il valore e il seme della carta in stringa
	 */
	public String toString() {
		return ""+this.getValore().getValue()+" di "+this.getSeme();
	}

	/**
	 * 
	 * @return imageView della carta
	 */
	public ImageView getImageViewCarta() {
		return imageViewCarta;
	}

	public void setImageViewCarta(ImageView imageViewCarta) {
		this.imageViewCarta = imageViewCarta;
	}

	/**
	 * Mi serve per far capire al bot se una carta che si è gia mossa non puo piu muoversi.
	 * (Per non andare in loop infinito quando il bot trova una carta valida)
	 * @return boolean
	 */
	public boolean isMoved() {
		return moved;
	}

	/**
	 * Setta se la carta si è spostata o no
	 * @param moved
	 */
	public void setMoved(boolean moved) {
		this.moved = moved;
	}
	
	/**
	 *  
	 * @return Seme della carta
	 */
	public Seme getSeme() {
		return seme;
	}
	
	/**
	 * 
	 * @return valore della carta
	 */
	public Valore getValore() {
		return valore;
	}

	/**
	 * Mi serve per capire se caricare l'immagine di una carta coperta o valida
	 * @return boolean
	 */
	public boolean isCoperta() {
		return coperta;
	}
	
	public void setCoperta(Boolean coperta) {
		this.coperta = coperta;
	}
	
	/**
	 * 
	 * @return Image della carta se non è coperta, se è coperta ritorna immagine della carta girata
	 */
	public Image getImage() {
		if(!coperta)
			return this.imageCarta;
		return new Image(getClass().getResourceAsStream("img/covederedDeck.png"));
	}
}
