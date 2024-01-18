package model;

import java.util.Observable;

import controller.ProfiloController;
import javafx.scene.image.ImageView;

/**
 * Classe che rappresenta il modello del Profilo
 * @author gianmarcomottola
 *
 */
public class Profilo extends Observable{
	
	private ProfiloController profiloController;
	private String nickName;
	private Avatar avatar;
	private int partiteGiocate;
	private int partiteVinte;
	private int lv;
	
	/**
	 * Ritorna il nome dell'utente
	 * @return String
	 */
	public String getNickName() {
	   return nickName;
	}
	
	/**
	 * Setto il nickname chiamando Update della View
	 * @param nickName
	 */
	public void setNickName(String nickName) {
	   this.nickName = nickName;
	   setChanged();
	   notifyObservers("Nickname cambiato");
	}

	/**
	 * Setto avatar chiamando Update della View
	 * @param avatar
	 */
	public void setAvatar(Avatar avatar) {
	   this.avatar = avatar;
	   setChanged();
	   notifyObservers("Avatar cambiato");
	}
	
	/**
	 * Setto le partite vinte chiamando Update della View
	 * @param partiteVinte
	 */
	public void setPartiteVinte(int partiteVinte) {
		this.partiteVinte = partiteVinte;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Setto le partite giocate chiamando Update della View
	 * @param partiteGiocate
	 */
	public void setPartiteGiocate(int partiteGiocate) {
		this.partiteGiocate = partiteGiocate;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Ritorna l'avatar del profilo
	 * @return
	 */
	public Avatar getAvatar() {
		return avatar;
	}
	
	/**
	 * Setta il controller del profilo
	 * @param p
	 */
	public void setProfiloController(ProfiloController p) {
		profiloController=p;
	}
	
	/**
	 * Ritorna il controller dell'utente
	 * @return ProfiloController
	 */
	public ProfiloController getProfiloController() {
		return profiloController;
	}

	/**
	 * Ritorna le partite giocate dell'utente
	 * @return int
	 */
	public int getPartiteGiocate() {
		return partiteGiocate;
	}

	/**
	 * Ritorna le partie Vinte dell'utente
	 * @return int
	 */
	public int getPartiteVinte() {
		return partiteVinte;
	}

	/**
	 * Il livello dell'utente
	 * @return int
	 */
	public int getLv() {
		return lv;
	}

	/**
	 * Setta il livello
	 * @param int
	 */
	public void setLv(int lv) {
		this.lv = lv;
	}


	

}
