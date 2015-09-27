package controllers

import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import dal._

import scala.concurrent.{ ExecutionContext, Future }

import javax.inject._

class UserController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  /**
   * The mapping for the user form.
   */
  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "email" -> nonEmptyText,
      "password" -> nonEmptyText,
      "username" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  /**
   * The login action.
   */
  def index = Action {
    Ok(views.html.edit(userForm))
  }

  /**
   * The add user action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on UserRepository.
   */
  def addUser = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    userForm.bindFromRequest.fold(
      // The error function. We return the login page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the user creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.edit(errorForm)))
      },
      // There were no errors in the from, so create the user.
      user => {
        repo.create(user.email, user.password, user.username).map { _ =>
          // If successful, we simply redirect to the login page.
          Redirect(routes.UserController.index)
        }
      }
    )
  }

  def users = Action.async { implicit result =>
    repo.list.map {
      all_users => Ok(views.html.users(all_users))
    }
  }
}

/**
 * The create user form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreateUserForm(email: String, password: String, username: String)
