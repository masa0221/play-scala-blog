package controllers

import javax.inject.Inject

import dal.PostRepository
import models.Post
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class PostController @Inject() (repo: PostRepository)
                                 (implicit ec: ExecutionContext) extends Controller{

  def list = Action.async { implicit rs =>
    repo.list.map { posts =>
      Ok(Json.obj("posts" -> posts))
    }
  }

  def add = Action.async(parse.json) { implicit rs =>
    rs.body.validate[Post].map { post =>
      repo.add(post.user_id, post.message, Some(DateTime.now.toString)).map { _ =>
        Ok(Json.obj("result" -> "success"))
      }
    }.recoverTotal ( e =>
      Future {
        BadRequest(Json.obj("result" -> "failure", "error" -> JsError.toJson(e)))
      }
    )
  }

  def delete(id: Long) = Action.async { implicit rs =>
    repo.delete(id).map {
      case 0 => BadRequest(Json.obj("result" -> "failure"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }
}
