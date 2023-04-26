package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecord): BudgetRecord = withContext(Dispatchers.IO) {
        transaction {
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.author = body.authorId?.let { AuthorEntity.findById((it)) }
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam, author: String?): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = (BudgetTable leftJoin AuthorTable)
                .select { BudgetTable.year eq param.year }
                .apply {
                    if (!author.isNullOrEmpty()) {
                        andWhere { AuthorTable.name.lowerCase() like "%${author.toLowerCase()}%" }
                    }
                }
                .limit(param.limit, param.offset)

            val total = query.count()
            val data = query.map {
                BudgetRecord(
                    year = it[BudgetTable.year],
                    month = it[BudgetTable.month],
                    amount = it[BudgetTable.amount],
                    type = BudgetType.valueOf(it[BudgetTable.type].toString()),
                    authorName = it[AuthorTable.name],
                    createdAt = it[AuthorTable.createdAt]?.toDateTime(),
                    authorId = it[AuthorTable.id]?.value
                )
            }

            val sumByType = data.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }

            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}