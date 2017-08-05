package org.opendatakit.thin.data

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._


case class Blob(id: Option[Int], body: Option[String])

class Blobs(tag: Tag) extends Table[Blob](tag, "blobs") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def body = column[Option[String]]("body")
  def * = (id.?, body) <> (Blob.tupled, Blob.unapply)
}
object Blobs {
  val db = Database.forConfig("database") // TODO: toplevel connection pool.
  val table = TableQuery[Blobs]

  def getById(id: Int): Future[Option[Blob]] = db.run(table.filter(_.id === id).result.headOption)

  def save(blob: Blob): Future[Blob] = {
    blob.id match {
      case Some(id) =>
        db.run(table.filter(_.id === id).update(blob).map(_ => blob))
      case None =>
        db.run((table returning table.map(_.id) into ((blob, id) => blob.copy(id=Some(id)))) += blob)
    }
  }
}

