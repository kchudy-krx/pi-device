package pl.com.krx.malinowka.devices

import java.util.concurrent.{Executors, ScheduledExecutorService}

import org.iot.raspberry.grovepi.GroveDigitalInListener
import pl.com.krx.malinowka.GrovePi4S
import pl.com.krx.malinowka.models.DeviceStatus.ButtonState
import pl.com.krx.malinowka.models.Schedule

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class GroveDigitalButton(port:Int)(implicit private val grovePi4S: GrovePi4S,implicit private val executionContext: ExecutionContext) {

  private val button = grovePi4S.getDigitalIn(port)

  def getState:Future[ButtonState] = Future {
    Try {
      button.get()
    } match {
      case Failure(exception) ⇒ throw exception
      case Success(value) ⇒ value
    }
  }

  def addListener(states:(ButtonState,ButtonState) ⇒ Unit,schedule: Schedule):ScheduledExecutorService = {
    button.setListener((oldValue: Boolean, newValue: Boolean) => {
      states(oldValue, newValue)
    })
    val service: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    service.scheduleAtFixedRate(button,schedule.initialDelay,schedule.period,schedule.timeUnit)
    service
  }
}
