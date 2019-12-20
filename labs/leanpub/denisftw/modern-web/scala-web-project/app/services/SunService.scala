package services

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import model.SunInfo
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SunService(wsClient: WSClient) {

  def getSunInfo(lat: Double, lon: Double): Future[SunInfo] = {
    val responseF = wsClient.url(s"http://api.sunrise-sunset.org/json?lat=$lat&lng=$lon&formatted=0").get()
    responseF.map { response =>
      val json = response.json
      val sunriseTimeStr = (json \ "results" \ "sunrise").as[String]
      val sunsetTimeStr = (json \ "results" \ "sunset").as[String]
      val sunriseTime = ZonedDateTime.parse(sunriseTimeStr)
      val sunsetTime = ZonedDateTime.parse(sunsetTimeStr)
      val formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.of("Australia/Sydney"))
      val sunInfo = SunInfo(
        sunrise = sunriseTime.format(formatter),
        sunset = sunsetTime.format(formatter)
      )
      sunInfo
    }
  }

}
