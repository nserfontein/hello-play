package services

import model.User
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, PlayBodyParsers, Request, Result, Results, WrappedRequest}

import scala.concurrent.{ExecutionContext, Future}

case class UserAuthRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

class UserAuthAction(authService: AuthService, ec: ExecutionContext, playBodyParsers: PlayBodyParsers)
extends ActionBuilder[UserAuthRequest, AnyContent]{

  override protected def executionContext: ExecutionContext = ec

  override def parser: BodyParser[AnyContent] = playBodyParsers.defaultBodyParser

  override def invokeBlock[A](request: Request[A], block: UserAuthRequest[A] => Future[Result]): Future[Result] = {
    val maybeUser = authService.checkCookie(request)
    maybeUser match {
      case None =>
        Future.successful(Results.Redirect("/login"))
      case Some(user) =>
        block(UserAuthRequest(user, request))
    }
  }

}
