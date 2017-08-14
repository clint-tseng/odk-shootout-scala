package org.opendatakit.thin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import slick.basic.DatabasePublisher
import com.twitter.io.Buf
import com.twitter.io.Reader
import com.twitter.concurrent.AsyncStream

import org.opendatakit.thin.data._

object Convert {
  def rowStreamToCsv(rows: DatabasePublisher[Submission]): AsyncStream[Buf] = {
    rows.mapResult { submission =>
      submission.xml.getOrElse("")
    }
  }

  implicit def publisherToStream(publisher: DatabasePublisher[String]): AsyncStream[Buf] = {
    val writable = Reader.writable()
    publisher.foreach { x => writable.write(Buf.Utf8(x)) }.onComplete { _ => writable.close() }
    return AsyncStream.fromReader(writable)
  }
}

