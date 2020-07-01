package pl.com.krx.malinowka

import java.util.logging.{Level, Logger}

import org.iot.raspberry.grovepi.devices.{GroveLightSensor, GroveRgbLcd, GroveTemperatureAndHumiditySensor, GroveTemperatureAndHumidityValue}
import pl.com.krx.malinowka.modules.RestService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Application extends App {
  Logger.getLogger("GrovePi").setLevel(Level.WARNING)
  Logger.getLogger("RaspberryPi").setLevel(Level.WARNING)

  var counter: Int = 0

  implicit val grovePi4S = new GrovePi4S()


  private val temperatureAndHumiditySensor = new GroveTemperatureAndHumiditySensor(grovePi4S.grove, 7, GroveTemperatureAndHumiditySensor.Type.DHT11)
  private val temperatureAndHumidityValue: GroveTemperatureAndHumidityValue = temperatureAndHumiditySensor.get()
  private val lcd: GroveRgbLcd = grovePi4S.getLcd()
  private val lightSensor: GroveLightSensor = new GroveLightSensor(grovePi4S.grove, 0)
  private val lightSensorValue: Double = lightSensor.get()
  private val temperatureValue: Double = temperatureAndHumidityValue.getTemperature
  private val humidityValue: Double = temperatureAndHumidityValue.getHumidity

  lcd.setRGB(0, 255, 0)

  /*  6699e603-6e66-4070-b8c0-d841e02f918f	temp
    b391f090-4fc6-46e2-97f7-49737d2d1e2c	hum
  746992ae-40c8-49fb-924a-c6685e3ca92b	light*/


  val restService: RestService = new RestService(host = "http://192.168.0.213:8080")
  val r = for {
    _ <- Future {
      lcd.setText(s"Sending temperature: $temperatureValue")
    }
    tempCode <- restService.send("6699e603-6e66-4070-b8c0-d841e02f918f", temperatureValue)
    _ <- Future {
      lcd.setText(s"Sending humidity: $humidityValue")
    }
    humCode <- restService.send("b391f090-4fc6-46e2-97f7-49737d2d1e2c", humidityValue)
    _ <- Future {
      lcd.setText(s"Sending lightValue: $lightSensorValue")
    }
    lightCode <- restService.send("746992ae-40c8-49fb-924a-c6685e3ca92b", lightSensorValue)
  } yield (tempCode, humCode, lightCode)

  r.map(codes => lcd.setText(codes.toString()))

  r.onComplete {
    case Failure(exception) =>
      lcd.setText(exception.getMessage)
    case Success(value) =>

  }


  Await.result(r, 10 seconds)

  grovePi4S.close().map(_ ⇒ println("done!"))
}
