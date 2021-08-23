package logic;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Class TextLogger, for creating a log file.
 * @author Miwand Baraksaie inf104162
 */
public class TextLogger implements GameLogger {

    private static final String NAME_OF_FILE = "outFile.txt";
    private FileOutputStream logger;
    private StringBuilder buffer;


    /**
     * Enum with definition constants
     */
    enum Definition {
        WELCOME,
        IS_PLAYING,
        TREASURE,
        PLAYER_MODE,
        TOTAL_TREASURE,
        MOVE_WAY_CARD_STATE,
        PUSH_CARD_LEFT_TO_RIGHT,
        PUSH_CARD_RIGHT_TO_LEFT,
        PUSH_CARD_UP_TO_DOWN,
        PUSH_CARD_DOWN_TO_UP,
        ROTATE_FREE_CARD_CLOCKWISE,
        MOVE_TO,
        MOVE_FROM,
        X_COORDINATE,
        Y_COORDINATE,
        HAS_COLLECTED,
        GO_TO_START,
        HAS_WON,
        NOT_INVOLVED,
        LINE_BREAK;

        /**
         * Every definition constant has a text to return
         * @param def defination
         * @return text
         */
        public static String getText(Definition def) {
            String result;

            switch (def) {
                case WELCOME:
                    result = "Welcome to labyrinth";
                    break;

                case IS_PLAYING:
                    result = "is playing: ";
                    break;

                case PLAYER_MODE:
                    result = " is: ";
                    break;

                case TREASURE:
                    result = " has Treasures: ";
                    break;

                case TOTAL_TREASURE:
                    result = "Total treasures: ";
                    break;

                case MOVE_WAY_CARD_STATE:
                    result = " is on turn. Please push way card";
                    break;

                case PUSH_CARD_LEFT_TO_RIGHT:
                    result = " pushed free way card from left to right at ";
                    break;

                case ROTATE_FREE_CARD_CLOCKWISE:
                    result = "Free card rotated clockwise";
                    break;

                case PUSH_CARD_RIGHT_TO_LEFT:
                    result = " pushed free way card from right to left at ";
                    break;

                case PUSH_CARD_UP_TO_DOWN:
                    result = " pushed free way card from up to down at ";
                    break;
                case PUSH_CARD_DOWN_TO_UP:
                    result = " pushed free way card from down to up at ";
                    break;

                case MOVE_TO:
                    result = " to: ";
                    break;
                case MOVE_FROM:
                    result = " is moving from: ";
                    break;
                case X_COORDINATE:
                    result = " X: ";
                    break;
                case Y_COORDINATE:
                    result = " Y: ";
                    break;
                case HAS_COLLECTED:
                    result = " has collected ";
                    break;

                case GO_TO_START:
                    result = " finished collecting treasures, go to your start position at: ";
                    break;

                case HAS_WON:
                    result = " has won!";
                    break;
                case NOT_INVOLVED:
                    result = " is not involved.";
                    break;
                case LINE_BREAK:
                    result = "\n";
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + def);
            }
            return result;
        }
    }

    /**
     * Constructor,
     * will create a new log file.
     * Will write the welcome text.
     */
    public TextLogger() {
        try {
            this.logger = new FileOutputStream(new File(NAME_OF_FILE));
            buffer = new StringBuilder();
            buffer.append(getText(Definition.WELCOME));
            buffer.append(getText(Definition.LINE_BREAK));
            logger.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
            logger.close();
            buffer.delete(0, buffer.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logPlayingPlayer(Player player) {
        openStreamAndAppendTextAndClose(
                getText(Definition.IS_PLAYING),
                player.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerHasTreasures(Player player) {
        openStreamAndAppendTextAndClose(
                player.getPlayerIndex().toString(),
                getText(Definition.TREASURE),
                player.getTreasures().toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logTotalTreasures(int amount) {
        openStreamAndAppendTextAndClose(getText(Definition.TOTAL_TREASURE),
                Integer.toString(amount),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logCurrentTurnStateOne(Player currentTurn) {
        openStreamAndAppendTextAndClose(currentTurn.getPlayerIndex().toString(),
                getText(Definition.MOVE_WAY_CARD_STATE),
                getText(Definition.LINE_BREAK));
    }


    @Override
    public void logPlayerPushedWayCardsHorizontalFromLeftToRight(int x_fx, int y_logical, Player player, WayCard freeCard) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.PUSH_CARD_LEFT_TO_RIGHT),
                buildCoordinate(x_fx, y_logical),
                freeCard.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerPushedWayCardsHorizontalFromRightToLeft(int x_fx, int y_logical, Player player, WayCard freeCard) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.PUSH_CARD_RIGHT_TO_LEFT),
                buildCoordinate(x_fx, y_logical),
                freeCard.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerPushedWayCardsVerticalFromUpToDown(int x_logical, int y_fx, Player player, WayCard freeCard) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.PUSH_CARD_UP_TO_DOWN),
                buildCoordinate(x_logical, y_fx),
                freeCard.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerPushedWayCardsVerticalFromDownToUp(int x_logical, int y_fx, Player player, WayCard freeCard) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.PUSH_CARD_DOWN_TO_UP),
                buildCoordinate(x_logical, y_fx),
                freeCard.toString(),
                getText(Definition.LINE_BREAK));
    }

    /**
     * Builds a String to given coordinates.
     *
     * @param x - position
     * @param y - position
     * @return - build String
     */
    private String buildCoordinate(int x, int y) {
        return new Coordinate(x, y).toString();
    }

    @Override
    public void logMovePlayerTo(Player player, Coordinate pos, Coordinate newPos) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.MOVE_FROM),
                pos.toString(),
                getText(Definition.MOVE_TO),
                newPos.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerCollectedTreasure(Treasure treasure, Player player) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.HAS_COLLECTED),
                treasure.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerGoToStartPosition(Player player, Coordinate coordinate) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.GO_TO_START),
                coordinate.toString(),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logHasWon(Player player) {
        openStreamAndAppendTextAndClose(player.getPlayerIndex().toString(),
                getText(Definition.HAS_WON),
                getText(Definition.LINE_BREAK));
    }

    @Override
    public void logPlayerNotInvolved(PlayerIndex index) {
        openStreamAndAppendTextAndClose(index.toString(),
                getText(Definition.NOT_INVOLVED),
                getText(Definition.LINE_BREAK));
    }

    /**
     * Opens the stream and appends the text to the
     * buffer. Write the buffer to stream. Will close the stream. Will delete the buffer
     * @param texts texts
     */
    private void openStreamAndAppendTextAndClose(String... texts) {
        try {
            this.logger = new FileOutputStream(new File(NAME_OF_FILE), true);
            for (String t : texts) {
                this.buffer.append(t);
            }
            logger.write(buffer.toString().getBytes(StandardCharsets.UTF_8));
            logger.close();
            buffer.delete(0, buffer.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param def
     * @return
     */
    private String getText(Definition def) {
        return Definition.getText(def);
    }


}
