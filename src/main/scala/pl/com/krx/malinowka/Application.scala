package pl.com.krx.malinowka

import java.util.UUID
import java.util.logging.{Level, Logger}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import javax.net.ssl.SSLContext
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.conn.ssl._
import org.apache.http.entity.{BasicHttpEntity, ContentType, StringEntity}
import org.apache.http.impl.client.{DefaultHttpClient, HttpClientBuilder, HttpClients}
import org.apache.http.ssl.SSLContextBuilder
import org.iot.raspberry.grovepi.devices.{GroveLightSensor, GroveTemperatureAndHumiditySensor, GroveTemperatureAndHumidityValue}
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.{AhcCurlRequestLogger, StandaloneAhcWSClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Application extends App {
  Logger.getLogger("GrovePi").setLevel(Level.WARNING)
  Logger.getLogger("RaspberryPi").setLevel(Level.WARNING)

  var counter:Int = 0

  implicit val grovePi4S = new GrovePi4S()

/*  val whiteDiode = new GroveDigitalDiode(3)
  val button:GroveDigitalButton = new GroveDigitalButton(2)
  val lcdDisplay: GroveRgbLcd = grovePi4S.grove.getLCD
  lcdDisplay.setRGB(255,255,0)
  lcdDisplay.setText(s"Ilosc naduszen: $counter")*/

/*  private val buttonListener: ScheduledExecutorService = button.addListener((oldState, newState) ⇒ {
    (oldState, newState) match {
      case (NotPushed, Pushed) ⇒
        counter = counter+1
        whiteDiode.setState(On)
      case _ ⇒ whiteDiode.setState(Off)
    }
    lcdDisplay.setText(s"Ilosc naduszen: $counter")
  }, Schedule(0, 1, TimeUnit.MILLISECONDS))*/


  private val temperatureAndHumiditySensor = new GroveTemperatureAndHumiditySensor(grovePi4S.grove,7,GroveTemperatureAndHumiditySensor.Type.DHT11)
  private val temperatureAndHumidityValue: GroveTemperatureAndHumidityValue = temperatureAndHumiditySensor.get()
  private val lightSensor: GroveLightSensor = new GroveLightSensor(grovePi4S.grove,0)
  private val lightSensorValue:Double = lightSensor.get()
  private val temperatureValue:Double = temperatureAndHumidityValue.getTemperature
  private val humidityValue:Double = temperatureAndHumidityValue.getHumidity


  println(s"temperatura: $temperatureValue")
  println(s"wilgotnosc: $humidityValue")
  println(s"światło: $lightSensorValue")

  import scala.concurrent.ExecutionContext.Implicits._
/*  implicit val system: ActorSystem = ActorSystem()
  system.registerOnTermination{
    System.exit(0)
  }
  implicit val materializer: ActorMaterializer = ActorMaterializer()*/
  import play.api.libs.ws.JsonBodyWritables._

  //val wsClient = StandaloneAhcWSClient()

  val deviceId:UUID = UUID.fromString("24f759eb-164c-4054-853b-ab2fadead3ec")
  val json:JsObject = Json.obj(
    "temperature" → temperatureValue,
    "humidity" → humidityValue,
    "light" → lightSensorValue
  )

  val builder = new SSLContextBuilder()
  builder.loadTrustMaterial(null, new TrustAllStrategy())

  val sslsd:SSLConnectionSocketFactory = new SSLConnectionSocketFactory(
    builder.build()
  )

  val httpClient = HttpClients.custom().setSSLSocketFactory(
    sslsd
  ).build()

  val post = new HttpPost("https://piapi.krx.com.pl/api/data")
  post.addHeader("Auth",deviceId.toString)
  val jsonString = new StringEntity(json.toString(),ContentType.APPLICATION_JSON)
  post.setEntity(jsonString)

  private val response: CloseableHttpResponse = httpClient.execute(post)
  val status = response.getStatusLine.getStatusCode
  println(status)




/*
  private val eventualResponse = wsClient.url("https://piapi.krx.com.pl/api/data")
    .withHttpHeaders("Auth" → deviceId.toString)
    .withRequestFilter(AhcCurlRequestLogger())
    .post(json)

  eventualResponse.onComplete {
    case Failure(exception) ⇒
      println(exception.getMessage)
      throw exception
    case Success(response) ⇒
      response.status match {
        case 204 ⇒ println("wysłano")
        case status ⇒ println(s"poszlo nie tak: $status")
      }
  }
  eventualResponse.andThen {
    case _ ⇒ wsClient.close()
  }.andThen {
    case _ ⇒ system.terminate()
  }*/
  grovePi4S.close().map(_ ⇒ println("done!"))
}
