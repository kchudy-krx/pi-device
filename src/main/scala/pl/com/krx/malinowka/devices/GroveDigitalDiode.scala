package pl.com.krx.malinowka.devices

import pl.com.krx.malinowka.GrovePi4S
import pl.com.krx.malinowka.models.DeviceStatus.LedState

import scala.concurrent.{ExecutionContext, Future}

class GroveDigitalDiode(port:Int)(implicit private val grovePi4S: GrovePi4S, implicit private val executionContext: ExecutionContext)  {

  private val diode = grovePi4S.getDigitalOut(port)

  def setState(ledState: LedState):Future[Unit] = Future {
    diode.set(ledState)
  }
}
