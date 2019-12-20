import actors.StatsActor
import actors.StatsActor.Ping
import akka.actor.Props
import com.softwaremill.macwire._
import controllers.{Application, AssetsComponents}
import filters.StatsFilter
import play.api
import play.api.cache.caffeine.CaffeineCacheComponents
import play.api.db.evolutions.{DynamicEvolutions, EvolutionsComponents}
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.{ControllerComponents, DefaultControllerComponents, Filter}
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, Logger, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes
import scalikejdbc.config.DBs
import services.{AuthService, SunService, UserAuthAction, WeatherService}

import scala.concurrent.Future

class AppApplicationLoader extends ApplicationLoader {

  override def load(context: ApplicationLoader.Context): api.Application = {
    LoggerConfigurator(context.environment.classLoader).foreach { cfg =>
      cfg.configure(context.environment)
    }
    new AppComponents(context).application
  }

}

class AppComponents(context: ApplicationLoader.Context)
  extends BuiltInComponentsFromContext(context)
    with AhcWSComponents
    with EvolutionsComponents
    with DBComponents
    with HikariCPComponents
    with CaffeineCacheComponents
    with AssetsComponents
    with HttpFiltersComponents {

  private val log = Logger(this.getClass)

  lazy val authService = new AuthService(defaultCacheApi.sync)

  lazy val statsActor = actorSystem.actorOf(Props(wire[StatsActor]), StatsActor.name)

  lazy val sunService = wire[SunService]
  lazy val weatherService = wire[WeatherService]
  lazy val userAuthAction = wire[UserAuthAction]

  lazy val statsFilter: Filter = wire[StatsFilter]
  override lazy val httpFilters = Seq(statsFilter)

  override lazy val controllerComponents: ControllerComponents = wire[DefaultControllerComponents]
  lazy val prefix: String = "/"
  lazy val router: Router = wire[Routes]
  lazy val applicationController: Application = wire[Application]

  override lazy val dynamicEvolutions = new DynamicEvolutions

  val onStart = {
    log.info("The app is about to start")
    DBs.setupAll()
    applicationEvolutions
    statsActor ! Ping
  }

  applicationLifecycle.addStopHook { () =>
    log.info("The app is about to stop")
    DBs.closeAll()
    Future.successful(Unit)
  }

}

