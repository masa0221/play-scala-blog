package controllers

import play.api._
import play.api.mvc._

trait Secured {
  private def email(request: RequestHeader) = request.session.get("email")

  // 未認証時のリダイレクト先
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.BoardController.login)

  // Actionに認証をかませてラップ
  def isAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(email, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }
}
