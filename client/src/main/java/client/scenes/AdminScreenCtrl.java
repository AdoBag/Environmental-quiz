package client.scenes;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import client.utils.ServerUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class AdminScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private ObservableList<Activity> data;

    @FXML
    private Button addActivity;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView background;

    @FXML
    private Button chooseFile;

    @FXML
    private Button commitEdit;

    @FXML
    private TableColumn<Activity, String> consumption;

    @FXML
    private Button deleteActivity;

    @FXML
    private Button editActivity;

    @FXML
    private TextField filePath;

    @FXML
    private TableColumn<Activity, String> id;

    @FXML
    private TableColumn<Activity, String> imagePath;

    @FXML
    private Button importFile;

    @FXML
    private ImageView nukeSymbol;

    @FXML
    private Button quitButton;

    @FXML
    private TableColumn<Activity, String> source;

    @FXML
    private TableView<Activity> table;

    @FXML
    private TextField textConsumption;

    @FXML
    private TextField textId;

    @FXML
    private TextField textPath;

    @FXML
    private TextField textSource;

    @FXML
    private TextField textTitle;

    @FXML
    private TableColumn<Activity, String> title;

    /**
     * Constructor for this class.
     * @param server the ServerUtils variable that represents the server
     * @param mainCtrl the main controller of the app
     */
    @Inject
    public AdminScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the scene.
     * @param location the URL of the scene
     * @param resources other resources used for the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCtrl.loadImages(background, nukeSymbol, quitButton);
        id.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().id));
        imagePath.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().image_path));
        title.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().title));
        consumption.setCellValueFactory(q -> new SimpleStringProperty(Long.toString(q.getValue().consumption_in_wh)));
        source.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().source));
        server.checkForActivityUpdates((Consumer<Activity>) activity -> refresh());
    }

    /**
     * Lets a user select a file from which all activities will be imported.
     * Called after 'Select file' is clicked
     */
    public void selectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a file to import the activities from");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedPath = chooser.showOpenDialog(mainCtrl.getPrimaryStage());
        if (selectedPath != null) filePath.setText(selectedPath.getAbsolutePath());
    }

    /**
     * Checks if a file is selected and calls a reading method.
     * Called after 'Import all activities' is clicked
     */
    public void importAllActivities() {
        String path;
        String regex = "";
        String os = System.getProperty("os.name");
        if (os.contains("Mac"))
            regex = "/";
        if (os.contains("Win")) {
            regex = "\\\\";
        }
        path = breakFile(filePath.getText(), regex);
        if (filePath.getText().equals("")) {
            Alert noFile = new Alert(Alert.AlertType.ERROR);
            noFile.setHeaderText("Please select a file first");
            noFile.showAndWait();
        }
        else {
            ObjectMapper mapper = new ObjectMapper();
            try {


                List<Activity> toBeAdded  = mapper.readValue(
                        new File(filePath.getText()), new TypeReference<List<Activity>>(){});
                for (Activity a : toBeAdded) {
                    File file = new File(makePath(path, a.getImagePath(), regex));
                    if (file.exists()) {
                        a.setImage(extractBytes(makePath(path, a.getImagePath(), regex)));
                        server.addActivity(a);
                    }
                }
                refresh();
                Alert successfulImport = new Alert(Alert.AlertType.INFORMATION);
                successfulImport.setHeaderText("The file has been imported successfully!");
                successfulImport.showAndWait();
            }
            catch (IOException e) {
                Alert badFile = new Alert(Alert.AlertType.ERROR);
                badFile.setHeaderText("The selected .json file is not compatible with this application.");
                badFile.showAndWait();
            }
        }
    }

    /**
     * Creates a full path for a picture.
     * @param path - the absolute path of the .json activity bank
     * @param pathex - the relative path of a particular image
     * @param regex - the regex of the user's OS
     * @return - returns a path for an image
     */
    public String makePath(String path, String pathex, String regex) {
        return path + regex + pathex;
    }

    /**
     * Extracts all bytes from an image.
     * @param path - the path of an image
     * @return - returns a byte array for the image
     * @throws IOException if there is a problem working with the file
     */
    public byte[] extractBytes(String path) throws IOException {
        File fin = new File(path);
        return Files.readAllBytes(fin.toPath());
    }

    /**
     * Changes the String representation of a path so that importing works on different platforms.
     * @param path - the original file path
     * @param regex - the regex used in the OS
     * @return returns - a custom file path
     */
    public String breakFile(String path, String regex) {
        String[] paths = path.split(regex);
        StringBuilder s = new StringBuilder(new String(""));
        for (int i = 0; i < paths.length - 1; i++) {
            s.append(regex);
            s.append(paths[i]);
        }
        return s.toString();
    }

    /**
     * Checks if a valid Activity is entered and adds it to the database.
     * Called after the 'Nuclear' button is clicked
     */
    public void addActivity() {
        Activity activity = checkTextFields();
        if (activity != null) {
            server.addActivity(activity);
            refresh();
            Alert successfulAdd = new Alert(Alert.AlertType.INFORMATION);
            successfulAdd.setHeaderText("The activity has been added successfully!");
            successfulAdd.showAndWait();
        }
    }

    /**
     * Deletes the selected activity.
     * Called after the 'Delete activity' button is clicked
     */
    public void deleteActivity() {
        Activity toBeDeleted = table.getSelectionModel().getSelectedItem();
        if (toBeDeleted == null) {
            Alert noSelection = new Alert(Alert.AlertType.ERROR);
            noSelection.setHeaderText("Please select an activity from the table first");
            noSelection.showAndWait();
            return;
        }
        Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDeletion.setHeaderText("The activity is about to be deleted (no undo)!");
        ((Button) confirmDeletion.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) confirmDeletion.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

        Optional<ButtonType> res = confirmDeletion.showAndWait();
        if (res.get() == ButtonType.OK) {
            server.deleteActivity(toBeDeleted);
        }
        refresh();
    }

    /**
     * Deletes the selected activity and copies its data to add a new Activity.
     * Called after the 'Edit activity' button is clicked
     */
    public void editActivity() {
        Activity toBeEdited = table.getSelectionModel().getSelectedItem();
        if (toBeEdited == null) {
            Alert noSelection = new Alert(Alert.AlertType.ERROR);
            noSelection.setHeaderText("Please select an activity to edit from the table first");
            noSelection.showAndWait();
        }
        textId.setText(toBeEdited.getId());
        textPath.setText(toBeEdited.getImagePath());
        textTitle.setText(toBeEdited.getTitle());
        textConsumption.setText(Long.toString(toBeEdited.getConsumption()));
        textSource.setText(toBeEdited.getSource());
        server.deleteActivity(toBeEdited);
        commitEdit.setDisable(false);
        addActivity.setDisable(true);
        deleteActivity.setDisable(true);
        editActivity.setDisable(true);
    }

    /**
     * Commits the edited activity to the server.
     * Called after the 'Commit edit' button is clicked
     */
    public void commitEdit() {
        Activity toBeAdded = checkTextFields();
        if (toBeAdded != null) {
            server.addActivity(toBeAdded);
            refresh();
            commitEdit.setDisable(true);
            addActivity.setDisable(false);
            deleteActivity.setDisable(false);
            editActivity.setDisable(false);
        }
    }

    /**
     * Refreshes the data in the tableView.
     */
    public void refresh() {
        var activities = server.getActivities();
        data = FXCollections.observableList(activities);
        table.setItems(data);
    }

    /**
     * Asks a user whether he wants to return to the homescreen.
     * Called after the 'Nuclear' button is clicked
     */
    public void quit() {
        if (!commitEdit.isDisabled()) {
            Alert unsavedChanges = new Alert(Alert.AlertType.ERROR);
            unsavedChanges.setHeaderText("Cannot leave while there are uncommitted changes.");
            unsavedChanges.showAndWait();
        }
        else {
            mainCtrl.showHome();
        }
    }

    /**
     * Checks if Activity textFields contain a valid activity.
     * @return - returns an activity if it's valid and null otherwise
     */
    public Activity checkTextFields() {
        String id = textId.getText();
        String path = textPath.getText();
        String title = textTitle.getText();
        String source = textSource.getText();
        if (id.equals("") || path.equals("") || title.equals("")
                || textConsumption.getText().equals("") || source.equals("")) {
            Alert emptyFields = new Alert(Alert.AlertType.ERROR);
            emptyFields.setHeaderText("Please fill all the text fields with corresponding data.");
            emptyFields.showAndWait();
            return null;
        }
        try {
            long consumption = Long.parseLong(textConsumption.getText());
            if (consumption <= 0) {
                Alert negativeNumber = new Alert(Alert.AlertType.ERROR);
                negativeNumber.setHeaderText("Consumption must be a positive number!");
                negativeNumber.showAndWait();
                return null;
            }
            clearFields();
            Activity a = new Activity(id, path, title, consumption, source);
            try {
                a.setImage(extractBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return a;
        }
        catch (NumberFormatException e) {
            Alert wrongType = new Alert(Alert.AlertType.ERROR);
            wrongType.setHeaderText("Consumption must be an integer number!");
            wrongType.showAndWait();
            return null;
        }
    }

    /**
     * Clears all the textFields with Activity data.
     */
    public void clearFields() {
        textId.clear();
        textPath.clear();
        textTitle.clear();
        textConsumption.clear();
        textSource.clear();
    }

    /**
     * Calls the adminLPStop function in the ServerUtils.
     * The stop method from ServerUtils which causes the thread to stop.
     */
    public void stop() {
        server.adminLPStop();
    }
}
