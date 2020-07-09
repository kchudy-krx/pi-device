package pl.com.krx.malinowka

import java.util.logging.{Level, Logger}

import org.iot.raspberry.grovepi.devices.{GroveLightSensor, GroveRgbLcd, GroveTemperatureAndHumiditySensor, GroveTemperatureAndHumidityValue}
import pl.com.krx.malinowka.modules.RestService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

object Application {

  def main(args: Array[String]): Unit = {
    Logger.getLogger("GrovePi").setLevel(Level.WARNING)
    Logger.getLogger("RaspberryPi").setLevel(Level.WARNING)

    var counter: Int = 0

    implicit val grovePi4S = new GrovePi4S()


    val temperatureAndHumiditySensor = new GroveTemperatureAndHumiditySensor(grovePi4S.grove, 7, GroveTemperatureAndHumiditySensor.Type.DHT11)
    val temperatureAndHumidityValue: GroveTemperatureAndHumidityValue = temperatureAndHumiditySensor.get()
    val lcd: GroveRgbLcd = grovePi4S.getLcd()
    val lightSensor: GroveLightSensor = new GroveLightSensor(grovePi4S.grove, 0)
    val lightSensorValue: Double = lightSensor.get()
    val temperatureValue: Double = temperatureAndHumidityValue.getTemperature
    val humidityValue: Double = temperatureAndHumidityValue.getHumidity

    val restService: RestService = new RestService(host = "http://pi.krx.com.pl")
    val r = for {

      tempCode <- restService.send("6699e603-6e66-4070-b8c0-d841e02f918f", temperatureValue)
      humCode <- restService.send("b391f090-4fc6-46e2-97f7-49737d2d1e2c", humidityValue)

      lightCode <- restService.send("746992ae-40c8-49fb-924a-c6685e3ca92b", lightSensorValue)
    } yield (tempCode, humCode, lightCode)

    Await.result(r, 20 seconds)


    grovePi4S.close().map(_ ⇒ println("done!"))
  }

}
