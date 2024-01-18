package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Costruito con un Image, un ImageView e un path
 * @author gianmarcomottola
 *
 */
public class Avatar {
	private Image image;
	private ImageView imageView;
	private String path;

	/**
	 * Setto l'avatar da un Avatar gia esistente ( inizialize di ProfiloView )
	 * @param avatar
	 */
	public Avatar(Avatar avatar) {
		image=avatar.getImage();
		imageView=avatar.getImageView();
		path=avatar.getPath();
	}
	
	/**
	 * Costruttore vuoto ( determinaPercorsoAvatar di ProfiloController )
	 */
	public Avatar() {
		
	}

	/**
	 * Immagine dell'avatar
	 * @return Image
	 */
	public Image getImage() {
		return image;
	}
	/**
	 * Setta l'immagine dell'avatar
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Ritorna l'image View del'avatar
	 * @return ImageView
	 */
	public ImageView getImageView() {
		return imageView;
	}
	
	/**
	 * Setta l'imageView dell'avatar
	 * @param imageView
	 */
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
	/**
	 * Ritorna il percorso dell'immagine dell'avatar
	 * @return String
	 */
	public String getPath() {
		return path;
	}
	/**
	 * Setta il percorso
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
}
