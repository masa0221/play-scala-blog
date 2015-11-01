package controllers

import javax.inject.Inject

import dal.PostRepository
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext


class PostController @Inject() (repo: PostRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  def list = Action.async { implicit rs =>
    repo.list.map { posts =>
      Ok(Json.obj("posts" -> posts))
    }
  }
}
