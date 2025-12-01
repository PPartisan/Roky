package utils

import org.koin.dsl.factory
import org.koin.dsl.module

val smartWrapModule =
    module {
        factory<SmartWrap> { LoggingSmartWrap() }
    }
