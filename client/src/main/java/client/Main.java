/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import static com.google.inject.Guice.createInjector;

import java.io.IOException;
import java.net.URISyntaxException;

import client.scenes.*;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var player = FXML.load(AddPlayerCtrl.class, "client/scenes/AddPlayer.fxml", "css/getplayer.css");
        var home = FXML.load(HomeScreenCtrl.class, "client/scenes/HomeScreen.fxml", "css/homescreen.css");
        var admin = FXML.load(AdminScreenCtrl.class, "client/scenes/AdminScreen.fxml", "css/admin.css");
        var game = FXML.load(GameScreenCtrl.class, "client/scenes/GameScreen.fxml", "css/gamescreen.css");
        var guess = FXML.load(GuessQuestionCtrl.class, "client/scenes/GuessQuestionScreen.fxml", "css/gamescreen.css");;
        var waiting = FXML.load(WaitingRoomScreenCtrl.class, "client/scenes/WaitingRoomScreen.fxml", "css/waitingroom.css");
        var leaderboard = FXML.load(LeaderboardCtrl.class, "client/scenes/Leaderboard.fxml", "css/leaderboard.css");
        var countdown = FXML.load(CountdownScreenCtrl.class, "client/scenes/CountdownScreen.fxml", null);
        var correctAnswer = FXML.load(CorrectAnswerCtrl.class, "client/scenes/CorrectAnswer.fxml", null);
        var incorrectAnswer = FXML.load(IncorrectAnswerCtrl.class, "client/scenes/IncorrectAnswer.fxml", null);
        var howmuch = FXML.load(HowMuchScreenCtrl.class, "client/scenes/HowMuchScreen.fxml", null);
        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, player, home, admin, game, guess, waiting, leaderboard, countdown, correctAnswer, incorrectAnswer, howmuch);
    }
}
