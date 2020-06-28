package pl.com.krx.malinowka

import org.iot.raspberry.grovepi.pi4j.GrovePi4J
import org.iot.raspberry.grovepi.{GroveAnalogIn, GroveAnalogOut, GroveDigitalIn, GroveDigitalOut}

import scala.concurrent.{ExecutionContext, Future}


class GrovePi4S()(implicit private val executionContext: ExecutionContext){

  val grove = new GrovePi4J()

  def close(): Future[Unit] = Future {
    grove.close()
  }

  def getDigitalOut(digitalPort: Int): GroveDigitalOut = grove.getDigitalOut(digitalPort)

  def getDigitalIn(digitalPort: Int): GroveDigitalIn = grove.getDigitalIn(digitalPort)

  def getAnalogOut(digitalPort: Int): GroveAnalogOut = grove.getAnalogOut(digitalPort)

  def getAnalogIn(digitalPort: Int, bufferSize: Int): GroveAnalogIn = grove.getAnalogIn(digitalPort, bufferSize)
}
