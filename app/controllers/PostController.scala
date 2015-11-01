package controllers

import javax.inject.Inject

import dal.PostRepository
import models.Post
import org.joda.time.DateTime
import play.api.i18n._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{Future, ExecutionContext}


class PostController @Inject() (repo: PostRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  def list = Action.async { implicit rs =>
    repo.list.map { posts =>
      Ok(Json.obj("posts" -> posts))
    }
  }

  def add = Action.async(parse.json) { implicit rs =>
    rs.body.validate[Post].map { post =>
      repo.add(post.user_id, post.message, DateTime.now.toString).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal ( e =>
      Future {
        BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(e)))
      }
    )
  }
}
