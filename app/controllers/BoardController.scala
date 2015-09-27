package controllers

import javax.inject.Inject

import dal.UserRepository
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class BoardController @Inject() (repo: UserRepository, val messagesApi: MessagesApi) (implicit ec: ExecutionContext) extends Controller with I18nSupport with Secured {
  val loginUser: Form[LoginUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginUserForm.apply)(LoginUserForm.unapply).verifying("Invalid User!", user =>
      repo.find(user.email, user.password)
    )
  }

  def login = Action {
    Ok(views.html.login(loginUser))
  }

  def authentication = Action.async { implicit request =>
    loginUser.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.login(errorForm)))
      },
      user => {
        Future.successful(
          Redirect(routes.BoardController.board).withSession("email" -> user.email)
        )
      }
    )
  }

  def board = isAuthenticated { email => _ =>
    Ok(views.html.board())
  }

  def logout = Action {
    Redirect(routes.BoardController.login).withNewSession.flashing(
      "success" -> "You've been logged out!"
    )
  }
}

case class LoginUserForm(email: String, password: String)
