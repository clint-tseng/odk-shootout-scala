package org.opendatakit.thin

import com.twitter.util.{Future => TFuture, Promise => TPromise, Return, Throw}
import scala.concurrent.{Future => SFuture, Promise => SPromise, ExecutionContext}
import scala.util.{Success, Failure}
import scala.language.implicitConversions


object FutureImplicits {
  implicit class RichSFuture[T](val sFuture: SFuture[T]) extends AnyVal {
    def forTwitter(implicit ec: ExecutionContext): TFuture[T] = {
      val tPromise: TPromise[T] = new TPromise[T]
      sFuture.onComplete {
        case Success(value) => tPromise.setValue(value)
        case Failure(exception) => tPromise.setException(exception)
      }
      return tPromise
    }
  }

  implicit def sf2tf[T](sFuture: SFuture[T])(implicit ec: ExecutionContext): TFuture[T] = sFuture.forTwitter
}

