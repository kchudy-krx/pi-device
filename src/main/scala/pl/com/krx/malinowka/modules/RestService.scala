package pl.com.krx.malinowka.modules

import java.time.LocalDateTime

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.client.HttpClients
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.{ExecutionContext, Future}

class RestService(host: String)(implicit val executionContext: ExecutionContext) {

  def send(deviceId: String, value: Double): Future[Int] = Future {

    val post = new HttpPost(s"$host/device/${deviceId}")

    val now = LocalDateTime.now()

    val json: JsObject = Json.obj(
      "value" → value,
      "sampledAt" -> now
    )


    val httpClient: HttpClient = HttpClients.createDefault()
    val jsonString = new StringEntity(json.toString(), ContentType.APPLICATION_JSON)
    post.setEntity(jsonString)

    val response: HttpResponse = httpClient.execute(post)
    println(response.getStatusLine.getStatusCode)
    response.getStatusLine.getStatusCode


  }
}
