package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Post(id: Long, message: String, create_at: String)

object Post {
  implicit val postFormat = (
    (JsPath \ "id").format[Long] and
    (JsPath \ "message").format[String] and
    (JsPath \ "create_at").format[String]
  )(Post.apply, unlift(Post.unapply))
}