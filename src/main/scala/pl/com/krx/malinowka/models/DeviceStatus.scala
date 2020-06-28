package pl.com.krx.malinowka.models

import scala.language.implicitConversions

object DeviceStatus {

  sealed trait LedState

  case object On extends LedState
  case object Off extends LedState

  implicit def ledStateFromBoolean(value:Boolean):LedState = if(value) On else Off

  implicit def ledStateToBoolean(ledState: LedState):Boolean = ledState match {
    case On ⇒ true
    case Off ⇒ false
  }

  sealed trait ButtonState

  case object Pushed extends ButtonState
  case object NotPushed extends ButtonState

  implicit def buttonStateFromBoolean(value:Boolean):ButtonState = if(value) Pushed else NotPushed

}
