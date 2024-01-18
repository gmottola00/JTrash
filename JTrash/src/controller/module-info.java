module JTrash {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.media;
	requires javafx.base;
	requires java.desktop;
	requires javafx.swing;
	
	opens view to javafx.graphics, javafx.fxml, javafx.controls, javafx.media, javafx.base;
	opens controller to javafx.graphics, javafx.fxml, javafx.controls, javafx.media, javafx.base;
}