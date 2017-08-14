package org.opendatakit.thin.data

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.backend.DatabasePublisher


case class Submission(id: Option[Int], formId: String, instanceId: String, xml: Option[String])

class Submissions(tag: Tag) extends Table[Submission](tag, "submissions") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def formId = column[String]("form_id")
  def instanceId = column[String]("instance_id")
  def xml = column[Option[String]]("xml")
  def * = (id.?, formId, instanceId, xml) <> (Submission.tupled, Submission.unapply)
}
object Submissions {
  val db = Database.forConfig("database") // TODO: toplevel connection pool.
  val table = TableQuery[Submissions]

  def getById(id: Int): Future[Option[Submission]] = db.run(table.filter(_.id === id).result.headOption)

  def listByFormId(formId: String): Future[Seq[Submission]] = db.run(table.filter(_.formId === formId).result)
  def streamByFormId(formId: String): DatabasePublisher[Submission] = db.stream(table.filter(_.formId === formId).result)
}

