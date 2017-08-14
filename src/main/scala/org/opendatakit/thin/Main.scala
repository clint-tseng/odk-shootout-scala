package org.opendatakit.thin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

import com.twitter.finagle.Http
import com.twitter.finagle.http._
import com.twitter.io.Buf
import com.twitter.concurrent._
import com.twitter.util._

import io.circe._
import io.circe.generic.auto._

import io.finch._
import io.finch.circe._

import org.opendatakit.thin.data._
import org.opendatakit.thin.Convert._
import org.opendatakit.thin.FutureImplicits._


object Main extends App {
  case class Message(message: String)

  val blobCreate: Endpoint[Blob] = post("blob" :: stringBody) { content: String =>
    Blobs.save(Blob(None, Some(content))).map(Ok(_)).forTwitter
  }

  val blobGet: Endpoint[Blob] = get("blob" :: int) { id: Int =>
    Blobs.getById(id).map { _ match {
      case Some(blob) => Ok(blob)
      case None => NotFound(new Exception("Not Found!"))
    } }.forTwitter
  }

  val submissionGet: Endpoint[AsyncStream[Buf]] = get("submission" :: string) { formId: String =>
    Ok(rowStreamToCsv(Submissions.streamByFormId(formId)))
  }

  val healthcheck: Endpoint[Message] = get("healthcheck") { Ok(Message("ok!")) }

  val service = Bootstrap.configure(includeServerHeader = false)
    .serve[Application.Json](blobCreate :+: blobGet :+: healthcheck)
    .serve[Text.Plain](submissionGet)
    .toService

  Await.ready(Http.server.serve(":8585", service))
}

