package controllers

import javax.inject.Inject

import dal.{PostRepository, UserRepository}
import models.Post
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


class PostController @Inject() (repo: PostRepository, user_repo: UserRepository)
                                 (implicit ec: ExecutionContext) extends Controller with Secured{

  def list = Action.async { implicit rs =>
    repo.list.map { posts =>
      Ok(Json.obj("posts" -> posts))
    }
  }

  def add = Action.async(parse.json) { implicit rs =>
    rs.body.validate[Post].map { post =>
      user_repo.get(rs.session.get("email").get).map {
        case Some(v) => {
          repo.add(v.id, post.message, Some(DateTime.now.toString))
          Ok(Json.obj("result" -> "success"))
        }
        case _ => BadRequest(Json.obj("result" -> "failure"))
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
