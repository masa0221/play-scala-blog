package dal

import javax.inject.{Inject, Singleton}

import models.Post
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class PostRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class PostTable(tag: Tag) extends Table[Post](tag, "posts") {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def message = column[String]("message")
    def create_at = column[Option[String]]("create_at")
    def * = (id, user_id, message, create_at) <> ((Post.apply _).tupled, Post.unapply)
  }

  private val post = TableQuery[PostTable]

  def add(user_id: Long, message: String, create_at: Option[String]): Future[Post] = db.run {
    (post.map(p => (p.user_id, p.message, p.create_at))
      returning post.map(_.id)
      into ((postInfo, id) => Post(id, postInfo._1, postInfo._2, postInfo._3))
      ) +=(user_id, message, create_at)
  }

  def delete(id: Long): Future[Int] = db.run {
    post.filter(_.id === id).delete
  }

  def list(): Future[Seq[Post]] = db.run {
    post.result
  }
}

