package services

import com.softwaremill.macwire._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{BuiltInComponentsFromContext, NoHttpFiltersComponents}

class WeatherServiceSpec extends PlaySpec
  with OneAppPerSuiteWithComponents
  with ScalaFutures
  with IntegrationPatience {

  override def components = new TestAppComponents(context)

  "WeatherService" must {
    "return a meaningful temperature" in {
      val lat = -33.8830
      val lon = 151.2167
      val resultF = components.weatherService.getTemperature(lat, lon)
      whenReady(resultF) { result =>
        result mustBe >=(-20.0)
        result mustBe <=(60.0)
      }
    }
  }

}

class TestAppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with NoHttpFiltersComponents
  with AhcWSComponents {

  override lazy val router: Router = Router.empty

  lazy val weatherService = wire[WeatherService]

}
