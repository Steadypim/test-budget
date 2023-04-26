package mobi.sevenwinds.common

import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification

fun RequestSpecification.auth(token: String): RequestSpecification = this
    .header("Authorization", "Bearer $token")

fun <T> RequestSpecification.jsonBody(body: T): RequestSpecification = this
    .body(body)
    .contentType(ContentType.JSON)

inline fun <reified T> ResponseBodyExtractionOptions.toResponse(): T {
    return this.`as`(T::class.java)
}

fun RequestSpecification.When(): RequestSpecification {
    return this.`when`()
}