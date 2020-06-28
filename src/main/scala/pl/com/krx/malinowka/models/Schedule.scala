package pl.com.krx.malinowka.models

import java.util.concurrent.TimeUnit

case class Schedule(initialDelay:Long = 0,period:Long,timeUnit: TimeUnit = TimeUnit.MILLISECONDS)
