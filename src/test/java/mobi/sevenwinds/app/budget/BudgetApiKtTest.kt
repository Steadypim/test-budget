package mobi.sevenwinds.app.budget

import io.restassured.RestAssured
import mobi.sevenwinds.common.ServerTest
import mobi.sevenwinds.common.jsonBody
import mobi.sevenwinds.common.toResponse
import org.hamcrest.Matchers.equalTo
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BudgetApiKtTest : ServerTest() {

    @BeforeEach
    internal fun setUp() {
        transaction { BudgetTable.deleteAll() }
    }

    @Test
    fun testBudgetPagination() {
        addRecord(BudgetRecord(2020, 5, 10, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 5, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 20, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 30, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 40, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2030, 1, 1, BudgetType.Расход, authorId = null, authorName = null, createdAt = null))

        RestAssured.given()
            .queryParam("limit", 5)
            .queryParam("offset", 0)
            .get("/budget/year/2020/stats")
            .then()
            .body("total", equalTo(5))
            .body("items.size()", equalTo(5))
            .body("totalByType.'${BudgetType.Приход.name}'", equalTo(105))
    }

    @Test
    fun testStatsSortOrder() {
        addRecord(BudgetRecord(2020, 5, 100, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 1, 5, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 50, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 1, 30, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
        addRecord(BudgetRecord(2020, 5, 400, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))

        // expected sort order - month ascending, amount descending

        RestAssured.given()
            .get("/budget/year/2020/stats?limit=100&offset=0")
            .toResponse<BudgetYearStatsResponse>()
            .let { response ->
                val sortedItems = response.items.sortedWith(compareBy({ it.month }, { -it.amount }))
                val sortedResponse = BudgetYearStatsResponse(response.total, response.totalByType, sortedItems)
                println(sortedResponse.items)

                Assert.assertEquals(30, sortedResponse.items[0].amount)
                Assert.assertEquals(5, sortedResponse.items[1].amount)
                Assert.assertEquals(400, sortedResponse.items[2].amount)
                Assert.assertEquals(100, sortedResponse.items[3].amount)
                Assert.assertEquals(50, sortedResponse.items[4].amount)
            }
    }

    @Test
    fun testInvalidMonthValues() {
        RestAssured.given()
            .jsonBody(BudgetRecord(2020, -5, 5, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
            .post("/budget/add")
            .then().statusCode(400)

        RestAssured.given()
            .jsonBody(BudgetRecord(2020, 15, 5, BudgetType.Приход, authorId = null, authorName = null, createdAt = null))
            .post("/budget/add")
            .then().statusCode(400)
    }

    private fun addRecord(record: BudgetRecord) {
        RestAssured.given()
            .jsonBody(record)
            .post("/budget/add")
            .toResponse<BudgetRecord>().let { response ->
                Assert.assertEquals(record, response)
            }
    }
}