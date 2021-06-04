#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "level.h"
#include "advancedMath.h"

typedef struct {
    IntVector2 hunterPos;
    IntVector2 exitPosition;
    int timeInSec;

    IntSize2 mapSize;
    const FIELD_TYPE* map;
} LevelTemplate;
/// Level 1
const static FIELD_TYPE level1[] = {
        P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_EXIT, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_KEY_GOLD, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_TREASURE_CLOSED, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_TRAP, P_FREE, P_FREE, P_FREE, P_FREE, P_WALL,
        P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL
};
/// Level 2
const static FIELD_TYPE level2[] = {
        P_BLACK, P_BLACK, P_WALL, P_BLACK, P_BLACK, P_BLACK, P_BLACK, P_BLACK,
        P_BLACK, P_WALL, P_EXIT, P_WALL, P_BLACK, P_BLACK, P_BLACK, P_BLACK,
        P_WALL, P_FREE, P_FREE, P_FREE, P_WALL, P_BLACK, P_BLACK, P_BLACK,
        P_WALL, P_FREE, P_FREE, P_FREE, P_WALL, P_WALL, P_WALL, P_WALL,
        P_WALL, P_KEY_GRAY, P_FREE, P_TREASURE_CLOSED, P_DOOR, P_FREE, P_KEY_GOLD, P_WALL,
        P_WALL, P_FREE, P_FREE, P_FREE, P_DOOR, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_FREE, P_WALL, P_WALL, P_WALL, P_WALL,
        P_WALL, P_BOX2, P_BOX2, P_BOX2, P_WALL, P_BLACK, P_BLACK, P_BLACK,
        P_WALL, P_FREE, P_FREE, P_KEY_GRAY, P_WALL, P_BLACK, P_BLACK, P_BLACK,
        P_BLACK, P_WALL, P_FREE, P_WALL, P_BLACK, P_BLACK, P_BLACK, P_BLACK,
        P_BLACK, P_BLACK, P_WALL, P_BLACK, P_BLACK, P_BLACK, P_BLACK, P_BLACK
};

/// Level 3
const static FIELD_TYPE level3[] = {
        P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, // y = 0
        P_WALL, P_FREE, P_FREE, P_FREE, P_DOOR, P_FREE, P_FREE, P_FREE, P_EXIT, // y = 1
        P_WALL, P_FREE, P_FREE, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL,
        P_WALL, P_FREE, P_FREE, P_WALL, P_TREASURE_CLOSED, P_WALL, P_KEY_GRAY, P_FREE, P_WALL,
        P_WALL, P_FREE, P_WALL, P_FREE, P_FREE, P_BOX1, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_WALL, P_FREE, P_FREE, P_WALL, P_WALL, P_FREE, P_WALL,
        P_WALL, P_FREE, P_TRAP, P_FREE, P_FREE, P_DOOR, P_FREE, P_BOX3, P_WALL,
        P_WALL, P_FREE, P_WALL, P_FREE, P_WALL, P_WALL, P_FREE, P_FREE, P_WALL,
        P_WALL, P_WALL, P_KEY_GRAY, P_WALL, P_FREE, P_FREE, P_BOX2, P_FREE, P_WALL,
        P_WALL, P_FREE, P_BOX1, P_FREE, P_FREE, P_WALL, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_FREE, P_FREE, P_BOX3, P_FREE, P_FREE, P_WALL,
        P_WALL, P_FREE, P_FREE, P_FREE, P_FREE, P_BOX1, P_FREE, P_KEY_GOLD, P_WALL,
        P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL, P_WALL
};

/// Original levels that can be coped to level instances
const static LevelTemplate templates[] = {
        {
                .hunterPos = {2, 9},
                .exitPosition = {7, 1},
                .timeInSec = 90,
                .mapSize = {9, 11},
                .map = level1,
        },
        {
                .hunterPos = {1, 2},
                .exitPosition = {2 ,1},
                .timeInSec = 50,
                .mapSize = {8, 11},
                .map = level2,
        },
        {
                .hunterPos = {2, 10},
                .exitPosition = {8, 1},
                .timeInSec = 60,
                .mapSize = {9, 13},
                .map = level3,
        },
};

unsigned int totalLevelCount() {
    return sizeof(templates) / sizeof(*templates);
}

GameState generateLevel(LevelNumber levelNumber) {
    if (levelNumber <= 0 && levelNumber >= totalLevelCount()) {
        printf("Unknown level %d", levelNumber);
        exit(EXIT_FAILURE);
    }

    const LevelTemplate* template = &(templates[levelNumber]);

    // allocate space
    GameState gameState = malloc(sizeof(*gameState));
    unsigned int totalFieldsSpace = template->mapSize.width * template->mapSize.height * sizeof(*template->map);
    gameState->map.fields = malloc(totalFieldsSpace);

    // Copy level info
    gameState->map.size = template->mapSize;
    memcpy(gameState->map.fields, template->map, totalFieldsSpace);

    // Set plain variables
    gameState->hunterPos = template->hunterPos;
    gameState->timeLeft = template->timeInSec;
    gameState->collectedGoldKeyCount = 0;
    gameState->collectedGrayKeyCount = 0;
    gameState->levelNumber = levelNumber;
    gameState->exitIsOpen = 0;
    UNUSED(levelNumber);  //TODO: unsused
    gameState->cameraMode = VIEW3D_FREE_VIEW;
    gameState->freeViewCamera.azimuthalAngle = degToRad(45);
    gameState->freeViewCamera.polarAngle = degToRad(45);
    gameState->freeViewCamera.distanceFromCenter = 10;
    gameState->lightRenderingIsEnabled = 1;
    gameState->lightParallelIsEnabled = 1;
    gameState->spotLightIsEnabled = 1;
    gameState->timeLeft = template->timeInSec;

    gameState->hunterDirection = UP;

    return gameState;
}

void freeGeneratedLevel(GameState gameState) {
    if (gameState) {
        free(gameState->map.fields);
        free(gameState);
    }
}
