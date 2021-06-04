#include "logic.h"
#include "io.h"
#include "advancedMath.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

/// These are some marcos that will check if a key is pressed and also preventing opposite directions at the same time
#define IS_PLAYER_PRESSING(input, key) !!(input & key)
#define IS_PLAYER_PRESSING_UP(input) !!(IS_PLAYER_PRESSING(input, PADDLE_UP) && !IS_PLAYER_PRESSING(input, PADDLE_DOWN))
#define IS_PLAYER_PRESSING_DOWN(input) !!(!IS_PLAYER_PRESSING(input, PADDLE_UP) && IS_PLAYER_PRESSING(input, PADDLE_DOWN))
#define IS_PLAYER_PRESSING_LEFT(input) !!(IS_PLAYER_PRESSING(input, PADDLE_ROTATE_CLOCKWISE) && !IS_PLAYER_PRESSING(input, PADDLE_ROTATE_COUNTER_CLOCKWISE))
#define IS_PLAYER_PRESSING_RIGHT(input) !!(!IS_PLAYER_PRESSING(input, PADDLE_ROTATE_CLOCKWISE) && IS_PLAYER_PRESSING(input, PADDLE_ROTATE_COUNTER_CLOCKWISE))

#define BALL_LEFT_FROM_PADDLE(pBall, paddle) ((pBall->pos.x - pBall->radius) < (paddle.pos.x + (paddle.size.width/2)))
#define BALL_RIGHT_FROM_PADDLE(pBall, paddle) ((pBall->pos.x + pBall->radius) > (paddle.pos.x - (paddle.size.width/2)))

#define _BALL_ON_SAME_HEIGHT_UPPER(pBall, paddle) ((pBall->pos.y + pBall->radius) > (paddle.pos.y - (paddle.size.height/2)))
#define _BALL_ON_SAME_HEIGHT_LOWER(pBall, paddle) ((pBall->pos.y - pBall->radius) < (paddle.pos.y + (paddle.size.height/2)))
#define BALL_ON_SAME_HEIGHT_AS_PADDLE(pBall, paddle) (_BALL_ON_SAME_HEIGHT_UPPER(pBall, paddle) && _BALL_ON_SAME_HEIGHT_LOWER(pBall, paddle))

#define BALL_IS_MOVING_LEFT(pBall) ((pBall->momentum.x < 0))
#define BALL_IS_MOVING_RIGHT(pBall) ((pBall->momentum.x > 0))

unsigned int winningScore = 3;

const float paddleMaxRotation = 35.0f;
const float paddleMinRotation = -35.0f;

const float paddleVerticalSpeed = 0.7f;
const float paddleRotationSpeed = 150.0f;

const float ballStartSpeed = 0.4f;
const float ballRotationSpeed = 150.0f;
const float ballSpeedAddedPerPaddleHit = 0.1f;
const float ballStartMargin = 0.2f;

/// Processes the paddles via input.
void processPaddle(float timeDelta, PaddleInfo *paddle, PLAYER_INPUT input) {
    if (IS_PLAYER_PRESSING_UP(input)) {
        paddle->pos.y += timeDelta * paddleVerticalSpeed;
    } else if (IS_PLAYER_PRESSING_DOWN(input)) {
        paddle->pos.y -= timeDelta * paddleVerticalSpeed;
    }

    if (IS_PLAYER_PRESSING_LEFT(input)) {
        paddle->rotation -= timeDelta * paddleRotationSpeed;
    } else if (IS_PLAYER_PRESSING_RIGHT(input)) {
        paddle->rotation += timeDelta * paddleRotationSpeed;
    }
    paddle->rotation = CLAMP(paddle->rotation, paddleMinRotation, paddleMaxRotation);

    float paddleMinY = (paddle->size.height / 2) + gGameState.bottomBarrierHeight;
    float paddleMaxY = (1 - (paddle->size.height / 2)) - gGameState.topBarrierHeight;
    paddle->pos.y = CLAMP(paddle->pos.y, paddleMinY, paddleMaxY);
}

/// Returns the input for an AI player.
PLAYER_INPUT DoAiWork(SIDE forSide) {
    PLAYER_INPUT result = 0;

    float myPaddleY;
    float targetY;

    BallInfo* ball = &gGameState.ball;
    if (forSide == LEFT_SIDE) {
        myPaddleY = gGameState.paddleLeft.pos.y;
        if (BALL_IS_MOVING_RIGHT(ball)) {
            targetY = 0.5f;
        } else {
            targetY = ball->pos.y;
        }
    } else { // RIGHT_SIDE
        myPaddleY = gGameState.paddleRight.pos.y;
        if (BALL_IS_MOVING_LEFT(ball)) {
            targetY = 0.5f;
        } else {
            targetY = ball->pos.y;
        }
    }

    // Only move the paddle if this threshold is exceeded
    const float startToMoveThreshold = paddleVerticalSpeed / 30.0f;
    float diff = myPaddleY - targetY;

    if (diff >= startToMoveThreshold) {
        result |= PADDLE_DOWN;
    } else if (diff <= -startToMoveThreshold) {
        result |= PADDLE_UP;
    }

    return result;
}

void processInput(float timeDelta) {
    PLAYER_INPUT leftInput = IS_LEFT_SIDE_CONTROLLED_BY_AI ? DoAiWork(LEFT_SIDE) : gInput.left;
    PLAYER_INPUT rightInput = IS_RIGHT_SIDE_CONTROLLED_BY_AI ? DoAiWork(RIGHT_SIDE) : gInput.right;
    // Let the AI do it's work before modifying the gameState

    processPaddle(timeDelta, &gGameState.paddleLeft, leftInput);
    processPaddle(timeDelta, &gGameState.paddleRight, rightInput);
}

void printScores(void) {
    printf("Score: %d vs %d\n", gGameState.scoreLeftPlayer, gGameState.scoreRightPlayer);
}

/// Creates a random number between (including) the given number ranges
int randomRange(int min, int max) {
    return min + (rand() % ((max + 1) - min));
}

void scoreUpAndResetBall(BallInfo* ball, SIDE winning_side) {
    float ballStartX;

    // We are calculating a random angle from -45° to 45°
    // Depending on the side the ball is spawning; we are negating the y part of the coordinate
    // if the ball spawn on the right side we are negating x to convert the angle to -135°,+135°
    float startMomentumXDirection;
    int startAngle = randomRange(-45, 45);

    if (winning_side == LEFT_SIDE) {
        gGameState.scoreLeftPlayer++;
        ballStartX = ballStartMargin;
        startMomentumXDirection = 1; // we want the ball to fly to the RIGHT side
    } else { // must be RIGHT_SIDE
        gGameState.scoreRightPlayer++;
        ballStartX = 1 - ballStartMargin;
        startMomentumXDirection = -1; // we want the ball to fly to the LEFT side
    }

    ball->pos.x = ballStartX;
    ball->pos.y = 0.5f; // Center
    ball->speed = ballStartSpeed;

    ball->momentum = unitVectorInDeg((float) startAngle);
    ball->momentum.x *= startMomentumXDirection; // flip the x direction if needed

    if (gGameState.scoreLeftPlayer >= winningScore) {
        printf("Left player won\n");
        exit(EXIT_SUCCESS);
    } else if (gGameState.scoreRightPlayer >= winningScore) {
        printf("Right player won\n");
        exit(EXIT_SUCCESS);
    }

    printf("Player %s won this round\n", SIDE_TO_NAME(winning_side));
    printScores();
}

void calculateBallCollisionWithPaddle(BallInfo* ball, const PaddleInfo* touchedPaddle) {
    ball->momentum.x *= -1;

    // arctan2 | Calculating the angle of a vector | Note: we already flipped the x side
    float ballDirectionRad = atan2f(ball->momentum.y, ball->momentum.x);

    // Our current paddle rotation
    float paddleRotationRad = degToRad(touchedPaddle->rotation);

    // Calculate the new angle | https://en.wikipedia.org/wiki/Reflection_(physics)#Reflection_of_light
    float newDirectionRad = ballDirectionRad + (paddleRotationRad * 2);

    ball->momentum = unitVectorInRad(newDirectionRad);

    ball->speed += ballSpeedAddedPerPaddleHit;
}

void processBallAndCollision(float timeDelta, BallInfo *ball) {
    ball->pos.x += ball->momentum.x * ball->speed * timeDelta;
    ball->pos.y += ball->momentum.y * ball->speed * timeDelta;
    ball->rotation += ballRotationSpeed * ball->speed * timeDelta;

    /// Collision checks

    /// Touches top/bottom barrier
    // If the ball is already "in" a barrier we have to repel it in the opposite direction
    float yAlreadyInTopBarrier = (ball->pos.y + ball->radius) - (1 - gGameState.topBarrierHeight);
    if (yAlreadyInTopBarrier > 0) {
        ball->momentum.y *= -1;
        ball->pos.y -= yAlreadyInTopBarrier;
    } else {
        float yAlreadyInBottomBarrier = (gGameState.bottomBarrierHeight) - (ball->pos.y - ball->radius);
        if (yAlreadyInBottomBarrier > 0) {
            ball->momentum.y *= -1;
            ball->pos.y += yAlreadyInBottomBarrier;
        }
    }

    /// Touches out of field
    if (BALL_IS_MOVING_LEFT(ball) && (ball->pos.x + ball->radius) < 0) {
        scoreUpAndResetBall(ball, RIGHT_SIDE); /// Touches left side
    } else if (BALL_IS_MOVING_RIGHT(ball) && (ball->pos.x - ball->radius) > 1) {
        scoreUpAndResetBall(ball, LEFT_SIDE); /// Touches right side
    }

    /// Touches paddles
    // TODO: Working with "alreadyInPaddle float" would be to complicated to calculate for a rotating rectangle
    if (BALL_IS_MOVING_LEFT(ball) &&
        BALL_LEFT_FROM_PADDLE(ball, gGameState.paddleLeft) &&
        BALL_ON_SAME_HEIGHT_AS_PADDLE(ball, gGameState.paddleLeft)
        ) {
        calculateBallCollisionWithPaddle(ball, &gGameState.paddleLeft); /// Touches left paddle
        ball->color = randomRange(0, COLOR_MAX - 1);
        ball->type = randomRange(0, BALL_TYPE_MAX - 1);
    } else if (
            BALL_IS_MOVING_RIGHT(ball) &&
            BALL_RIGHT_FROM_PADDLE(ball, gGameState.paddleRight) &&
            BALL_ON_SAME_HEIGHT_AS_PADDLE(ball, gGameState.paddleRight)
            ) {
        calculateBallCollisionWithPaddle(ball, &gGameState.paddleRight); /// Touches right paddle
        ball->color = randomRange(0, COLOR_MAX - 1);
        ball->type = randomRange(0, BALL_TYPE_MAX - 1);
    }
}

/// Resets the global game state to a default one
void resetGameState(void) {
    memset(&gGameState, 0, sizeof(gGameState));

    gGameState.scoreLeftPlayer = gGameState.scoreRightPlayer = 0;

    gGameState.topBarrierHeight = gGameState.bottomBarrierHeight = 0.05f;

    gGameState.paddleLeft.size.width = gGameState.paddleRight.size.width = 0.04f;
    gGameState.paddleLeft.size.height = gGameState.paddleRight.size.height = 0.2f;

    const float paddleMarginFromScreenBorder = 0.025f;
    gGameState.paddleLeft.pos.x = paddleMarginFromScreenBorder + (gGameState.paddleLeft.size.width / 2);
    gGameState.paddleRight.pos.x = 1 - paddleMarginFromScreenBorder - (gGameState.paddleRight.size.width / 2);
    gGameState.paddleLeft.pos.y = gGameState.paddleRight.pos.y = 0.5f; // centering yboth paddles vertically

    gGameState.ball.radius = 0.03f;
    gGameState.ball.rotation = gGameState.ball.color = 0;

    // Initial movement
    gGameState.ball.pos.x = 1 - ballStartMargin;
    gGameState.ball.pos.y = 0.5f;
    const float startAngle = 180.0f;
    gGameState.ball.momentum = unitVectorInDeg(startAngle);
    gGameState.ball.speed = 0.3f;

    gGameState.paddleLeft.rotation = 30.0f;

    // Norm the momentum vector
    float momLength = vectorLength(gGameState.ball.momentum.x, gGameState.ball.momentum.y);
    gGameState.ball.momentum.x /= momLength;
    gGameState.ball.momentum.y /= momLength;

    gGameState.ball.color = GREEN;
    printScores();
}

/// Updates the global gameState (physics calculations, score, win condition and stuff)
void updateGameState(float timeDelta) {
    if (!IS_PAUSED) {
        processInput(timeDelta);
        processBallAndCollision(timeDelta, &gGameState.ball);
    }
}
