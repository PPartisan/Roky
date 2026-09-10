package chatroom

import utils.FileLogger

fun Any.logSubscribe(caller: String) =
    also {
        FileLogger.log("$caller::subscribing")
    }

fun Any.logUnsubscribe(caller: String) =
    also {
        FileLogger.log("$caller::unsubscribing")
    }
