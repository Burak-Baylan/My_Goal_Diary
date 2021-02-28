package com.example.mygoaldiary

import kotlinx.coroutines.delay

object ConstantValues {

    var PROJECT_VARIABLES_STRING =
            "id INTEGER PRIMARY KEY, projectUuid VARCHAR, title TEXT, projectColor INTEGER, yearDate TEXT, time TEXT, isHybrid TEXT, lastInteraction TEXT, targetedDeadline TEXT"

    var PROJECT_VARIABLES_NAME_STRING =
            "projectUuid, title, projectColor, yearDate, time, isHybrid, lastInteraction, targetedDeadline"

    var TASK_VARIABLES_STRING =
            "id INTEGER PRIMARY KEY, taskUuid VARCHAR, title VARCHAR, isDone VARCHAR, isHybridTask VARCHAR, yearDate VARCHAR, time VARCHAR"

    var TASK_VARIABLES_NAME_STRING =
            "taskUuid, title, isDone, isHybridTask, yearDate, time"

}