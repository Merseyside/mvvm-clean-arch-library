package com.merseyside.mvvmcleanarch.utils.time

object Conversions {
    const val MILLIS_CONST = 1000L
    const val SECONDS_MINUTES_CONST = 60L
    const val HOURS_CONST = 24L
}

interface TimeUnit {
    val value: Long

    fun toMillisLong(): Long {
        return toMillis().value
    }

    fun toMillis(): Millis
    fun toSeconds(): Seconds
    fun toMinutes(): Minutes
    fun toHours(): Hours
    fun toDays(): Days

    fun toLong(): Long {
        return value
    }

    operator fun TimeUnit.plus(increment: Long): Long {
        return value + increment
    }

    operator fun TimeUnit.div(divider: Long): Long {
        return value / divider
    }

    operator fun TimeUnit.times(mul: Long): Long {
        return value * mul
    }

    operator fun TimeUnit.minus(operand: Long): Long {
        return value - operand
    }
}


inline class Millis(override val value: Long): TimeUnit {

    override fun toMillis(): Millis {
        return this
    }

    override fun toSeconds(): Seconds {
        return Seconds(value / Conversions.MILLIS_CONST)
    }

    override fun toMinutes(): Minutes {
        return Minutes(toSeconds() / Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toHours(): Hours {
        return Hours(toMinutes() / Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toDays(): Days {
        return Days(toHours() / Conversions.HOURS_CONST)
    }
}

inline class Seconds(override val value: Long): TimeUnit {

    override fun toMillis(): Millis {
        return Millis(value * Conversions.MILLIS_CONST)
    }

    override fun toSeconds(): Seconds {
        return this
    }

    override fun toMinutes(): Minutes {
        return Minutes(value / Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toHours(): Hours {
        return Hours(toMinutes() / Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toDays(): Days {
        return Days(toHours() / Conversions.HOURS_CONST)
    }
}

inline class Minutes(override val value: Long): TimeUnit {

    override fun toMillis(): Millis {
        return Millis(toSeconds() * Conversions.MILLIS_CONST)
    }

    override fun toSeconds(): Seconds {
        return Seconds(value * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toMinutes(): Minutes {
        return this
    }

    override fun toHours(): Hours {
        return Hours(value / Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toDays(): Days {
        return Days(toHours() / Conversions.HOURS_CONST)
    }
}

inline class Hours(override val value: Long): TimeUnit {

    override fun toMillis(): Millis {
        return Millis(toSeconds() * Conversions.MILLIS_CONST)
    }

    override fun toSeconds(): Seconds {
        return Seconds(toMinutes() * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toMinutes(): Minutes {
        return Minutes(value * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toHours(): Hours {
        return this
    }

    override fun toDays(): Days {
        return Days(value / Conversions.HOURS_CONST)
    }
}

inline class Days(override val value: Long): TimeUnit {

    override fun toMillis(): Millis {
        return Millis(toSeconds() * Conversions.MILLIS_CONST)
    }

    override fun toSeconds(): Seconds {
        return Seconds(toMinutes() * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toMinutes(): Minutes {
        return Minutes(toHours() * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toHours(): Hours {
        return Hours(value * Conversions.SECONDS_MINUTES_CONST)
    }

    override fun toDays(): Days {
        return this
    }
}