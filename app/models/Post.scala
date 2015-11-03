package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Post(id: Option[Long], user_id: Long, message: String, create_at: Option[String])

object Post {
  implicit val postFormat = (
    (JsPath \ "id").formatNullable[Long] and
    (JsPath \ "user_id").format[Long] and
    (JsPath \ "message").format[String] and
    (JsPath \ "create_at").formatNullable[String]
  )(Post.apply, unlift(Post.unapply))
}