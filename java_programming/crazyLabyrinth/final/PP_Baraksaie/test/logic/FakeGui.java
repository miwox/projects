package logic;

import java.util.List;
import java.util.Map;

public class FakeGui implements GUIGameConnector {

    @Override
    public void showFreeCard(WayCard freeCard) {

    }

    @Override
    public void rotateFreeCard(WayCard freeCard) {


    }

    @Override
    public void showWayCard(WayCard wayCard, int x_logical, int y_logical) {
    }

    @Override
    public void initializeArrows() {

    }


    @Override
    public void initializeAndShowPlayer(Player player) {

    }

    @Override
    public void animatePushHorizontalLeftToRightAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

    }

    @Override
    public void animatePushHorizontalRightToLeftAndRespawnPlayers(int y_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

    }

    @Override
    public void animatePushVerticalUpToDownAndRespawnPlayers(int x_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

    }

    @Override
    public void animatePushVerticalDownToUpAndRespawnPlayers(int x_logical, Map<PlayerIndex, Coordinate> respawnCoordinates, GameField gameState) {

    }

    @Override
    public void setDurationPushCardAnimation(int index) {

    }

    @Override
    public void highLightAvailable(Coordinate pos_logical) {

    }

    @Override
    public void highLightTreasureCard(Coordinate pos_logical) {

    }

    @Override
    public void highLightDisable(Coordinate pos_logical) {

    }

    @Override
    public void highLightNotAvailable(Coordinate pos_logical) {

    }

    @Override
    public void animateMovePlayerFromTo(PlayerIndex playerIndex, List<Coordinate> wayListToGo, GameField state) {

    }


    @Override
    public void removeTreasureCardUpdateTreasuresLeft(int x_logical, int y_logical, PlayerIndex index, int treasuresLeft) {

    }

    @Override
    public void displayShowCurrentPlayer(Player player) {

    }

    @Override
    public void deleteWayCardImage(int x_logical, int y_logical, boolean hasTreasure) {

    }

    @Override
    public void deleteFreeCardImage(boolean hasTreasure) {

    }


    @Override
    public void removePlayer(PlayerIndex index, Coordinate pos_logical) {

    }

    @Override
    public void displayHasWon(PlayerIndex playerIndex) {

    }

    @Override
    public void displayActionPushCard(PlayerIndex index) {

    }

    @Override
    public void displayActionMovePlayer(PlayerIndex index) {

    }

    @Override
    public void displayError(Exception exception) {

    }

    @Override
    public void textAndButtonsToDefault() {

    }

    @Override
    public void highLightFreeWayCard() {

    }

    @Override
    public void highLightDisableFreeWayCard() {

    }

    @Override
    public void disableAllHighLighting() {

    }

    @Override
    public void setArrowVisibility(Direction dir, Coordinate pos_blocked, boolean isVisible) {

    }

    @Override
    public void showBtnStartGame() {

    }

    @Override
    public void setDurationPlayerMovementAnimation(int index) {

    }


}
