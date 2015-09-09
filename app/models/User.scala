package models

import play.api.libs.json._

case class User(id: Long, email: String, password: String, username: String)

object User {
  
  implicit val personFormat = Json.format[User]
}