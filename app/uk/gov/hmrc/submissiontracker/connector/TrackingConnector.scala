/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.submissiontracker.connector

import javax.inject.{Inject, Named, Singleton}

import com.google.inject.ImplementedBy
import play.api._
import uk.gov.hmrc.http.{HttpGet, HeaderCarrier}
import uk.gov.hmrc.submissiontracker.domain.TrackingDataSeq

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[TrackingConnectorImpl])
trait TrackingConnector {
  val trackingBaseUrl: String

	val httpGet: HttpGet

  def trackingDataLink(id: String, idType:String): String = s"$trackingBaseUrl/tracking-data/user/$idType/$id"

	def getUserTrackingData(id: String,idType:String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TrackingDataSeq] = {
		Logger.debug("submission-tracker: Requesting tracking data")
		httpGet.GET[TrackingDataSeq](trackingDataLink(id,idType))
	}

}

@Singleton
class TrackingConnectorImpl @Inject()(@Named("trackingUrl") val trackingBaseUrl: String, val httpGet: HttpGet) extends TrackingConnector
