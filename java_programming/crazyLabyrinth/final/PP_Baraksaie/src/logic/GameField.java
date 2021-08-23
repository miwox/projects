package logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GameField represent the logical component of this project
 *
 * @author Miwand Baraksaie inf104162
 */
public class GameField {

    /**
     * only used to init regular game field
     */
    private static final int SIZE_OF_WAY_CARD_FIELD = 7;
    private static final int NUMBER_OF_CORNER = 4;
    private static final int NUMBER_OF_FREE_CARD = 1;
    private static final int NUMBER_OF_CARDS = SIZE_OF_WAY_CARD_FIELD * SIZE_OF_WAY_CARD_FIELD + NUMBER_OF_FREE_CARD;
    private static final int NUMBER_OF_MOVABLE_L_WAY_CARD = 15;
    private static final int NUMBER_OF_MOVABLE_I_WAY_CARD = 13;
    private static final int NUMBER_OF_MOVABLE_T_WAY_CARD = 6;
    private static final int HEAD_OF_LIST = 0;
    /**
     * Flags for AI
     */
    private static final boolean NORMAL_AI = false;
    private static final boolean ADVANCED_AI = true;
    private static final Integer NOT_USED_FLAG = null;
    private static final int DISTANCE_DIAGONAL = 2;

    private static final IllegalInputException COMPUTER_TURN_EXCEPTION =
            new IllegalInputException("Not allowed, while computer is playing.");

    private static final IllegalInputException IS_STATE_ONE =
            new IllegalInputException("Please push Way Card!");


    /**
     * only used for mapping coordinates from controller
     */
    private static final int OFF_SET = 1;
    private static final int FIRST_WAY_CARD = 0;
    private static final int INDEX_FIRST_ROW_OR_COLUMN_TO_PUSH = 0;
    private static final int INDEX_LAST_ROW_OR_COLUMN_TO_PUSH = 8;
    private static final int X_FX_FREE_CARD_POS = 9;
    private static final int Y_FX_FREE_CARD_POS = 4;
    private static final int INDEX_FIRST_PUSH = 1;

    /**
     * player start position also necessary after finishing with collecting the treasures, after finishing this are coordinates are the goals to reach.
     */
    static final Coordinate[] PLAYER_START_COORDINATE = new Coordinate[]{
            new Coordinate(0, 0), // Yellow Player (Player 1)
            new Coordinate(6, 0), // Blue Player   (Player 2)
            new Coordinate(6, 6), // Green         (Player 3)
            new Coordinate(0, 6)  // Red           (Player 4)
    };
    static final Coordinate INVALID_POSITION = new Coordinate(-1, -1);
    private static final Coordinate FREE_CARD_POS = INVALID_POSITION;

    /**
     * very important the grid pane in the controller is bigger than the way card array
     * we don't want any null inside the array. so we have to map the coordinates of the controller once
     */
    private WayCard[][] wayCards;
    private WayCard freeCard;
    private final GUIGameConnector gui;
    private GameLogger logger;
    private List<Player> players;
    private Player currentTurn;
    private AiWork aiWork;

    /**
     * List with possible ways to go from current player position.
     * Coordinate = {From, To}
     * Necessary for the gui to animate the way to the target.
     */
    private List<Pair<Coordinate, Coordinate>> currentPossibleWays;

    /**
     * is input of the controller allowed
     */
    private boolean isInputAllowed;

    /**
     * states
     */
    private boolean isMoveWayCardState;
    private boolean isMovePlayerState;
    private boolean isRotationAllowedState;
    private Coordinate blockedCoordinate;
    private final int INDEX_LAST_WAY_CARD;
    private final int INDEX_FIRST_WAY_CARD = 0;
    private final int SIZE_GAME_FIELD;

    /**
     * important for saving and loading,
     * when the game is not started, the gui is not initialised
     */
    private boolean gameHasStarted = false;

    /**
     * Main Constructor get called from the game controller
     * It initialises the gui, and the player with random treasures.
     *
     * @param gui                 - graphical interface
     * @param treasuresEachPlayer - amount treasure of each player
     * @param playerModes         - array of player modes maps to names
     * @param names               - array of the player names maps to player modes
     * @param logger              - a logger
     */
    public GameField(GUIGameConnector gui, int treasuresEachPlayer, PlayerMode[] playerModes, String[] names, GameLogger logger) {
        this.gui = gui;
        this.wayCards = new WayCard[SIZE_OF_WAY_CARD_FIELD][SIZE_OF_WAY_CARD_FIELD];
        this.INDEX_LAST_WAY_CARD = wayCards.length - 1;
        this.SIZE_GAME_FIELD = wayCards[0].length;
        this.logger = logger;
        players = new LinkedList<>();

        // init with minus to signalise that there is no blockedWayCard.
        this.blockedCoordinate = INVALID_POSITION;
        // init players
        for (int i = 0; i < playerModes.length; i++) {
            if (playerModes[i] != PlayerMode.NONE) {
                Player player = new Player(PlayerIndex.values()[i], playerModes[i], names[i], PLAYER_START_COORDINATE[i]);
                players.add(player);
            }
        }
        currentTurn = players.get(0);

        // init game and gui with treasures randomly
        initGameAndGui(treasuresEachPlayer);
    }

    /**
     * Constructor for test the movement between the way cards.
     *
     * @param gui           fake gui
     * @param arrayWayCards way cards
     * @param freeCard      free card
     */
    GameField(GUIGameConnector gui, WayCard[][] arrayWayCards, WayCard freeCard) {
        this.gui = gui;
        this.wayCards = arrayWayCards;
        this.freeCard = freeCard;
        this.INDEX_LAST_WAY_CARD = arrayWayCards[0].length - 1;
        this.SIZE_GAME_FIELD = arrayWayCards[0].length;
        this.players = new LinkedList<>();
        this.currentTurn = new Player(PlayerIndex.PLAYER_ONE, PlayerMode.NONE, new Coordinate(0, 0));
        setStateTwoUpdatePossibleWays();
    }

    /**
     * Set directly to state two. For testing.
     *
     * @param gui      - fake gui
     * @param wayCards - way cards
     * @param freeCard - free card
     * @param players  - players
     */
    GameField(GUIGameConnector gui, WayCard[][] wayCards, WayCard freeCard, Player... players) {
        this.gui = gui;
        this.wayCards = wayCards;
        this.freeCard = freeCard;
        this.players = Arrays.asList(players);
        this.INDEX_LAST_WAY_CARD = wayCards[0].length - 1;
        this.SIZE_GAME_FIELD = wayCards[0].length;
        this.currentTurn = this.players.get(0);
        setStateTwoUpdatePossibleWays();
    }

    /**
     * Set directly to state two. For testing.
     *
     * @param fakeGui  - fake gui
     * @param wayCards - way cards
     * @param player   - player
     */
    GameField(GUIGameConnector fakeGui, WayCard[][] wayCards, Player player) {
        this.gui = fakeGui;
        this.wayCards = wayCards;
        this.players = Arrays.asList(player);
        this.INDEX_LAST_WAY_CARD = wayCards[0].length - 1;
        this.SIZE_GAME_FIELD = wayCards[0].length;
        this.currentTurn = players.get(0);
        setStateTwoUpdatePossibleWays();
    }

    /**
     * For testing the logic.
     * Real game field size with players and treasures
     * Initialise the given numbers of player as Humans
     *
     * @param fakeGui            - fake interface
     * @param treasureEachPlayer - number of treasure of each player
     * @param numberOfPlayer     - the number of player
     */
    GameField(GUIGameConnector fakeGui, int treasureEachPlayer, int numberOfPlayer, GameLogger fakeLogger) {
        this.gui = fakeGui;
        this.wayCards = new WayCard[SIZE_OF_WAY_CARD_FIELD][SIZE_OF_WAY_CARD_FIELD];
        this.INDEX_LAST_WAY_CARD = SIZE_OF_WAY_CARD_FIELD - 1;
        this.SIZE_GAME_FIELD = SIZE_OF_WAY_CARD_FIELD;
        this.logger = fakeLogger;
        players = new LinkedList<>();
        for (int i = 0; i < numberOfPlayer; i++) {
            players.add(new Player(PlayerIndex.values()[i], PlayerMode.HUMAN, PLAYER_START_COORDINATE[i]));
        }
        this.initGameAndGui(treasureEachPlayer);
        this.freeCard = new WayCard(WayType.I, Rotation.ZERO, Treasure.NONE);
    }

    /**
     * Init and start the game. Get called by the public constructor.
     * Will init: treasures, game field, players and gui.
     *
     * @param numberOfTreasureEachPlayer - number of treasures each player
     */
    private void initGameAndGui(int numberOfTreasureEachPlayer) {
        int numberOfTreasure = players.size() * numberOfTreasureEachPlayer;
        List<Treasure> listWithTreasures = new LinkedList<>();

        // add all possible treasures in a list
        for (int i = Treasure.TREASURE_1.ordinal(); i < Treasure.values().length; i++) {
            listWithTreasures.add(Treasure.values()[i]);
        }
        Collections.shuffle(listWithTreasures);
        // get only the treasures which are really needed
        listWithTreasures = listWithTreasures.subList(0, numberOfTreasure);
        Collections.shuffle(listWithTreasures);

        for (int i = 0; i < players.size(); i++) {
            List<Treasure> subList = listWithTreasures.subList(i * (listWithTreasures.size() / players.size()), (i + 1) * (listWithTreasures.size() / players.size()));
            Player currPlayer = players.get(i);
            currPlayer.addTreasures(subList);
        }

        initializeHardWayCards();
        // list with treasures is already shorten
        initializeDynamicGameFieldUnitsAndTreasure(listWithTreasures);
        initializeGuiWithPlayCards();
        initializeGuiWithPlayers();
    }

    /**
     * Start the game, after pressing start game button
     */
    public void startGame() {
        int treasures = 0;
        gameHasStarted = true;
        for (Player player : players) {
            treasures += player.getTreasures().size();
            logger.logPlayingPlayer(player);
            logger.logPlayerHasTreasures(player);
        }

        // log not involved players
        for (int index = 0; index < PlayerIndex.values().length; index++) {
            boolean flagNotInvolved = false;
            PlayerIndex pIndex = PlayerIndex.values()[index];
            for (Player value : players) {
                flagNotInvolved |= (value.getPlayerIndex() == pIndex);
            }
            if (!flagNotInvolved) {
                logger.logPlayerNotInvolved(pIndex);
            }
        }

        // important for loading files, where the player hast already won.
        if (hasWon(currentTurn)) {
            winningSituation();
        } else {
            logger.logTotalTreasures(treasures);
            startAndSetStateOne();
        }
    }


    /**
     * State;
     * private Coordinate blockedCoordinate;
     * private final int INDEX_LAST_WAY_CARD;
     * private final int INDEX_FIRST_WAY_CARD = 0;
     */
    private void initializeGuiWithPlayers() {
        players.forEach(gui::initializeAndShowPlayer);
    }

    /**
     * Initialise the gui with all way card and the free card.
     */
    private void initializeGuiWithPlayCards() {
        for (int y_logical = 0; y_logical < SIZE_OF_WAY_CARD_FIELD; y_logical++) {
            for (int x_logical = 0; x_logical < SIZE_OF_WAY_CARD_FIELD; x_logical++) {
                this.gui.showWayCard(this.wayCards[x_logical][y_logical], x_logical, y_logical);
            }
        }
        gui.showFreeCard(this.freeCard);
    }

    /**
     * Initialize the way card which are fixed in position with fixed way card.
     */
    private void initializeHardWayCards() {
        wayCards[0][0] = new WayCard(WayType.L, Rotation.NINETY, Treasure.NONE);
        wayCards[2][0] = new WayCard(WayType.T, Rotation.ZERO, Treasure.NONE);
        wayCards[4][0] = new WayCard(WayType.T, Rotation.ZERO, Treasure.NONE);
        wayCards[6][0] = new WayCard(WayType.L, Rotation.HUNDRED_EIGHTY, Treasure.NONE);
        wayCards[6][2] = new WayCard(WayType.T, Rotation.NINETY, Treasure.NONE);
        wayCards[6][4] = new WayCard(WayType.T, Rotation.NINETY, Treasure.NONE);
        wayCards[6][6] = new WayCard(WayType.L, Rotation.TWO_HUNDRED_SEVENTY, Treasure.NONE);
        wayCards[4][6] = new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.NONE);
        wayCards[2][6] = new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.NONE);
        wayCards[0][6] = new WayCard(WayType.L, Rotation.ZERO, Treasure.NONE);
        wayCards[0][4] = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, Treasure.NONE);
        wayCards[0][2] = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, Treasure.NONE);
        wayCards[2][2] = new WayCard(WayType.T, Rotation.TWO_HUNDRED_SEVENTY, Treasure.NONE);
        wayCards[4][2] = new WayCard(WayType.T, Rotation.ZERO, Treasure.NONE);
        wayCards[4][4] = new WayCard(WayType.T, Rotation.NINETY, Treasure.NONE);
        wayCards[2][4] = new WayCard(WayType.T, Rotation.HUNDRED_EIGHTY, Treasure.NONE);
    }

    /**
     * Initialise the logical way card array with way cards and treasures randomly.
     * Initialise the free card.
     *
     * @param listWithTreasures - list with all treasures
     */
    private void initializeDynamicGameFieldUnitsAndTreasure(List<Treasure> listWithTreasures) {
        List<WayCard> listWayCards = new LinkedList<>();
        int numberOfTreasure = listWithTreasures.size();

        Collections.shuffle(listWithTreasures);
        for (int i = 0; i < NUMBER_OF_CARDS - NUMBER_OF_CORNER - numberOfTreasure; i++) {
            listWithTreasures.add(Treasure.NONE);
        }
        Rotation rotation;
        for (int i = 0; i < NUMBER_OF_MOVABLE_L_WAY_CARD; i++) {
            rotation = Rotation.getRandomRotation();
            // treasure = listWithTreasures.remove(HEAD_OF_LIST);

            listWayCards.add(new WayCard(WayType.L, rotation, Treasure.NONE));
        }

        for (int i = 0; i < NUMBER_OF_MOVABLE_I_WAY_CARD; i++) {
            rotation = Rotation.getRandomRotation();
            //  treasure = listWithTreasures.remove(HEAD_OF_LIST);
            listWayCards.add(new WayCard(WayType.I, rotation, Treasure.NONE));
        }

        for (int i = 0; i < NUMBER_OF_MOVABLE_T_WAY_CARD; i++) {
            rotation = Rotation.getRandomRotation();
            //  treasure = listWithTreasures.remove(HEAD_OF_LIST);
            listWayCards.add(new WayCard(WayType.T, rotation, Treasure.NONE));
        }

        Collections.shuffle(listWayCards);
        for (int y = 0; y < SIZE_OF_WAY_CARD_FIELD; y++) {
            for (int x = (y % 2 == 0) ? 1 : 0; x < SIZE_OF_WAY_CARD_FIELD; x++) {
                int random = new Random().nextInt(listWayCards.size());
                wayCards[x][y] = listWayCards.remove(random);
                x = y % 2 == 0 ? (x + 1) : x; // If
            }
        }

        // Fill the first and last row treasures. Don't fill the corner.
        for (int y = 0; y < SIZE_OF_WAY_CARD_FIELD; y += (SIZE_OF_WAY_CARD_FIELD - 1)) {
            for (int x = 1; x < SIZE_OF_WAY_CARD_FIELD - 1; x++) {
                int random = new Random().nextInt(listWithTreasures.size());
                wayCards[x][y].setTreasure(listWithTreasures.remove(random));
            }
        }
        // Fill snd - snd last with treasures
        for (int y = 1; y < SIZE_OF_WAY_CARD_FIELD - 1; y++) {
            for (int x = 0; x < SIZE_OF_WAY_CARD_FIELD; x++) {
                int random = new Random().nextInt(listWithTreasures.size());
                wayCards[x][y].setTreasure(listWithTreasures.remove(random));
            }
        }

        freeCard = listWayCards.remove(HEAD_OF_LIST);
        freeCard.setTreasure(listWithTreasures.remove(HEAD_OF_LIST));

        assert listWithTreasures.isEmpty();
        assert listWayCards.isEmpty();
    }

    /**
     * Get called when user clicked on free card.
     * Rotate the free card clockwise.
     */
    public void rotateFreeCard() {
        freeCard.rotateClockWise();
        gui.rotateFreeCard(freeCard);
    }

    /**
     * Get free card.
     *
     * @return WayCard freeCard
     */
    WayCard getFreeCard() {
        return freeCard;
    }

    /**
     * Getter method for current turn player
     * @return player
     */
    Player getCurrentTurn(){ return  currentTurn;}

    /**
     * For test
     * @return blocked coordinate
     */
    Coordinate getBlockedCoordinate() {
        return this.blockedCoordinate;
    }

    /**
     * Get all players in a list.
     *
     * @return players
     */
    List<Player> getPlayers() {
        return players;
    }

    /**
     * Get Ai Work
     * @return ai Work
     */
    AiWork getAiWork(){ return aiWork; }

    /**
     * Get all way cards which are part of the current game (not the free card).
     *
     * @return way cards
     */
    WayCard[][] getWayCards() {
        return wayCards;
    }

    /**
     * Sets the free card, for test and debugging
     *
     * @param freeCard - to set
     */
    void setFreeCard(WayCard freeCard) {
        this.freeCard = freeCard;
    }

    /**
     * Get the current possible ways from the current position of the current player.
     * Only return a List with all possible coordinates to go.
     * No information about origin of the coordinate.
     *
     * @return List of all possible coordinates to go, from the current position of the current player.
     */
    List<Coordinate> getCurrentPossibleWays() {
        return extractPossibleWays(currentPossibleWays);
    }

    /**
     * Extracts the list of the possible ways with origin information of each coordinate
     * to a list of possible coordinates to move, without origin information.
     *
     * @param possibleWaysFromRef - a list with Pair of coordinates the key represents the origin of the value.
     * @return an extracted list with possible coordinates to move.
     */

    List<Coordinate> extractPossibleWays(List<Pair<Coordinate, Coordinate>> possibleWaysFromRef) {
        List<Coordinate> coordinate = new LinkedList<>();
        for (Pair<Coordinate, Coordinate> obj : possibleWaysFromRef) {
            coordinate.add(obj.getKey());
        }
        return coordinate;
    }

    /**
     * Calculate the shortest way from the current start position to a destination.
     * The list is ordered, the first element is the first coordinate to go, and the second the third and so on.
     * The last coordinate of the list is the target.
     *
     * @param possibleWaysFromRef - list with possible ways with origin information
     * @param target              - the target position to go
     * @return the shortest way list to target first element the current start point, and the last element is the targets. Elements between are the coordinates to go
     * Example (0,0) is start and target to go is (0,3) List = {(0,0), (0,1), (0,2) ,(0,3)}
     */
    private List<Coordinate> getShortestWayTo(List<Pair<Coordinate, Coordinate>> possibleWaysFromRef, Coordinate target) {
        assert extractPossibleWays(possibleWaysFromRef).contains(target);
        List<Coordinate> shortestWayTo = new LinkedList<>();
        int idx = 0;
        // find coordinate target
        while (!possibleWaysFromRef.get(idx++).getKey().equals(target)) ;
        Pair<Coordinate, Coordinate> start = possibleWaysFromRef.get(idx - 1);
        // build path to target
        while (!start.getKey().equals(start.getValue())) {
            shortestWayTo.add(HEAD_OF_LIST, start.getKey());
            Coordinate from = start.getValue();
            idx = 0;
            // find where we come from
            while (!possibleWaysFromRef.get(idx++).getKey().equals(from)) ;
            start = possibleWaysFromRef.get(idx - 1);
        }
        // add the start also
        shortestWayTo.add(HEAD_OF_LIST, start.getKey());
        return shortestWayTo;
    }

    /**
     * Get the list way list from currentPossibleWays of the current player,
     * to his target he/she wants to go
     *
     * @param target - position to go.
     * @return the way list from start coordinate to target coordinate
     */
    List<Coordinate> getCurrentShortestWayTo(Coordinate target) {
        return getShortestWayTo(currentPossibleWays, target);
    }

    /**
     * Moves the way cards horizontal in a direction (left or right)
     * Get called from logic so no computed size of offset is needed.
     * Will push the free card exact at the position of y
     * and will move the others cards left or right.
     *
     * @param direction - direction to move left or right
     * @param y_logical - exact position to move.
     * @return the new free card
     */
    WayCard moveCardHorizontalGetFreeCard(Direction direction, WayCard[][] wayCards, WayCard freeCard, int y_logical) {
        assert direction == Direction.LEFT || direction == Direction.RIGHT;
        WayCard newFreeCard = direction == Direction.RIGHT ? wayCards[wayCards[0].length - 1][y_logical] : wayCards[0][y_logical];
        int operator = direction == Direction.RIGHT ? -1 : 1;
        // check the direction
        for (int i = (direction == Direction.RIGHT ? INDEX_LAST_WAY_CARD : FIRST_WAY_CARD);
             direction == Direction.RIGHT ? (i > FIRST_WAY_CARD) : (i < INDEX_LAST_WAY_CARD);
             i += operator) {
            wayCards[i][y_logical] = wayCards[i + operator][y_logical];
        }
        wayCards[direction == Direction.RIGHT ? FIRST_WAY_CARD : INDEX_LAST_WAY_CARD][y_logical] = freeCard;
        return newFreeCard;
    }

    /**
     * Updates the player position after pushing the free card horizontal
     *
     * @param direction - direction to push left or right
     * @param players   - the players to update
     * @param y_logical - exact position
     * @return when a player is moving out of the way card, it has to been respawn on the other side. Need it for the gui.
     */
    Map<PlayerIndex, Coordinate> updatePlayerPosAfterPushHorizontalGetRespawn(Direction direction, List<Player> players, int y_logical) {
        assert direction == Direction.LEFT || direction == Direction.RIGHT;
        Map<PlayerIndex, Coordinate> respawnCoordinate = new HashMap<>();
        final int operator = direction == Direction.RIGHT ? -1 : 1;
        final int indexLastWayCard = INDEX_LAST_WAY_CARD;
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            // check position in pushed row then add new.
            if (currPlayer.getPosition().getY() == y_logical) {
                Coordinate playerPos = currPlayer.getPosition();
                // when player is on border then teleport to another side.
                if (playerPos.getX() == INDEX_FIRST_WAY_CARD && direction == Direction.LEFT) {
                    Coordinate respawn = new Coordinate(indexLastWayCard, y_logical);
                    currPlayer.setPosition(respawn);
                    respawnCoordinate.put(currPlayer.getPlayerIndex(), respawn);
                } else if (playerPos.getX() == indexLastWayCard && direction == Direction.RIGHT) {
                    Coordinate respawn = new Coordinate(INDEX_FIRST_WAY_CARD, y_logical);
                    currPlayer.setPosition(respawn);
                    respawnCoordinate.put(currPlayer.getPlayerIndex(), respawn);
                } else {
                    //don't need to respawn.
                    currPlayer.setPosition(new Coordinate(playerPos.getX() - operator, y_logical));
                }
            }
        }
        return respawnCoordinate;
    }

    /**
     * Update the position of the players.
     * And will return the new position of the players, in a map if there is a respawn.
     * Respawn coordinate for animation in the gui.
     *
     * @param direction - direction of push up or down
     * @param logical_x - logical coordinate x
     * @return respawn coordinate with player index and the new respawned position
     */
    Map<PlayerIndex, Coordinate> updatePlayerPosAfterPushVerticalGetRespawn(Direction direction, List<Player> players, int logical_x) {
        assert direction == Direction.UP || direction == Direction.DOWN;

        int operator = direction == Direction.UP ? 1 : -1;
        Map<PlayerIndex, Coordinate> respawnCoordinates = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            Player currPlayer = players.get(i);
            // check position in pushed row then add new.
            if (currPlayer.getPosition().getX() == logical_x) {
                Coordinate playerPos = currPlayer.getPosition();
                // when player is on border then teleport to another side.
                if (direction == Direction.UP && playerPos.getY() == FIRST_WAY_CARD) {
                    Coordinate respawn = new Coordinate(logical_x, INDEX_LAST_WAY_CARD);
                    currPlayer.setPosition(respawn);
                    respawnCoordinates.put(currPlayer.getPlayerIndex(), respawn);
                } else if (direction == Direction.DOWN && playerPos.getY() == INDEX_LAST_WAY_CARD) {
                    Coordinate respawn = new Coordinate(logical_x, FIRST_WAY_CARD);
                    currPlayer.setPosition(respawn);
                    respawnCoordinates.put(currPlayer.getPlayerIndex(), respawn);
                } else {
                    //don't need to respawn.
                    currPlayer.setPosition(new Coordinate(logical_x, playerPos.getY() - operator));
                }
            }
        }
        return respawnCoordinates;
    }

    /**
     * Moves the way cards vertical, in a direction (up or down).
     * Insert the free card in the way card field
     * Package private for JUNIT - test.
     *
     * @param direction - must be UP or DOWN
     * @param wayCards  - field of the way cards
     * @param freeCard  - current free card - to set
     * @param logical_x - position of the column to move. Don't add offset because it get called private
     * @return new free Card
     */
    WayCard moveCardVerticalAndGetNewFreeCard(Direction direction, WayCard[][] wayCards, WayCard freeCard, int logical_x) {
        assert direction == Direction.UP || direction == Direction.DOWN;
        WayCard newFreeCard = direction == Direction.UP ? wayCards[logical_x][0] : wayCards[logical_x][wayCards[0].length - 1];
        int operator = direction == Direction.UP ? 1 : -1;
        for (int i = (direction == Direction.UP ? INDEX_FIRST_WAY_CARD : INDEX_LAST_WAY_CARD);
             direction == Direction.UP ? (i < INDEX_LAST_WAY_CARD) : (i > INDEX_FIRST_WAY_CARD);
             i += operator) {
            wayCards[logical_x][i] = wayCards[logical_x][i + operator];
        }
        wayCards[logical_x][direction == Direction.UP ? wayCards[0].length - 1 : 0] = freeCard;

        return newFreeCard;
    }

    /**
     * Starts the first round.
     */
    private void startAndSetStateOne() {
        setStateOnePushCardUpdatePossibleWays();
        switch (currentTurn.getPlayerMode()) {
            case HUMAN:
                break;
            case COMPUTER_1:
                doAiWorkPushCardNormal();
                break;
            case COMPUTER_2:
                doAiWorkPushCardAdvanced();
                break;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Does AI work, to push card advanced
     * Try to find ways with distance 2 (diagonal) or 0 if possible.
     * It avoids taking distances with 1, because of being direct neighbour of the target
     * If there is no preferred option, it uses the normal AI method to push the card in.
     * See the documentation for more information
     */
    private void doAiWorkPushCardAdvanced() {
        aiWork = getAiInformationToPushFreeCard(wayCards, freeCard, currentTurn, blockedCoordinate, ADVANCED_AI);
        Coordinate currentPosition = currentTurn.getPosition();
        Coordinate currentNextTarget = getTargetToGo(currentTurn, wayCards);
        Coordinate toPushCardIn = null;
        Rotation rot = null;

        // go to target directly.
        if (aiWork.getDistance() == 0 || (currentPosition.distance(currentNextTarget) != aiWork.getDistance()
                && aiWork.getDistance() > DISTANCE_DIAGONAL)) {

            int usedOption = 0;
            aiWork.setUsedOption(usedOption);
            rot = aiWork.getRotationList().get(usedOption);
            toPushCardIn = aiWork.getPushCoordinates().get(usedOption);

        } else if (aiWork.getDistance() == DISTANCE_DIAGONAL) {

            // there distance of 2, it's possible that there good diagonal and connected options,
            AiWork advanced = findDiagonalWayAiWorkAdvanced(aiWork);
            // try to find even better options by filtering the options
            advanced = filterBestOptionsForAi(advanced, currentTurn.getPlayerIndex(), players, wayCards, freeCard, ADVANCED_AI);
            if (advanced.getNumberOfOptions() > 0) {
                aiWork = advanced;
                int usedOption = 0;
                aiWork.setUsedOption(usedOption);
                rot = aiWork.getRotationList().get(usedOption);
                toPushCardIn = aiWork.getPushCoordinates().get(usedOption);
            }
        }

        if (toPushCardIn == null) {
            // use the normal AI to get the nearest way to go
            aiWork = getAiInformationToPushFreeCard(wayCards, freeCard, currentTurn, blockedCoordinate, NORMAL_AI);
            if (aiWork.getDistance() == currentPosition.distance(currentNextTarget)) {
                // use the normal AI because there is no good option, and to avoid cycle
                toPushCardIn = getPushCoordinateForNormalAi(currentPosition, currentNextTarget, blockedCoordinate);
                rot = freeCard.getRotation();
            } else {
                // try to change the position of the other players or the position of their targets
                aiWork = filterBestOptionsForAi(aiWork, currentTurn.getPlayerIndex(), players, wayCards, freeCard, ADVANCED_AI);
                toPushCardIn = aiWork.getPushCoordinates().get(0);
                rot = aiWork.getRotationList().get(0);
                aiWork.setUsedOption(0);
            }
        }

        // rotate the free card
        if (toPushCardIn != null) {
            while (freeCard.getRotation() != rot) {
                rotateFreeCard();
            }
        }

        // handle pushing
        assert toPushCardIn != null;
        Direction dir = getPushDirection(toPushCardIn);
        pushRowColumn(toPushCardIn.getX(), toPushCardIn.getY(), dir);
    }

    /**
     * Filters the advanced AI option, with maybe diagonal options inside.
     * It will find the diagonal options, so the player can reach the target,
     * on the next step-
     *
     * @param aiWork - AI options to filter
     * @return - options with diagonal connection
     */
    private AiWork findDiagonalWayAiWorkAdvanced(AiWork aiWork) {
        assert aiWork.getDistance() == DISTANCE_DIAGONAL;

        int options = aiWork.getPlayerToGoCoordinatesAfterPush().size();
        AiWork advancedAi = new AiWork();
        advancedAi.setNewDistance(DISTANCE_DIAGONAL);
        List<Coordinate> pushCoordinates = aiWork.getPushCoordinates();
        List<Rotation> freeCardRotations = aiWork.getRotationList();
        List<Coordinate> playerToGoCoordinate = aiWork.getPlayerToGoCoordinatesAfterPush();

        // checking opportunities of each option, if its connected and diagonal
        for (int i = 0; i < options; i++) {
            WayCard[][] cWayCards = getDeepCopyOfWayCards(wayCards);
            WayCard cFreeCard = new WayCard(freeCard);
            Coordinate pushCoordinate = pushCoordinates.get(i);
            Direction dir = getPushDirection(pushCoordinate);
            // updates the way cards after using the option
            switch (dir) {
                case UP:
                case DOWN:
                    int x_logical = pushCoordinate.getX();
                    moveCardVerticalAndGetNewFreeCard(dir, cWayCards, cFreeCard, x_logical);
                    break;
                case LEFT:
                case RIGHT:
                    int y_logical = pushCoordinate.getY();
                    moveCardHorizontalGetFreeCard(dir, cWayCards, cFreeCard, y_logical);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + dir);
            }

            // checking if the option is diagonal and connected
            Coordinate posPlayerToGoMaybeDiagonalAfterPush = playerToGoCoordinate.get(i);
            Coordinate posTargetAfterPush = getTargetToGo(currentTurn, cWayCards);
            WayCard wayCardToGoMaybeDiagonalAfterPush = cWayCards[posPlayerToGoMaybeDiagonalAfterPush.getX()][posPlayerToGoMaybeDiagonalAfterPush.getY()];
            WayCard posTargetWayCardAfterPush = cWayCards[posTargetAfterPush.getX()][posTargetAfterPush.getY()];

            // adding to the new advanced AI solution
            if (isDiagonalAndConnected(posPlayerToGoMaybeDiagonalAfterPush, wayCardToGoMaybeDiagonalAfterPush, posTargetAfterPush, posTargetWayCardAfterPush)) {
                Rotation rotFreeCard = freeCardRotations.get(i);
                advancedAi.addNewValues(pushCoordinate,
                        rotFreeCard,
                        posPlayerToGoMaybeDiagonalAfterPush
                );
            }
        }

        return advancedAi;
    }

    /**
     * This method will test if a given way card with position, is diagonal and reachable two a target way card.
     * Is not only important the two-way cards are diagonal, its also important that the destination is reachable after one pushing
     * the way card
     *
     * @param posPlayer     - position of the player he/she wants to move
     * @param wayCardPlayer - the way card of the position, that the player wants to move
     * @param posTarget     - the target position
     * @param wayCardTarget - the target way card
     * @return boolean - is diagonal and connected
     */
    private boolean isDiagonalAndConnected(Coordinate posPlayer, WayCard wayCardPlayer, Coordinate posTarget, WayCard wayCardTarget) {
        assert posPlayer.distance(posTarget) == DISTANCE_DIAGONAL;
        boolean isDiagonal = posTarget.isDiagonal(posPlayer);

        // check position of player to target
        int distanceX = posPlayer.getX() - posTarget.getX();
        int distanceY = posPlayer.getY() - posTarget.getY();

        // is connected to target
        boolean upFromPlayerConnected = false,
                rightFromPlayerConnected = false,
                downFromPlayerConnected = false,
                leftFromPlayerConnected = false;

        // check the diagonal from the player to the target, 4 possible diagonal position of the player to the target
        boolean leftUpFromTarget = distanceX == -1 && distanceY == -1;
        boolean leftDownFromTarget = distanceX == -1 && distanceY == 1;
        boolean rightUpFromTarget = distanceX == 1 && distanceY == -1;
        boolean rightDownFromTarget = distanceX == 1 && distanceY == 1;

        // when the player is left-up or right-up to the target, to reach the target on the next pushing situation
        // the way card of the player, he/she wants to go, must be connected down. So the target way card must be up possible to go,
        // the way card of the player, he/she wants to go, must be connected down possible to,
        // when this condition are give, so the target way card is reachable on the next or second pushing (when blocked coordinate, doesn't allow
        // to push it on next turn).
        if (leftUpFromTarget || rightUpFromTarget) {
            downFromPlayerConnected = wayCardPlayer.isDownPossibleToGo() && wayCardTarget.isUpPossibleToGo();
        }

        // see above
        if (leftDownFromTarget || rightDownFromTarget) {
            upFromPlayerConnected = wayCardPlayer.isUpPossibleToGo() && wayCardTarget.isDownPossibleToGo();
        }

        // see above
        if (leftDownFromTarget || leftUpFromTarget) {
            rightFromPlayerConnected = wayCardPlayer.isRightPossibleToGo() && wayCardTarget.isLeftPossibleToGo();
        }

        // see above
        if (rightDownFromTarget || rightUpFromTarget) {
            leftFromPlayerConnected = wayCardPlayer.isLeftPossibleToGo() && wayCardTarget.isRightPossibleToGo();
        }

        // if the position are diagonal, and one of the above condition is true,
        // we can accept it
        return isDiagonal
                && (rightFromPlayerConnected
                || leftFromPlayerConnected
                || upFromPlayerConnected
                || downFromPlayerConnected);
    }


    /**
     * See documentation for further details.
     * Does work for normal AI, to find the best option to push the way card in
     * If there is no changes in distance between before and after, we will push the way card to move the player
     * or the treasure, or randomly if both position are fixed
     * At first we get the best options for the AI, to push the free card and where to go.
     * We will try to filter the options, so if an advanced AI is playing, that position and the target position
     * of the advanced AI is not changing. If there is no filtered option, we will take still the best option.
     */
    private void doAiWorkPushCardNormal() {
        aiWork = getAiInformationToPushFreeCard(wayCards, freeCard, currentTurn, blockedCoordinate, NORMAL_AI);
        Coordinate currentPosition = currentTurn.getPosition();
        Coordinate currentNextTarget = getTargetToGo(currentTurn, wayCards);
        int currDistanceToTarget = currentPosition.distance(currentNextTarget);
        Coordinate toPushCardIn = null;
        // if there is no changes in distance after pushing
        if (aiWork.getDistance() != 0
                && aiWork.getDistance() == currDistanceToTarget) {
            aiWork.setUsedOption(NOT_USED_FLAG);
            toPushCardIn = getPushCoordinateForNormalAi(currentPosition, currentNextTarget, blockedCoordinate);
        }

        if (toPushCardIn == null) {
            // try to don't move the player and the next target of the advanced AI, if possible.
            aiWork = filterBestOptionsForAi(aiWork, currentTurn.getPlayerIndex(), players, wayCards, freeCard, NORMAL_AI);
            // for the normal AI we take the option from the list.
            int option = 0;
            aiWork.setUsedOption(option);
            Rotation rot = aiWork.getRotationList().get(option);
            toPushCardIn = aiWork.getPushCoordinates().get(option);
            while (freeCard.getRotation() != rot) {
                rotateFreeCard();
            }
        }
        // handle pushing
        Direction dir = getPushDirection(toPushCardIn);
        pushRowColumn(toPushCardIn.getX(), toPushCardIn.getY(), dir);
    }

    /**
     * Filter the best option of given options.
     * When the advanced AI is playing, we try to find options, to move the position of the other players.
     * When the normal AI is playing, we try to find options, where the target position and position of the advanced AI, is not changing.
     *
     * @param aiWork      - options with all the same distance to target
     * @param playerIndex - current player
     * @param players     - all players
     * @param wayCards    - current way cards
     * @param freeCard    - current free card
     * @param isAdvanced  - advanced
     * @return the new aiWork with better options, if there is good option, than return the given one
     */
    private AiWork filterBestOptionsForAi(AiWork aiWork, PlayerIndex playerIndex, List<Player> players, WayCard[][] wayCards, WayCard freeCard, boolean isAdvanced) {
        AiWork bestOptionAi = new AiWork();
        bestOptionAi.setNewDistance(aiWork.getDistance());
        int options = aiWork.getPushCoordinates().size();

        for (int i = 0; i < options; i++) {
            List<Player> cPlayers = getDeepCopyOfPlayer(players);
            WayCard[][] cWayCards = getDeepCopyOfWayCards(wayCards);
            WayCard cFreeCard = new WayCard(freeCard);

            Coordinate pushCoordinate = aiWork.getPushCoordinates().get(i);
            Rotation rotOfFreeCard = aiWork.getRotationList().get(i);
            cFreeCard.setRotation(rotOfFreeCard);
            Direction dir = getPushDirection(pushCoordinate);


            List<Coordinate> posPlayersBeforePush = cPlayers.stream().map(x -> new Coordinate(x.getPosition())).collect(Collectors.toList());
            List<Coordinate> posTargetsBeforePush = cPlayers.stream().map(x -> new Coordinate(getTargetToGo(x, cWayCards))).collect(Collectors.toList());

            //
            switch (dir) {
                case UP:
                case DOWN:
                    updatePlayerPosAfterPushVerticalGetRespawn(dir, cPlayers, pushCoordinate.getX());
                    moveCardVerticalAndGetNewFreeCard(dir, cWayCards, cFreeCard, pushCoordinate.getX());
                    break;
                case RIGHT:
                case LEFT:
                    updatePlayerPosAfterPushHorizontalGetRespawn(dir, cPlayers, pushCoordinate.getY());
                    moveCardHorizontalGetFreeCard(dir, cWayCards, cFreeCard, pushCoordinate.getY());
                    break;
            }

            List<Coordinate> posPlayerAfterPush = cPlayers.stream().map(x -> new Coordinate(x.getPosition())).collect(Collectors.toList());
            List<Coordinate> posTargetAfterPush = cPlayers.stream().map(x -> new Coordinate(getTargetToGo(x, cWayCards))).collect(Collectors.toList());
            assert cPlayers.size() == posPlayerAfterPush.size();

            for (int k = 0; k < posPlayerAfterPush.size(); k++) {
                boolean highPriority = false;
                // normal AI, find an option that the player with the advanced AI
                // that target and position won't change after pushing the way card in it.
                if (!isAdvanced) {
                    if (cPlayers.get(k).getPlayerIndex().ordinal() != playerIndex.ordinal()
                            && players.get(k).getPlayerMode() == PlayerMode.COMPUTER_2) {
                        highPriority = posPlayerAfterPush.get(k).equals(posPlayersBeforePush.get(k))
                                && posTargetAfterPush.get(k).equals(posTargetsBeforePush.get(k));
                    }

                } else {
                    // advanced AI, find an option that the others players position will change after
                    // pushing
                    if (cPlayers.get(k).getPlayerIndex().ordinal() != playerIndex.ordinal()) {
                        highPriority = !posPlayerAfterPush.get(k).equals(posPlayersBeforePush.get(k))
                                || !posTargetAfterPush.get(k).equals(posTargetsBeforePush.get(k));
                    }
                }
                if (highPriority) {
                    Rotation rot = aiWork.getRotationList().get(i);
                    Coordinate push = aiWork.getPushCoordinates().get(i);
                    Coordinate posPlayer = aiWork.getPlayerToGoCoordinatesAfterPush().get(i);
                    bestOptionAi.addNewValues(push, rot, posPlayer);
                }
            }
        }

        // if there is no filtered option, return the old AI. The old AI is still the best
        return bestOptionAi.getPushCoordinates().isEmpty() ? aiWork : bestOptionAi;
    }

    /**
     * Get information to push the way card in. Will save the information in an instance of AiWork.
     * It will save all options with the same distance in the instance.
     * The method will try all options of pushing the free card into the way cards.
     * After pushing it will find the nearest distance from the player to the target. And will push this information, to the AiWork instance.
     * If there is new nearer distance, it will delete the old options and set the new distance and will add the options
     * with the same distance into the instance. We need all options to filter the best option.
     * When we need information for the advanced AI, the option with the most high priority is still 0, but we don't want options,
     * with the distance of 1. So it's better to get options with the distance of 2 than with 1.
     * Options with 2, there can be diagonal options to reach the target on the next turn.
     * When the normal AI is on turn, we search the nearest option.
     *
     * @param wayCards          - current way card
     * @param freeCard          - free card
     * @param player            - current player
     * @param blockedCoordinate - blocked Coordinate
     * @param isAdvanced        - is the AI advanced or normal
     * @return AiWork all options with the same distance
     */
    public AiWork getAiInformationToPushFreeCard(WayCard[][] wayCards, WayCard freeCard, Player player, Coordinate blockedCoordinate, boolean isAdvanced) {
        final int numberOfRotations = Rotation.values().length;
        int nearestDistance = Integer.MAX_VALUE;
        AiWork aiWork = new AiWork();
        // no access to the other players
        final List<Player> players = new LinkedList<>();
        players.add(new Player(player));
        Coordinate maybeTargetAfterPush;
        WayCard tempFreeCard = new WayCard(freeCard);

        // check every row to push the card in (horizontal)
        for (int rot = 0; rot < numberOfRotations; rot++) {
            for (int logical_x = FIRST_WAY_CARD; logical_x < SIZE_GAME_FIELD; logical_x += INDEX_LAST_WAY_CARD) {
                for (int logical_y = INDEX_FIRST_PUSH; (logical_y < SIZE_GAME_FIELD); logical_y = logical_y + 2) {
                    WayCard[][] dCopyWayCards = getDeepCopyOfWayCards(wayCards);
                    List<Player> dCopyPlayers = getDeepCopyOfPlayer(players);

                    Direction direction = logical_x == FIRST_WAY_CARD ? Direction.RIGHT : Direction.LEFT;
                    moveCardHorizontalGetFreeCard(direction, dCopyWayCards, tempFreeCard, logical_y);
                    updatePlayerPosAfterPushHorizontalGetRespawn(direction, dCopyPlayers, logical_y);
                    Player playerAfterPush = dCopyPlayers.get(0);
                    maybeTargetAfterPush = getTargetToGo(playerAfterPush, dCopyWayCards);

                    // don't kick the next target out.
                    // check the push coordinate with the blocked coordinate
                    if (!maybeTargetAfterPush.equals(FREE_CARD_POS)
                            && !blockedCoordinate.isEqual(logical_x, logical_y)) {

                        Coordinate newPlayerPosAfterPush = playerAfterPush.getPosition();
                        int x = newPlayerPosAfterPush.getX();
                        int y = newPlayerPosAfterPush.getY();
                        List<Coordinate> possibleWays = extractPossibleWays(getPossibleWays(dCopyWayCards, x, y));

                        for (Coordinate playerToGo : possibleWays) {

                            Coordinate intendPush = new Coordinate(logical_x, logical_y);
                            Rotation intendRotation = tempFreeCard.getRotation();
                            // when the advanced AI is on turn, the optimal distance 2, to get the maybe diagonal options
                            boolean advancedFlag = isAdvanced && playerToGo.distance(maybeTargetAfterPush) == 1;
                            // add also the other opportunities into the AI class to find the best solution when we use the advanced KI.
                            if (playerToGo.distance(maybeTargetAfterPush) == nearestDistance && !advancedFlag) {

                                aiWork.addNewValues(intendPush, intendRotation, playerToGo);
                            }

                            if (playerToGo.distance(maybeTargetAfterPush) < nearestDistance) {

                                advancedFlag = isAdvanced && playerToGo.distance(maybeTargetAfterPush) == 1;

                                if (!advancedFlag) {

                                    nearestDistance = playerToGo.distance(maybeTargetAfterPush);
                                    aiWork.setNewDistance(nearestDistance);
                                    aiWork.addNewValues(intendPush, intendRotation, playerToGo);
                                }
                            }
                        }
                    }
                }
            }

            // check every column to push the card vertical
            for (int logical_y = FIRST_WAY_CARD; logical_y < SIZE_GAME_FIELD; logical_y += INDEX_LAST_WAY_CARD) {
                for (int logical_x = INDEX_FIRST_PUSH; logical_x < SIZE_GAME_FIELD; logical_x = logical_x + 2) {
                    WayCard[][] dCopyWayCards = getDeepCopyOfWayCards(wayCards);
                    List<Player> dCopyPlayers = getDeepCopyOfPlayer(players);
                    Player playerAfterPush = dCopyPlayers.get(0);

                    Direction direction = logical_y == FIRST_WAY_CARD ? Direction.DOWN : Direction.UP;
                    moveCardVerticalAndGetNewFreeCard(direction, dCopyWayCards, tempFreeCard, logical_x);
                    updatePlayerPosAfterPushVerticalGetRespawn(direction, dCopyPlayers, logical_x);
                    maybeTargetAfterPush = getTargetToGo(playerAfterPush, dCopyWayCards);

                    // don't kick the next target out.
                    // check the push coordinate with the blocked coordinate
                    if (!maybeTargetAfterPush.equals(INVALID_POSITION)
                            && !blockedCoordinate.isEqual(logical_x, logical_y)) {
                        Coordinate newPlayerPosAfterPush = playerAfterPush.getPosition();
                        int x = newPlayerPosAfterPush.getX();
                        int y = newPlayerPosAfterPush.getY();
                        List<Coordinate> possibleWays = extractPossibleWays(getPossibleWays(dCopyWayCards, x, y));

                        for (Coordinate playerToGo : possibleWays) {
                            Coordinate intendPush = new Coordinate(logical_x, logical_y);
                            Rotation intendRotation = tempFreeCard.getRotation();
                            boolean advancedFlag = isAdvanced && playerToGo.distance(maybeTargetAfterPush) == 1;

                            // add also the other opportunities into the AI class to find the best solution when we use the advanced KI.
                            if (playerToGo.distance(maybeTargetAfterPush) == nearestDistance && !advancedFlag) {
                                aiWork.addNewValues(intendPush, intendRotation, playerToGo);
                            }
                            if (playerToGo.distance(maybeTargetAfterPush) < nearestDistance) {
                                advancedFlag = isAdvanced && playerToGo.distance(maybeTargetAfterPush) == 1;
                                if (!advancedFlag) {
                                    nearestDistance = playerToGo.distance(maybeTargetAfterPush);
                                    aiWork.setNewDistance(nearestDistance);
                                    aiWork.addNewValues(intendPush, intendRotation, playerToGo);
                                }
                            }
                        }
                    }
                }
            }
            tempFreeCard.rotateClockWise();
        }
        return aiWork;
    }

    /**
     * When the distance to the target is still the same,
     * we will use ths method, to get push coordinates for the normal AI.
     * The free card, will not be changed, it has his current rotation.
     * Will return the push coordinate for the normal AI.
     * It's possible that the AI will push the free card out.
     *
     * @param currentPosition - current position (before push)
     * @param currentNextTarget - next target (before push)
     * @param blockedCoordinate - the current blocked coordinate
     * @return the push coordinate
     */
    private Coordinate getPushCoordinateForNormalAi(Coordinate currentPosition, Coordinate currentNextTarget, Coordinate blockedCoordinate) {
        Coordinate pushFreeCardIn;
        // both on an unmovable row column, random
        if (!isMovable(currentPosition) && !isMovable(currentNextTarget)) {
            pushFreeCardIn = chooseRandomPush(blockedCoordinate);

            // currentPosition is not possible to move. Move treasure by pushing column
        } else if (!isMovable(currentPosition) && isRowMovable(currentNextTarget)) {

            Coordinate left = new Coordinate(INDEX_FIRST_WAY_CARD, currentNextTarget.getY());
            Coordinate right = new Coordinate(INDEX_LAST_WAY_CARD, currentNextTarget.getY());
            pushFreeCardIn = left.equals(blockedCoordinate) ? right : left;

            // push column of target
        } else if (!isMovable(currentPosition) && isColumnMovable(currentNextTarget)) {

            Coordinate down = new Coordinate(currentNextTarget.getX(), INDEX_FIRST_WAY_CARD);
            Coordinate up = new Coordinate(currentNextTarget.getX(), INDEX_LAST_WAY_CARD);
            pushFreeCardIn = up.equals(blockedCoordinate) ? down : up;
            // push row of player
        } else if (!isMovable(currentNextTarget) && isRowMovable(currentPosition)) {

            Coordinate left = new Coordinate(INDEX_FIRST_WAY_CARD, currentPosition.getY());
            Coordinate right = new Coordinate(INDEX_LAST_WAY_CARD, currentPosition.getY());
            pushFreeCardIn = left.equals(blockedCoordinate) ? right : left;
            // push column of current player
        } else if (!isMovable(currentNextTarget) && isColumnMovable(currentPosition)) {

            Coordinate down = new Coordinate(currentPosition.getX(), INDEX_FIRST_WAY_CARD);
            Coordinate up = new Coordinate(currentPosition.getX(), INDEX_LAST_WAY_CARD);
            pushFreeCardIn = up.equals(blockedCoordinate) ? down : up;

            // when column of player is movable, and the target is movable
            // push column of player
        } else if (isColumnMovable(currentPosition)) {

            Coordinate down = new Coordinate(currentPosition.getX(), INDEX_FIRST_WAY_CARD);
            Coordinate up = new Coordinate(currentPosition.getX(), INDEX_LAST_WAY_CARD);
            pushFreeCardIn = up.equals(blockedCoordinate) ? down : up;

            // push row of current player
        } else if (isRowMovable(currentPosition)) {

            Coordinate left = new Coordinate(INDEX_FIRST_WAY_CARD, currentPosition.getY());
            Coordinate right = new Coordinate(INDEX_LAST_WAY_CARD, currentPosition.getY());
            pushFreeCardIn = left.equals(blockedCoordinate) ? right : left;

            // push column of target
        } else if (isColumnMovable(currentNextTarget)) {

            Coordinate down = new Coordinate(currentNextTarget.getX(), INDEX_FIRST_WAY_CARD);
            Coordinate up = new Coordinate(currentNextTarget.getX(), INDEX_LAST_WAY_CARD);
            pushFreeCardIn = up.equals(blockedCoordinate) ? down : up;

            // push row of target
        } else if (isRowMovable(currentNextTarget)) {

            Coordinate left = new Coordinate(INDEX_FIRST_WAY_CARD, currentNextTarget.getY());
            Coordinate right = new Coordinate(INDEX_LAST_WAY_CARD, currentNextTarget.getY());
            pushFreeCardIn = left.equals(blockedCoordinate) ? right : left;

        } else {
            throw new RuntimeException();
        }
        return pushFreeCardIn;
    }

    /**
     * Will calculate a random push coordinate.
     * @param blockedCoordinate - current blocked coordinate.
     * @return random push coordinate
     */
    private Coordinate chooseRandomPush(Coordinate blockedCoordinate) {
        Direction direction;
        Coordinate click = blockedCoordinate;
        int coordinate;
        while (click.equals(blockedCoordinate)) {
            direction = Direction.values()[new Random().nextInt(Direction.values().length)];
            coordinate = 2 * (new Random().nextInt(SIZE_GAME_FIELD / 2)) + 1;
            switch (direction) {
                case UP:
                    click = new Coordinate(coordinate, INDEX_LAST_WAY_CARD);
                    break;
                case RIGHT:
                    click = new Coordinate(INDEX_FIRST_WAY_CARD, coordinate);
                    break;
                case DOWN:
                    click = new Coordinate(coordinate, INDEX_FIRST_WAY_CARD);
                    break;
                case LEFT:
                    click = new Coordinate(INDEX_LAST_WAY_CARD, coordinate);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return click;
    }

    /**
     * Is the coordinate movable
     * @param cor - coordinate to check
     * @return boolean
     */
    private boolean isMovable(Coordinate cor) {
        return isRowMovable(cor) || isColumnMovable(cor);
    }

    /**
     * Is the row of the coordinate movable
     * @param cor - coordinate to check
     * @return boolean
     */
    private boolean isRowMovable(Coordinate cor) {
        return cor.getY() % 2 != 0;
    }

    /**
     * Is the column of the coordinate movable
     * @param cor - coordinate to check
     * @return boolean
     */
    private boolean isColumnMovable(Coordinate cor) {
        return cor.getX() % 2 != 0;
    }

    /**
     * Will move the player to the shortest distance,
     * to the target. When the target is the free card it will find the shortest distance
     * to (-1,-1).
     */
    private void doAiWorkMovePlayerToShortestDistance() {
        Coordinate toMove = null;
        int tempDistance = Integer.MAX_VALUE;
        Coordinate target = getTargetToGo(currentTurn, wayCards);
        List<Coordinate> coordinates = extractPossibleWays(currentPossibleWays);

        for (Coordinate cor : coordinates) {
            if (cor.distance(target) < tempDistance) {
                tempDistance = cor.distance(target);
                toMove = new Coordinate(cor);
            }
        }
        moveCurrentPlayerTo(toMove);
    }

    /**
     * When an option of the AiWork instance is used,
     * we move the player to the position of the used option.
     * Otherwise, we move the player to the shortest distance to next target.
     *
     */
    private void doAiWorkMovePlayer() {
        if (aiWork.getUsedCoordinateFlag()) {
            Coordinate pos = aiWork.getPlayerToGoCoordinatesAfterPush().get(aiWork.getUsedOption());
            moveCurrentPlayerTo(pos);
        } else {
            doAiWorkMovePlayerToShortestDistance();
        }
    }


    /**
     * Will copy the way cards
     * @param wayCards to copy
     * @return copy of way cards
     */
    static WayCard[][] getDeepCopyOfWayCards(WayCard[][] wayCards) {
        WayCard[][] deepCopyWayCard = new WayCard[wayCards.length][];
        for (int x = 0; x < wayCards.length; x++) {
            deepCopyWayCard[x] = new WayCard[wayCards[x].length];
            for (int y = 0; y < wayCards[x].length; y++) {
                deepCopyWayCard[x][y] = new WayCard(wayCards[x][y]);
            }
        }
        return deepCopyWayCard;
    }

    /**
     * Deep copy of a player list
     *
     * @param players to copy
     * @return copied players
     */
    static List<Player> getDeepCopyOfPlayer(List<Player> players) {
        List<Player> player = new LinkedList<>();
        for (Player p : players) {
            player.add(new Player(p));
        }
        return player;
    }

    /**
     * Get the target to go
     * @param player player where to go
     * @param wayCards way cards
     * @return the coordinate of next target
     */
    Coordinate getTargetToGo(Player player, WayCard[][] wayCards) {
        Coordinate target;
        if (player.isFinishedWithCollectingTreasures()) {
            target = PLAYER_START_COORDINATE[player.getPlayerIndex().ordinal()];
        } else {
            target = findPositionOfTreasure(player.getNextTreasure(), wayCards);
        }

        return target;
    }

    /**
     * Get called from the gui, after moving the player.
     * We set the next turn.
     * We have to check if the current player found the treasure by moving his position.
     * The new position is already set to the current player!
     */

    public void setStateOneCheckTreasureAfterMoving() {
        if (hasWon(currentTurn)) {
            winningSituation();
        } else {
            checkPlayerFoundTreasure(currentTurn);
            setNextPlayer();
            setStateOnePushCardUpdatePossibleWays();
            switch (currentTurn.getPlayerMode()) {
                case HUMAN:
                    break;
                case COMPUTER_1:
                    doAiWorkPushCardNormal();
                    break;
                case COMPUTER_2:
                    doAiWorkPushCardAdvanced();
                    break;
            }
        }
    }

    /**
     * Set winning situation
     */
    private void winningSituation() {
        gui.displayHasWon(currentTurn.getPlayerIndex());
        this.isInputAllowed = false;
        logger.logHasWon(currentTurn);
    }

    /**
     * Get called in the gui, after pushing a row or column.
     * It could be that a respawn player has found a new treasure
     * at the new respawned position. This has to be checked.
     */
    public void setStateTwoCheckTreasureRespawn() {
        // Check after respawn
        players.forEach(this::checkPlayerFoundTreasure);
        setStateTwoUpdatePossibleWays();

        switch (currentTurn.getPlayerMode()) {
            case HUMAN:
                break;
            case COMPUTER_1:
            case COMPUTER_2:
                doAiWorkMovePlayer();
                break;
        }
    }

    /**
     * Check player found treasure. When yes than remove.
     *
     * @param player - player
     */
    private void checkPlayerFoundTreasure(Player player) {
        int x = player.getPosition().getX();
        int y = player.getPosition().getY();
        if (!player.isFinishedWithCollectingTreasures() && checkFindTreasure(player, x, y)) {
            removeFirstTreasureFromPlayerAndGameField(player, x, y);
            gui.removeTreasureCardUpdateTreasuresLeft(x, y, player.getPlayerIndex(), player.getTreasures().size());
        }
    }
    /**
     * Set the next player.
     */
    private void setNextPlayer() {
        currentTurn = players.get((players.indexOf(currentTurn) + 1) % players.size());
    }

    /**
     * WIll set the state of pushing a card
     */
    private void setStateOnePushCardUpdatePossibleWays() {
        isMoveWayCardState = true;
        isMovePlayerState = false;
        isInputAllowed = true;
        isRotationAllowedState = true;
        logger.logCurrentTurnStateOne(currentTurn);
        gui.displayShowCurrentPlayer(currentTurn);
        gui.displayActionPushCard(currentTurn.getPlayerIndex());

        Coordinate currPlayerPos = currentTurn.getPosition();
        currentPossibleWays = getPossibleWays(wayCards, currPlayerPos.getX(), currPlayerPos.getY());
    }

    /**
     * Set state two and will update the possible ways.
     */

    void setStateTwoUpdatePossibleWays() {
        isMoveWayCardState = false;
        isMovePlayerState = true;
        isInputAllowed = true;
        isRotationAllowedState = false;
        Coordinate currPlayerPos = currentTurn.getPosition();
        currentPossibleWays = getPossibleWays(wayCards, currPlayerPos.getX(), currPlayerPos.getY());

        gui.displayActionMovePlayer(currentTurn.getPlayerIndex());
    }


    /**
     * Show if current state is on state one.
     * In state one we have to push the free way card in the field.
     * It's possible to rotate the way card
     *
     * @return state one
     */
    private boolean isStatePushCard() {
        return isMoveWayCardState && !isMovePlayerState && isInputAllowed && isRotationAllowedState;
    }


    /**
     * Show if current state is on state two.
     * In state two we have to move or move not the player
     *
     * @return state two
     */
    private boolean isStateMovePlayer() {
        return !isMoveWayCardState && isMovePlayerState && isInputAllowed && !isRotationAllowedState && currentPossibleWays != null;
    }


    /**
     * Will handle the pushing Row or Column
     * @param logical_x - position
     * @param logical_y - position
     * @param dir - direction
     */
    void pushRowColumn(int logical_x, int logical_y, Direction dir) {
        switch (dir) {
            case RIGHT:
                handlePushToRight(logical_y);
                break;
            case LEFT:
                handlePushToLeft(logical_y);
                break;
            case UP:
                handlePushToUp(logical_x);
                break;
            case DOWN:
                handlePushToDown(logical_x);
                break;
            default:
                throw new RuntimeException();
        }
    }

    /**
     * Will push the way card down at x_logical.
     * The column will push from down to up.
     * Will log.
     * @param x_logical - position
     */
    private void handlePushToUp(int x_logical) {
        logger.logPlayerPushedWayCardsVerticalFromDownToUp(x_logical, INDEX_LAST_ROW_OR_COLUMN_TO_PUSH, currentTurn, freeCard);

        if (!blockedCoordinate.equals(INVALID_POSITION)) {
            Direction dir = getPushDirection(blockedCoordinate);
            gui.setArrowVisibility(dir, blockedCoordinate, true);
        }

        blockedCoordinate = calcNewBlocked(Direction.UP, x_logical);
        isInputAllowed = false;
        this.freeCard = moveCardVerticalAndGetNewFreeCard(Direction.UP, wayCards, freeCard, x_logical);
        Map<PlayerIndex, Coordinate> respawnCoordinates = updatePlayerPosAfterPushVerticalGetRespawn(Direction.UP, players, x_logical);
        // will set setStateTwo to move player and will check treasures after respawn. It could be that a player respawn to a treasure
        gui.animatePushVerticalDownToUpAndRespawnPlayers(x_logical, respawnCoordinates, this);
    }

    /**
     * Will push the way card up at x_logical.
     * The column will push from up to down
     * Will log.
     * @param x_logical - position
     */
    private void handlePushToDown(int x_logical) {
        logger.logPlayerPushedWayCardsVerticalFromUpToDown(x_logical, INDEX_FIRST_ROW_OR_COLUMN_TO_PUSH, currentTurn, freeCard);

        if (!blockedCoordinate.equals(INVALID_POSITION)) {
            Direction dir = getPushDirection(blockedCoordinate);
            gui.setArrowVisibility(dir, blockedCoordinate, true);
        }

        blockedCoordinate = calcNewBlocked(Direction.DOWN, x_logical);
        isInputAllowed = false;
        this.freeCard = moveCardVerticalAndGetNewFreeCard(Direction.DOWN, wayCards, freeCard, x_logical);
        Map<PlayerIndex, Coordinate> respawnCoordinates = updatePlayerPosAfterPushVerticalGetRespawn(Direction.DOWN, players, x_logical);
        // will set setStateTwo to move player and will check treasures after respawn. It could be that a player respawn to a treasure
        gui.animatePushVerticalUpToDownAndRespawnPlayers(x_logical, respawnCoordinates, this);
    }

    /**
     * Will push the way card left at y_logical.
     * The row will push from left to right
     * Will log.
     * @param y_logical - position
     */
    private void handlePushToLeft(int y_logical) {
        logger.logPlayerPushedWayCardsHorizontalFromRightToLeft(INDEX_LAST_ROW_OR_COLUMN_TO_PUSH, y_logical, currentTurn, freeCard);

        if (!blockedCoordinate.equals(INVALID_POSITION)) {
            Direction dir = getPushDirection(blockedCoordinate);
            gui.setArrowVisibility(dir, blockedCoordinate, true);
        }

        blockedCoordinate = calcNewBlocked(Direction.LEFT, y_logical);
        isInputAllowed = false;

        this.freeCard = moveCardHorizontalGetFreeCard(Direction.LEFT, wayCards, freeCard, y_logical);
        Map<PlayerIndex, Coordinate> respawnCoordinates = updatePlayerPosAfterPushHorizontalGetRespawn(Direction.LEFT, players, y_logical);
        // will set setStateTwo to move player and will check treasures after respawn. It could be that a player respawn to a treasure
        gui.animatePushHorizontalRightToLeftAndRespawnPlayers(y_logical, respawnCoordinates, this);
    }

    /**
     * Will push the way card right at y_logical.
     * The row will push from right to left
     * Will log.
     * @param y_logical - position
     */
    private void handlePushToRight(int y_logical) {
        logger.logPlayerPushedWayCardsHorizontalFromLeftToRight(INDEX_FIRST_ROW_OR_COLUMN_TO_PUSH, y_logical, currentTurn, freeCard);
        isInputAllowed = false;

        if (!blockedCoordinate.equals(INVALID_POSITION)) {
            Direction dir = getPushDirection(blockedCoordinate);
            gui.setArrowVisibility(dir, blockedCoordinate, true);
        }

        blockedCoordinate = calcNewBlocked(Direction.RIGHT, y_logical);
        freeCard = moveCardHorizontalGetFreeCard(Direction.RIGHT, wayCards, freeCard, y_logical);
        Map<PlayerIndex, Coordinate> respawnCoordinates = updatePlayerPosAfterPushHorizontalGetRespawn(Direction.RIGHT, players, y_logical);
        // will set setStateTwo to move player and will check treasures after respawn. It could be that a player respawn to a treasure
        gui.animatePushHorizontalLeftToRightAndRespawnPlayers(y_logical, respawnCoordinates, this);
    }

    /**
     * Will calculate the blocked coordinate,
     * after pushing the free card
     * @param dir - direction of pushing
     * @param logical_pos position
     * @return the new blocked coordinate
     */
    private Coordinate calcNewBlocked(Direction dir, int logical_pos) {
        Coordinate cord;
        switch (dir) {
            case UP:
                cord = new Coordinate(logical_pos, INDEX_FIRST_WAY_CARD);
                break;
            case DOWN:
                cord = new Coordinate(logical_pos, INDEX_LAST_WAY_CARD);
                break;
            case LEFT:
                cord = new Coordinate(INDEX_FIRST_WAY_CARD, logical_pos);
                break;
            case RIGHT:
                cord = new Coordinate(INDEX_LAST_WAY_CARD, logical_pos);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return cord;
    }

    /**
     * Handles the input of the controller. Also maps the clicking position with
     * the logical put free card position.
     * It is not necessary to map in other parts of the program.
     * The gui will map itself from the logic, because the logic do not care about the gui.
     * And signalise only position of the way cards.
     *
     * @param x_fx - exact coordinate from the controller - maybe necessary to map to a logical controller_x
     * @param y_fx - exact coordinate from the controller - maybe necessary to map to a logical controller_y
     */

    public void clickedLeftOnArrowHandlingCoordinates(int x_fx, int y_fx) {

        Coordinate wrapper = new Coordinate(x_fx, y_fx);
        Direction direction = getPushDirectionAndMapPositions(wrapper);
        if (direction == null) {
            throw new RuntimeException("something went wrong");
        }
        int logical_x = wrapper.getX();
        int logical_y = wrapper.getY();
        if (isStatePushCard() && !blockedCoordinate.isEqual(logical_x, logical_y)) {
            pushRowColumn(logical_x, logical_y, direction);
        }
    }

    /**
     * Only used for clickedLeftOnArrowHandlingCoordinates.
     * clickedLeftOnArrowHandlingCoordinates is only get called when a player is clicking the gui.
     * For the doAiWork() we call other methods, which can be also used by JUNIT tests.
     *
     * @param cor wrapped x - y will be changed in this method.
     * @return Direction or null if null then exception
     */
    Direction getPushDirectionAndMapPositions(Coordinate cor) {
        Direction dir = null;
        // push from left to right
        if (cor.getX() == INDEX_FIRST_ROW_OR_COLUMN_TO_PUSH) {
            dir = Direction.RIGHT;
            cor.setY(cor.getY() - OFF_SET);
            // push from left to right
        } else if (cor.getX() == INDEX_LAST_ROW_OR_COLUMN_TO_PUSH) {
            dir = Direction.LEFT;
            cor.setX(cor.getX() - 2 * OFF_SET);
            cor.setY(cor.getY() - OFF_SET);
            // push from up to down
        } else if (cor.getY() == INDEX_FIRST_ROW_OR_COLUMN_TO_PUSH) {
            dir = Direction.DOWN;
            cor.setX(cor.getX() - OFF_SET);
            // push from down to up
        } else if (cor.getY() == INDEX_LAST_ROW_OR_COLUMN_TO_PUSH) {
            dir = Direction.UP;
            cor.setX(cor.getX() - OFF_SET);
            cor.setY(cor.getY() - 2 * OFF_SET);
        }
        return dir;
    }

    /**
     * Get called with logical position, where the way card should get pushed in
     * When the logical does not map, it will throw a RunTimeException
     *
     * @param logical_pos - position to push free card in
     * @return get the push direction.
     */
    Direction getPushDirection(Coordinate logical_pos) {
        Direction dir;
        // push from left to right
        if (logical_pos.getX() == INDEX_FIRST_WAY_CARD) {
            dir = Direction.RIGHT;
            // push from right to left
        } else if (logical_pos.getX() == INDEX_LAST_WAY_CARD) {
            dir = Direction.LEFT;
            // push from up to down
        } else if (logical_pos.getY() == INDEX_FIRST_WAY_CARD) {
            dir = Direction.DOWN;
            // push from down to up
        } else if (logical_pos.getY() == INDEX_LAST_WAY_CARD) {
            dir = Direction.UP;
        } else {
            // method should only get called with valid coordinates
            throw new RuntimeException();
        }
        return dir;
    }

    /**
     * Handle the mouse click on way cards from the player,
     * map to the coordinates to logical coordinates, by subtracting the off set
     *
     * @param x_fx - clicked coordinate, map to logical x
     * @param y_fx - clicked coordinate, map to logical y
     */
    public void clickedLeftOnWayCard(int x_fx, int y_fx) {
        // checking which state it is, rotate free card
        if (isStatePushCard() && x_fx == X_FX_FREE_CARD_POS && y_fx == Y_FX_FREE_CARD_POS) {
            rotateFreeCard();
            // TODO maybe do an alert
        } else if (isStatePushCard()) {
            // display that we are on state one
            gui.displayError(IS_STATE_ONE);
        } else if (isStateMovePlayer() && checkWayCardRange(x_fx - OFF_SET, y_fx - OFF_SET)) {
            Coordinate clicked = new Coordinate(x_fx - OFF_SET, y_fx - OFF_SET);
            // move player to the possible position
            if (extractPossibleWays(currentPossibleWays).contains(clicked)) {
                moveCurrentPlayerTo(clicked);
            }
        }
    }

    /**
     * Move player to new position
     * The gui will animate the movement of to the new position
     * Don't allow input while, move to the new position
     * This method get also called by the AI
     *
     * @param newPos - the new position
     */

    private void moveCurrentPlayerTo(Coordinate newPos) {
        assert currentPossibleWays != null && extractPossibleWays(currentPossibleWays).contains(newPos);
        logger.logMovePlayerTo(currentTurn, currentTurn.getPosition(), newPos);
        currentTurn.setPosition(newPos);
        this.isInputAllowed = false;
        List<Coordinate> shortWay = getShortestWayTo(currentPossibleWays, newPos);
        gui.animateMovePlayerFromTo(currentTurn.getPlayerIndex(), shortWay, this);
        // disable the highlight, after clicking, important when a human player is on turn
        this.gui.highLightDisable(newPos);
    }

    /**
     * Check the if the player has won
     * No treasures to collect, and player is on start position
     *
     * @param player - player to check
     * @return has won
     */
    private boolean hasWon(Player player) {
        return player.isFinishedWithCollectingTreasures() && player.getPosition().equals(PLAYER_START_COORDINATE[player.getPlayerIndex().ordinal()]);
    }

    /**
     * Removes the treasure from the game field, and from the player.
     * Logs the player found treasure
     *
     * @param player    - player remove the first treasure
     * @param x_logical - position on way card
     * @param y_logical - position on way card
     */
    private void removeFirstTreasureFromPlayerAndGameField(Player player, int x_logical, int y_logical) {
        assert player.hasTreasure();
        assert player.getNextTreasure() == wayCards[x_logical][y_logical].getTreasure();
        Treasure treasure = player.removeFirstTreasure();
        wayCards[x_logical][y_logical].setTreasure(Treasure.NONE);
        logger.logPlayerCollectedTreasure(treasure, player);
        if (player.hasTreasure()) {
            logger.logPlayerHasTreasures(player);
        } else {
            // all treasures found, go to start position
            logger.logPlayerGoToStartPosition(player, PLAYER_START_COORDINATE[player.getPlayerIndex().ordinal()]);
        }
    }

    /**
     * Get called after moving to a new position.
     * Check if player found a  treasure
     *
     * @param player    - player to check
     * @param x_logical - the position
     * @param y_logical - the position
     * @return boolean
     */
    private boolean checkFindTreasure(Player player, int x_logical, int y_logical) {
        return player.getNextTreasure() == wayCards[x_logical][y_logical].getTreasure();
    }

    /**
     * Disable lighting, after mouse exited the way card
     *
     * @param x_fx - real coordinate, map to logical position
     * @param y_fx - real coordinate, map to logical position
     */
    public void mouseExitedGridOnWayCard(int x_fx, int y_fx) {
        // when would use isStateTwo(), after clicking the new oo
        if (isInputAllowed && checkWayCardRange(x_fx - OFF_SET, y_fx - OFF_SET)) {
            Coordinate pos_logical = new Coordinate(x_fx - OFF_SET, y_fx - OFF_SET);
            gui.highLightDisable(pos_logical);
        }
    }

    /**
     * Handle mouse entered way card, map to logical coordinate!
     * Highlight gui
     *
     * @param x_fx - real coordinate, map to logical position
     * @param y_fx - real coordinate, map to logical position
     */
    public void mouseEnteredGridOnWayCard(int x_fx, int y_fx) {
        if (isInputAllowed && gameHasStarted && checkWayCardRange(x_fx - OFF_SET, y_fx - OFF_SET)) {
            Coordinate pos_logical = new Coordinate(x_fx - OFF_SET, y_fx - OFF_SET);
            if (extractPossibleWays(currentPossibleWays).contains(pos_logical)) {
                gui.highLightAvailable(pos_logical);
            } else {
                gui.highLightNotAvailable(pos_logical);
            }
        }
    }

    /**
     * Check validity of the coordinate
     *
     * @param x_logical - pos
     * @param y_logical - pos
     * @return is the range valid
     */
    private boolean checkWayCardRange(int x_logical, int y_logical) {
        return x_logical >= 0 && x_logical <= INDEX_LAST_WAY_CARD && y_logical >= 0 && y_logical <= INDEX_LAST_WAY_CARD;
    }

    /**
     * Checks for a coordinate if a neighbour exist
     *
     * @param pos_logical position to check
     * @return does a neighbour exist?
     */
    private boolean hasLeftOrUpNeighbour(int pos_logical) {
        return pos_logical > FIRST_WAY_CARD;
    }

    /**
     * Checks for a coordinate if a neighbour right or down exist.
     *
     * @param pos_logical position to check
     * @return does a neighbour exist?
     */
    private boolean hasRightOrDownNeighbour(int pos_logical) {
        return pos_logical < INDEX_LAST_WAY_CARD;
    }

    /**
     * We use https://en.wikipedia.org/wiki/Breadth-first_search
     * Breadth first search to search possible ways from a start point.
     * We save them in a list with Coordinates.
     * Returns a List with pairs: Pair(from, to), this is necessary to for the gui,
     * to find the path from start to destination
     *
     * @param x_logical - possible ways from x
     * @param y_logical - possible ways from y
     * @param wayCards  - way cards
     * @return LinkedList<Coordinate, Coordinate> possible coordinates, with origin
     */
    private List<Pair<Coordinate, Coordinate>> getPossibleWays(WayCard[][] wayCards, int x_logical, int y_logical) {
        assert x_logical >= FIRST_WAY_CARD && x_logical <= INDEX_LAST_WAY_CARD;
        assert y_logical >= FIRST_WAY_CARD && y_logical <= INDEX_LAST_WAY_CARD;

        Coordinate start = new Coordinate(x_logical, y_logical);
        // start position, where to search
        // the start position, is the only coordinate where the from and to coordinates are the same
        Pair<Coordinate, Coordinate> startFrom = new Pair<>(start, start);
        List<Coordinate> checkCoordinates = new LinkedList<>();

        // necessary to avoid loops , because of using breath depth search we add only shortest way coordinate into the list
        List<Coordinate> possibleWays = new LinkedList<>();
        // from, to pair
        List<Pair<Coordinate, Coordinate>> possibleWaysFrom = new LinkedList<>();
        // add the start position, and check all possible. after adding all possible way, delete start position and
        // take the next coordinate in the list to check
        checkCoordinates.add(start);
        possibleWays.add(start);
        possibleWaysFrom.add(startFrom);

        while (!checkCoordinates.isEmpty()) {
            // check the first element in the list
            Coordinate toCheck = checkCoordinates.get(0);
            WayCard wayToCheck = wayCards[toCheck.getX()][toCheck.getY()];

            // check left
            if (wayToCheck.isLeftPossibleToGo()
                    && hasLeftOrUpNeighbour(toCheck.getX())) {
                int xCord = toCheck.getX() - 1;
                int yCord = toCheck.getY();
                Coordinate possibleWay = new Coordinate(xCord, yCord);
                WayCard neighbor = wayCards[xCord][yCord];

                // check the right of the possible neighbor
                if (neighbor.isRightPossibleToGo() && !possibleWays.contains(possibleWay)) {
                    checkCoordinates.add(possibleWay);
                    possibleWays.add(possibleWay);
                    Pair<Coordinate, Coordinate> fromTo = new Pair<>(possibleWay, toCheck);
                    possibleWaysFrom.add(fromTo);
                }
            }

            // check right
            if (wayToCheck.isRightPossibleToGo()
                    && hasRightOrDownNeighbour(toCheck.getX())) {
                int xCord = toCheck.getX() + 1;
                int yCord = toCheck.getY();
                Coordinate possibleWay = new Coordinate(xCord, yCord);
                WayCard neighbor = wayCards[xCord][yCord];

                // check the left of the possible neighbor
                if (neighbor.isLeftPossibleToGo() && !possibleWays.contains(possibleWay)) {
                    checkCoordinates.add(possibleWay);
                    possibleWays.add(possibleWay);
                    Pair<Coordinate, Coordinate> fromTo = new Pair<>(possibleWay, toCheck);
                    possibleWaysFrom.add(fromTo);

                }
            }

            // check up
            if (wayToCheck.isUpPossibleToGo()
                    && hasLeftOrUpNeighbour(toCheck.getY())) {
                int xCord = toCheck.getX();
                int yCord = toCheck.getY() - 1;
                Coordinate possibleWay = new Coordinate(xCord, yCord);
                WayCard neighbor = wayCards[xCord][yCord];
                // add the coordinate to the list
                if (neighbor.isDownPossibleToGo() && !possibleWays.contains(possibleWay)) {
                    checkCoordinates.add(possibleWay);
                    possibleWays.add(possibleWay);
                    Pair<Coordinate, Coordinate> fromTo = new Pair<>(possibleWay, toCheck);
                    possibleWaysFrom.add(fromTo);
                }
            }

            // check down
            if (wayToCheck.isDownPossibleToGo()
                    && hasRightOrDownNeighbour(toCheck.getY())) {
                int xCord = toCheck.getX();
                int yCord = toCheck.getY() + 1;
                Coordinate possibleWay = new Coordinate(xCord, yCord);
                WayCard neighbor = wayCards[xCord][yCord];
                // add the coordinate to the list
                if (neighbor.isUpPossibleToGo() && !possibleWays.contains(possibleWay)) {
                    checkCoordinates.add(possibleWay);
                    possibleWays.add(possibleWay);
                    Pair<Coordinate, Coordinate> fromTo = new Pair<>(possibleWay, toCheck);
                    possibleWaysFrom.add(fromTo);

                }
            }
            // remove the coordinate, after checking
            checkCoordinates.remove(HEAD_OF_LIST);
        }

        // don't remove start from possible ways;
        return possibleWaysFrom;
    }


    /**
     * Will highlight next treasure,
     * when player is pressing H
     */
    public void handleKeyBoardPressedH() {
        if (gameHasStarted && currentTurn.hasTreasure() && isInputAllowed) {
            Coordinate pos = findPositionOfTreasure(currentTurn.getNextTreasure(), wayCards);
            if (!pos.equals(FREE_CARD_POS)) {
                gui.highLightTreasureCard(pos);
            } else {
                gui.highLightFreeWayCard();
            }
        }
    }

    /**
     * Will disable highlighting when player released H
     */
    public void handleKeyBoardReleasedH() {
        if (gameHasStarted && currentTurn.hasTreasure()) {
            Coordinate pos = findPositionOfTreasure(currentTurn.getNextTreasure(), wayCards);
            if (!pos.equals(FREE_CARD_POS)) {
                gui.highLightDisable(pos);
            } else {
                gui.highLightDisableFreeWayCard();
            }
        }
    }

    /**
     * Will find the position of the treasure.
     * @param toFind - treasure to find
     * @param wayCards - current position
     * @return coordinate of the treasure
     */
    private Coordinate findPositionOfTreasure(Treasure toFind, WayCard[][] wayCards) {
        boolean found = false;
        // very important: check this for the gui
        // if you make changes here, you have to change it also in the gui
        // maybe we can handle this otherwise
        Coordinate posOfTreasure = FREE_CARD_POS;

        for (int x = 0; x < wayCards.length && !found; x++) {
            for (int y = 0; y < wayCards.length && !found; y++) {
                found = toFind == wayCards[x][y].getTreasure();
                if (found) {
                    posOfTreasure = new Coordinate(x, y);
                }
            }
        }
        return posOfTreasure;
    }

    /**
     * Throws exception when failed json file
     * @param ex exception to display
     */
    public void handleErrorJSON(Exception ex) {
        gui.disableAllHighLighting();
        if (ex instanceof NullPointerException) {
            gui.displayError(new IllegalInputException("No file selected try again!"));
        } else {
            gui.displayError(ex);
        }
    }

    /**
     * Load game from file
     * @param url - path
     * @return null when load game is not succeed
     * @throws IllegalInputException - GameStateFactory will throw exception.
     */
    public GameField loadGame(String url) throws IllegalInputException {

        // when game is not started, accept loading files.
        // when game has started only accept loading when current turn is human or current turn has won
        if (!hasWon(currentTurn)
                && (currentTurn.getPlayerMode() != PlayerMode.HUMAN
                && gameHasStarted)) {
            throw COMPUTER_TURN_EXCEPTION;
        }

        boolean exceptionFlag = false;
        WayCard[][] wayCards = null;
        WayCard freeCard = null;
        Coordinate blockedCoordinate = null;
        List<Player> players = null;
        Player currentTurn = null;
        gui.disableAllHighLighting();
        try {
            // build the state
            GameStateFactory state = ParserJSON.loadGameState(url);
            // DEBUG System.out.println(url);
            wayCards = new WayCard[SIZE_OF_WAY_CARD_FIELD][SIZE_OF_WAY_CARD_FIELD];

            for (int x = 0; x < SIZE_OF_WAY_CARD_FIELD; x++) {
                for (int y = 0; y < SIZE_OF_WAY_CARD_FIELD; y++) {
                    wayCards[x][y] = state.buildWayCard(x, y);
                }
            }

            freeCard = state.buildFreeCard();
            blockedCoordinate = state.buildBlockedCoordinate(INDEX_FIRST_WAY_CARD, INDEX_LAST_WAY_CARD);
            players = state.buildPlayers();
            PlayerIndex idxCurrPlayer = state.buildCurrentPlayerIndex();
            currentTurn = players.get(0);
            int idx = 1;
            while (currentTurn.getPlayerIndex() != idxCurrPlayer) {
                currentTurn = players.get(idx++);
            }
        } catch (Exception e) {
            gui.displayError(e);
            exceptionFlag = true;
        }

        // if an exception is happening, don't init the game
        if (!exceptionFlag) {
            for (int x = 0; x < SIZE_OF_WAY_CARD_FIELD; x++) {
                for (int y = 0; y < SIZE_OF_WAY_CARD_FIELD; y++) {
                    WayCard oldWayCard = this.wayCards[x][y];
                    boolean hasTreasure = oldWayCard.hasTreasure();
                    gui.deleteWayCardImage(x, y, hasTreasure);
                }
            }
            gui.deleteFreeCardImage(this.freeCard.hasTreasure());
            this.logger = new TextLogger();
            this.wayCards = wayCards;

            // make gui arrow visible again, before changing the blocked coordinate
            if (this.blockedCoordinate != null && !this.blockedCoordinate.equals(INVALID_POSITION)) {
                Direction dir = getPushDirection(this.blockedCoordinate);
                gui.setArrowVisibility(dir, this.blockedCoordinate, true);
            }

            this.blockedCoordinate = blockedCoordinate;

            // make gui arrow invisible
            if (this.blockedCoordinate != null && !this.blockedCoordinate.equals(INVALID_POSITION)) {
                Direction dir = getPushDirection(this.blockedCoordinate);
                gui.setArrowVisibility(dir, this.blockedCoordinate, false);
            }

            this.freeCard = freeCard;
            this.players.forEach(x -> gui.removePlayer(x.getPlayerIndex(), x.getPosition()));

            this.players = players;
            this.currentTurn = currentTurn;
            gui.textAndButtonsToDefault();
            initializeGuiWithPlayCards();
            gui.textAndButtonsToDefault();
            for (Player p : players) {
                gui.initializeAndShowPlayer(p);
            }
            gui.showBtnStartGame();
            Coordinate currPlayerPos = currentTurn.getPosition();
            currentPossibleWays = getPossibleWays(wayCards, currPlayerPos.getX(), currPlayerPos.getY());
            isInputAllowed = false;
        }
        // for test
        return this;
    }


    /**
     * Save game to a JSON object
     *
     * @param path path to save
     */

    public void clickedSaveGame(String path) throws IOException {
        // when game is not started, accept saving file.
        // when game has started only accept saving when current turn is human or current turn has won
        if (!hasWon(currentTurn)
                && currentTurn.getPlayerMode() != PlayerMode.HUMAN
                && gameHasStarted) {
            gui.displayError(COMPUTER_TURN_EXCEPTION);
        } else {
            gui.disableAllHighLighting();
            // build the state
            GameStateFactory state = GameStateFactory.buildState(
                    wayCards,
                    freeCard,
                    blockedCoordinate,
                    players,
                    currentTurn.getPlayerIndex());
            ParserJSON.saveGame(path ,state);
        }
    }

    /**
     * Set Duration for player moving
     * @param index - index of duration
     */
    public void setDurationForPlayer(int index) {
        this.gui.setDurationPlayerMovementAnimation(index);
    }

    /**
     * Handle the animation speed
     * Input is not allowed during animation speed
     *
     * @param index - which animation speed get
     */
    public void setDurationForPushCardGui(int index) {
        this.gui.setDurationPushCardAnimation(index);
    }

}