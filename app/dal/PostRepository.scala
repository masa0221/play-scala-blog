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
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def message = column[String]("message")
    def create_at = column[String]("create_at")
    def * = (id, message, create_at) <> ((Post.apply _).tupled, Post.unapply)
  }

  private val post = TableQuery[PostTable]

  def add(message: String, create_at: String): Future[Post] = db.run {
    (post.map(p => (p.message, p.create_at))
      returning post.map(_.id)
      into ((postInfo, id) => Post(id, postInfo._1, postInfo._2))
      ) +=(message, create_at)
  }

  def list(): Future[Seq[Post]] = db.run {
    post.result
  }
}

