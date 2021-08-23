package logic;

import java.util.LinkedList;
import java.util.List;

/**
 * Do json work and factory class.
 * Will build states, and throw exception when the loaded JSON file is not correct
 * @author Miwand Baraksaie
 */

public class GameStateFactory {
    /**
     * These instances will be created by the parser
     */
    private Field[][] field;
    private FreeWayCard freeWayCard;
    private int currentPlayer;
    private GamePlayer[] players;
    /**
     * Exception which are thrown when the json input is wrong and contains errors
     */
    private static final IllegalInputException PLAYER_SEARCHING_SAME_TREASURE = new IllegalInputException("Some players are searching the same treasure!");
    private static final IllegalInputException TREASURE_OF_PLAYER_IS_NOT_ON_FIELD = new IllegalInputException("Player has treasures, which are not on way card!");
    private static final IllegalInputException FREE_CARD_EXCEPTION = new IllegalInputException("Check free card input!");
    private static final IllegalInputException WAY_CARD_EXCEPTION = new IllegalInputException("Check your way cards!");
    private static final IllegalInputException PLAYER_INVALID_POS_EXCEPTION = new IllegalInputException("At least one player, has a invalid position!");
    private static final IllegalInputException NO_PLAYERS_EXCEPTION = new IllegalInputException("There is no player!");
    private static final IllegalInputException CURRENT_PLAYER_INDEX_EXCEPTION = new IllegalInputException("Index current player is wrong!");
    private static final IllegalInputException CURRENT_PLAYER_IS_NOT_INVOLVED_EXCEPTION = new IllegalInputException("Current player is not involved!");
    private static final IllegalInputException TREASURE_OUT_OF_BOUNDS = new IllegalInputException("At least one treasure is out of bounds!");
    private static final IllegalInputException TREASURE_INVALID_FOR_PLAYER = new IllegalInputException("Invalid treasure for player");
    private static final IllegalInputException NUMBER_OF_PLAYERS_EXCEPTION = new IllegalInputException("Check the number of players!");
    private static final IllegalInputException PLAYER_DIRECTED_BY_EXCEPTION = new IllegalInputException("Check directed by of the players!");
    private static final IllegalInputException PLAYER_INVALID_NAME = new IllegalInputException("Invalid name of Player!");
    private static final IllegalInputException TREASURE_DOUBLE_IN_WAY_CARDS = new IllegalInputException("Duplicate treasure in way cards");

    /**
     * Constants
     */
    private static final int MAX_NUMBER_OF_PLAYERS = 4;
    private static final int MIN_PLAYER_INDEX = 0;
    private static final int MAX_PLAYER_INDEX = 3;
    private static final int OFFSET_PLAYER_INDEX = 1;
    private static final int MIN_POS = 0;
    private static final int MAX_POS = 6;
    private static final int MIN_TREASURE = 0;
    private static final int MIN_TREASURE_FOR_PLAYER = 1;
    private static final int MAX_TREASURE = 24;
    private static final int MIN_ROTATION = 0;
    private static final int MAX_ROTATION = 3;
    private static final int POS_FREE_CARD_OUT_LEFT_UP = -1;
    private static final int POS_FREE_CARD_OUT_RIGHT_DOWN = 7;
    private static final int NUMBER_OF_PLAYER = PlayerIndex.values().length;
    private static final Position INIT_POS_PLAYER = new Position(-1, -1);

    /**
     * Private class members
     */

    /**
     * For json object: a way card field
     */
    static class Field {
        int type;
        int rotated;
        int treasure;

        /**
         * Constructor for field
         *
         * @param t  - type
         * @param r  - rotation
         * @param tr - treasure
         */
        private Field(int t, int r, int tr) {
            type = t;
            rotated = r;
            treasure = tr;
        }
    }

    /**
     * For json object: the free way card
     */
    static class FreeWayCard {
        int type;
        int rotated;
        int treasure;
        Position position;
    }

    /**
     * For json object: a player object
     */
    static class GamePlayer {
        boolean involved;
        String name;
        int directedBy;
        Position position;
        int[] treasureCards;

    }

    /**
     * For json object: position x y
     */
    static class Position {
        int x;
        int y;

        /**
         * Constructor for position
         * @param x - position
         * @param y - position
         */
        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    /**
     * Will build a factory state, for saving options
     * @param wayCards - current way card
     * @param freeCard - current free card
     * @param blockedCoordinate  - blocked coordinate
     * @param players - players to safe
     * @param idxCurrPlayer - current player
     * @return - a state to save to json object
     */
    public static GameStateFactory buildState(WayCard[][] wayCards,
                                              WayCard freeCard,
                                              Coordinate blockedCoordinate,
                                              List<Player> players,
                                              PlayerIndex idxCurrPlayer) {
        // build the state
        GameStateFactory state = new GameStateFactory();
        Field tmpFreeCard = buildFieldFromWayCard(freeCard);
        state.freeWayCard = new FreeWayCard();

        state.freeWayCard.rotated = tmpFreeCard.rotated;
        state.freeWayCard.treasure = tmpFreeCard.treasure;
        state.freeWayCard.type = tmpFreeCard.type;
        state.freeWayCard.position = calcPositionFromBlockedCoordinate(blockedCoordinate);

        // build fields
        state.field = new Field[wayCards.length][];
        for (int x = 0; x < wayCards.length; x++) {
            state.field[x] = new Field[wayCards[x].length];
            for (int y = 0; y < wayCards[x].length; y++) {
                state.field[x][y] = buildFieldFromWayCard(wayCards[x][y]);
            }

        }

        state.currentPlayer = idxCurrPlayer.ordinal();
        state.players = new GamePlayer[NUMBER_OF_PLAYER];

        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            state.players[i] = new GamePlayer();
            GamePlayer player = state.players[i];
            player.involved = false;
            player.directedBy = 0;
            player.name = "";
            player.treasureCards = new int[0];
            player.position = INIT_POS_PLAYER;
        }

        // build player
        for (Player p : players) {
            int idx = p.getPlayerIndex().ordinal();
            GamePlayer currPlayer = state.players[idx];
            currPlayer.involved = true;
            currPlayer.directedBy = p.getPlayerMode().ordinal() - OFFSET_PLAYER_INDEX;
            currPlayer.name = p.getName();
            currPlayer.position = new Position(p.getPosition().getX(), p.getPosition().getY());
            int numberOfTreasure = p.getTreasures().size();
            currPlayer.treasureCards = new int[numberOfTreasure];

            for (int i = 0; i < numberOfTreasure; i++) {
                currPlayer.treasureCards[i] = p.getTreasures().get(i).ordinal();
            }
        }

        return state;
    }

    /**
     * Will calculate the json position of the blocked coordinate.
     * See documentation
     * @param coordinate - blocked coordinate logical
     * @return Position of the blocked coordinate
     */
    private static Position calcPositionFromBlockedCoordinate(Coordinate coordinate) {
        int x = -1,
                y = -1,
                logical_x = coordinate.getX(),
                logical_y = coordinate.getY();

        if (logical_x == -1 && logical_y == -1) {

            x = logical_x;
            y = logical_y;

        } else if (logical_x == MIN_POS) {

            x = POS_FREE_CARD_OUT_LEFT_UP;
            y = logical_y;

        } else if (logical_x == MAX_POS) {

            x = POS_FREE_CARD_OUT_RIGHT_DOWN;
            y = logical_y;

        } else if (logical_y == MIN_POS) {

            x = logical_x;
            y = POS_FREE_CARD_OUT_LEFT_UP;

        } else if (logical_y == MAX_POS) {

            x = logical_x;
            y = POS_FREE_CARD_OUT_RIGHT_DOWN;

        }

        Position pos = new Position(x, y);
        return pos;
    }

    /**
     * Builds a json Field of way card
     * @param wayCard way card to build as json object
     * @return  field
     */
    private static Field  buildFieldFromWayCard(WayCard wayCard) {
        int type = wayCard.getWayType().ordinal();
        int rotation = wayCard.getRotation().ordinal();
        int treasure = wayCard.getTreasure().ordinal();

        Field field = new Field(type, rotation, treasure);
        return field;
    }

    /**
     * Will build a way card to a given state
     * @param x_logical - position
     * @param y_logical - position
     * @return way card
     * @throws IllegalInputException - Way Card exception see above
     */
    public WayCard buildWayCard(int x_logical, int y_logical) throws IllegalInputException {
        try {
            Field field_ = field[x_logical][y_logical];
            return this.buildWayCard(field_);
            // when is out of bounds because of
        } catch (Exception e){
            throw WAY_CARD_EXCEPTION;
        }
    }

    /**
     * Will build the current player index
     * @return the current player index
     * @throws IllegalInputException - different exceptions when input is not valid
     */
    public PlayerIndex buildCurrentPlayerIndex() throws IllegalInputException {
        if (!isValidRange(currentPlayer, MIN_PLAYER_INDEX, MAX_PLAYER_INDEX)) {
            throw CURRENT_PLAYER_INDEX_EXCEPTION;
        }
        // no players
        if (players == null) {
            throw NO_PLAYERS_EXCEPTION;
        }
        // to many players or less
        if (!isValidRange(players.length, MAX_NUMBER_OF_PLAYERS, MAX_NUMBER_OF_PLAYERS)) {
            throw NUMBER_OF_PLAYERS_EXCEPTION;
        }
        // current player is not involved
        if (!players[currentPlayer].involved) {
            throw CURRENT_PLAYER_IS_NOT_INVOLVED_EXCEPTION;
        }

        return PlayerIndex.values()[currentPlayer];
    }

    /**
     * Will build the free card, and check validity
     * @return Way Card
     * @throws IllegalInputException when validity is not given
     */
    public WayCard buildFreeCard() throws IllegalInputException {
        if (!isValidFreeCard(freeWayCard)) {
            throw FREE_CARD_EXCEPTION;
        }

        Field field = new Field(freeWayCard.type, freeWayCard.rotated, freeWayCard.treasure);
        return this.buildWayCard(field);
    }

    /**
     * It's not needed to throw exception.
     * Build free card will do the job for us
     * @param min - min
     * @param max - max
     * @return
     */
    public Coordinate buildBlockedCoordinate(int min, int max)  {

        Position pos = freeWayCard.position;
        int x = pos.x;
        int y = pos.y;
        if (x == -1 && y == -1) {
            return new Coordinate(x, y);
        }

        if (x < min) {
            x = min;
        }
        if (x > max) {
            x = max;
        }
        if (y < min) {
            y = min;
        }
        if (y > max) {
            y = max;
        }
        return new Coordinate(x, y);
    }

    /**
     * Will build a game card to a given Field
     * @param field field
     * @return way card
     * @throws IllegalInputException
     */
    private WayCard buildWayCard(Field field) throws IllegalInputException {
        if (!isValidFieldWayCard(field)) {
            throw WAY_CARD_EXCEPTION;
        }
        WayType wayType = WayType.values()[field.type];
        Rotation rotation = Rotation.values()[field.rotated];
        Treasure treasure = Treasure.values()[field.treasure];
        return new WayCard(wayType, rotation, treasure);
    }

    /**
     * Check if the field is valid
     * @param field field to check
     * @return boolean
     */
    private boolean isValidFieldWayCard(Field field) {
        return field != null
                && checkRotation(field.rotated)
                && checkFieldType(field.type)
                && isValidTreasure(field.treasure);
    }

    /**
     * Will build the list of player to a state
     * @return list of player
     * @throws IllegalInputException different exception
     */
    public List<Player> buildPlayers() throws IllegalInputException {
        if (players == null) {
            throw NO_PLAYERS_EXCEPTION;
        }

        if (!isValidRange(currentPlayer, MIN_PLAYER_INDEX, MAX_PLAYER_INDEX)) {
            throw CURRENT_PLAYER_INDEX_EXCEPTION;
        }
        // current player is not playing
        if (!players[currentPlayer].involved) {
            throw CURRENT_PLAYER_IS_NOT_INVOLVED_EXCEPTION;
        }

        if (!isValidRange(players.length, MAX_NUMBER_OF_PLAYERS, MAX_NUMBER_OF_PLAYERS)) {
            throw NUMBER_OF_PLAYERS_EXCEPTION;
        }

        List<Player> players_ = new LinkedList<>();
        List<Treasure> checkTreasuresOfPlayerWithWayCardTreasure = new LinkedList<>();
        for (int i = 0; i < players.length; i++) {
            GamePlayer player = players[i];
            if (player.involved) {
                // not necessary to check index of, because we already check the length of the players
                PlayerIndex index = PlayerIndex.values()[i];
                int directedBy = player.directedBy;

                if (!isValidRange(directedBy, 0, 2)) {
                    throw PLAYER_DIRECTED_BY_EXCEPTION;
                }

                if (player.name == null) {
                    throw PLAYER_INVALID_NAME;
                }

                PlayerMode mode = PlayerMode.values()[player.directedBy + OFFSET_PLAYER_INDEX]; // our enum starts with NONE, so add 1
                String name = player.name;
                int logical_x = player.position.x;
                int logical_y = player.position.y;

                if (!isValidRange(logical_x, MIN_POS, MAX_POS) || !isValidRange(logical_y, MIN_POS, MAX_POS)) {
                    throw PLAYER_INVALID_POS_EXCEPTION;
                }

                Coordinate pos = new Coordinate(logical_x, logical_y);

                //add the treasures
                List<Treasure> treasures = new LinkedList<>();
                for (int t : player.treasureCards) {
                    if (!isValidRange(t, MIN_TREASURE_FOR_PLAYER, MAX_TREASURE)) {
                        throw TREASURE_INVALID_FOR_PLAYER;
                    }

                    Treasure t_ = Treasure.values()[t];
                    if (checkTreasuresOfPlayerWithWayCardTreasure.contains(t_)) {
                        throw PLAYER_SEARCHING_SAME_TREASURE;
                    }

                    treasures.add(t_);
                    checkTreasuresOfPlayerWithWayCardTreasure.add(t_);
                }

                // everything is fine, build the player and add it to the list
                Player lPLayer = new Player(index, mode, name, pos);
                lPLayer.addTreasures(treasures);
                players_.add(lPLayer);
            }

        }
        boolean exFlag = true;
        List<Treasure> treasuresOfWayCards = extractTreasuresCheckDuplicate(field, freeWayCard);

        // check the way card of the player
        for (int i = 0; i < checkTreasuresOfPlayerWithWayCardTreasure.size() && exFlag; i++) {
            Treasure t = checkTreasuresOfPlayerWithWayCardTreasure.get(i);
            exFlag = exFlag && treasuresOfWayCards.contains(t);
        }
        if (!exFlag) {
            throw TREASURE_OF_PLAYER_IS_NOT_ON_FIELD;
        }

        return players_;
    }

    /**
     * Extract treasure, and throw exception when there are duplicates
     * @param field - field to check
     * @param freeWayCard - free way card
     * @return - list of treasures
     * @throws IllegalInputException different exceptions
     */
    private List<Treasure> extractTreasuresCheckDuplicate(Field[][] field, FreeWayCard freeWayCard) throws IllegalInputException {
        List<Treasure> treasures = new LinkedList<>();
        for (Field[] fi : field) {
            for (Field f : fi) {
                if (!isValidTreasure(f.treasure)) {
                    throw TREASURE_OUT_OF_BOUNDS;
                }
                Treasure t = Treasure.values()[f.treasure];
                // check duplicate
                if (t != Treasure.NONE && treasures.contains(t)) {
                    throw TREASURE_DOUBLE_IN_WAY_CARDS;
                }
                treasures.add(t);
            }
        }
        int treasureFreeCard = freeWayCard.treasure;
        // also consider the free card
        if (isValidTreasure(treasureFreeCard) && treasureFreeCard > 0) {
            Treasure t = Treasure.values()[treasureFreeCard];
            treasures.add(t);
        }
        return treasures;
    }

    /**
     * Check the validity of the free card
     * @param freeCard free card to check
     * @return boolean
     */
    private boolean isValidFreeCard(FreeWayCard freeCard) {
        return freeCard != null
                && checkRotation(freeCard.rotated)
                && checkFieldType(freeCard.type)
                && isValidTreasure(freeCard.treasure)
                && checkPositionOfFreeCard(freeCard.position);
    }

    /**
     * Check the field type
     * @param type - type to check
     * @return boolean
     */
    private boolean checkFieldType(int type) {
        return isValidRange(type, 0, 2);
    }

    /**
     * Check validity of treasure
     * @param treasure - treasure to check
     * @return booleana
     */
    private boolean isValidTreasure(int treasure) {
        return isValidRange(treasure, MIN_TREASURE, MAX_TREASURE);
    }

    /**
     * Check the rotation of a free card
     * @param rotation - rotation to check
     * @return boolean
     */
    private boolean checkRotation(int rotation) {
        return isValidRange(rotation, MIN_ROTATION, MAX_ROTATION);
    }

    /**
     * Check the validity of the free card  position
     * @param position position to check
     * @return boolean
     */
    private boolean checkPositionOfFreeCard(Position position) {
        return (position.x == -1 && position.y == -1)
                || freeCardPosOnRow(position)
                || freeCardPosOnColumn(position);
    }

    /**
     * Will check if the free card is on a row
     * @param pos position
     * @return boolean
     */
    private boolean freeCardPosOnRow(Position pos) {
        return isOut(pos.x) && (pos.y % 2 == 1) && isValidRange(pos.y, 0, 6);
    }

    /**
     * Will check if the free card is on a column
     * @param pos position
     * @return boolean
     */
    private boolean freeCardPosOnColumn(Position pos) {
        return isOut(pos.y) && (pos.x % 2 == 1) && isValidRange(pos.x, 0, 6);
    }

    /**
     * Check if it's out
     * @param pos position
     * @return logical
     */
    private boolean isOut(int pos) {
        return isValidRange(pos, -1, -1) || isValidRange(pos, 7, 7);
    }

    /**
     * Checking range
     * @param toCheck to check
     * @param min clamp min
     * @param max clamp max
     * @return boolean
     */
    private boolean isValidRange(int toCheck, int min, int max) {
        return min <= toCheck && toCheck <= max;
    }

}
