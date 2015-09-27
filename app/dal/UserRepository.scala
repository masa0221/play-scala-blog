package dal

import javax.inject.{Inject, Singleton}

import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * A repository for user.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import driver.api._

  /**
   * Here we define the table. It will have a name of user
   */
  private class UserTable(tag: Tag) extends Table[User](tag, "users") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The email column */
    def email = column[String]("email")

    /** The password column */
    def password = column[String]("password")

    /** The username column */
    def username = column[String]("username")

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the User object.
     *
     * In this case, we are simply passing the id, name and page parameters to the User case classes
     * apply and unapply methods.
     */
    def * = (id, email, password, username) <> ((User.apply _).tupled, User.unapply)
  }

  /**
   * The starting point for all queries on the user table.
   */
  private val user = TableQuery[UserTable]

  /**
   * Create a user with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created user, which can be used to obtain the
   * id for that user.
   */
  def create(email: String, password: String, username: String): Future[User] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (user.map(p => (p.email, p.password, p.username))
      // Now define it to return the id, because we want to know what id was generated for the user
      returning user.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((userInfo, id) => User(id, userInfo._1, userInfo._2, userInfo._3))
      // into ((userInfo, id) => user.copy(id=Some(id)))
    // And finally, insert the user into the database
    ) += (email, password, username)
  }

  /**
   * List all the user in the database.
   */
  def list: Future[Seq[User]] = db.run {
    user.result
  }

  def find(email: String, password: String): Boolean = {
    val result = db.run {
      user.filter(u => u.email === email && u.password === password).length.result
    }

    Await.result(result, Duration.Inf)
    result.value match {
      case None => false
      case Some(v) => v.get > 0
    }
  }
}
