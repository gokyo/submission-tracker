package uk.gov.hmrc.submissiontracker

import uk.gov.hmrc.submissiontracker.stubs.{AuthStub, TrackingStub}
import uk.gov.hmrc.submissiontracker.support.BaseISpec
import com.github.tomakehurst.wiremock.client.WireMock._

class SubmissionTrackerISpec extends BaseISpec {

  "GET /tracking/:id/:idType" should {
    "return tracking data" in {
      val nino = "CS700100A"
      val idType = "some-id-type"
      AuthStub.grantAccess(nino, nino)
      TrackingStub.getUserTrackingData(idType, nino)
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
        .get())

      withClue(response.body) {
        response.status shouldBe 200
      }
    }

    "override to sandbox when using sandbox user, avoiding auth call" in {
      val nino = "CS700100A"
      val idType = "some-id-type"
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "X-MOBILE-USER-ID" -> "208606423740")
        .get())

      verify(0, postRequestedFor(urlEqualTo("/auth/authorise")))
      verify(0, postRequestedFor(urlEqualTo(s"/tracking-data/user/$idType/$nino")))

      withClue(response.body) {
        response.status shouldBe 200
      }
    }

    "fail with a 401 if confidence level is low" in {
      val nino = "CS700100A"
      val idType = "some-id-type"
      AuthStub.grantAccess(nino, nino, 100)
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
        .get())

      withClue(response.body) {
        response.status shouldBe 401
      }
    }

    "fail with a 401 if nino return is empty" in {
      val nino = "CS700100A"
      val idType = "some-id-type"
      AuthStub.grantAccess(nino, "")
      val response = await(wsUrl(s"/tracking/$nino/$idType")
        .withHeaders("Accept" -> "application/vnd.hmrc.1.0+json")
        .get())

      withClue(response.body) {
        response.status shouldBe 401
      }
    }
  }

}
