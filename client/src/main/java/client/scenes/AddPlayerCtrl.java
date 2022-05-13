package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AddPlayerCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private int counter = 0;
    private final File nameFile = new File("src/main/resources/name.txt");

    @FXML
    private TextField nickname;

    @FXML
    private ImageView bulb;

    /**
     * Constructor for this class.
     * @param server the ServerUtils variable that represents the server
     * @param mainCtrl the main controller of the app
     */
    @Inject
    public AddPlayerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Called when "OK" button on login screen is pressed.
     * Adds player to the database with the provided nickname.
     */
    public void ok() {
        try {
            server.addPlayer(getNick());
            mainCtrl.playerId = server.getId(mainCtrl.nickname);
            PrintWriter wr = new PrintWriter(nameFile);
            wr.write(nickname.getText());
            wr.close();
        }
        catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid username/This username has already been taken.\nPlease try again");
            alert.showAndWait();
            return;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mainCtrl.showHome();
    }

    /**
     * Getter for a player's nickname.
     * @return the nickname of a player
     */
    private Player getNick() {
        String nick = nickname.getText().stripTrailing();
        mainCtrl.nickname = nick;
        return new Player(nick);
    }

    /**
     * Initializes the scene.
     * @param location the URL of the scene
     * @param resources other resources used for the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(nameFile));
            nickname.setText(br.readLine());
            br.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("User file is not present. This will be created....");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when a key is pressed and evokes an appropriate response.
     * @param e the KeyEvent corresponding to the key that was pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            default:
                break;
        }
    }

    /**
     * Calls the stop function in the ServerUtils.
     * Stops the Executor so that the thread doesn't stay running forever.
     */
    public void stop() {
        server.stop();
    }
}
