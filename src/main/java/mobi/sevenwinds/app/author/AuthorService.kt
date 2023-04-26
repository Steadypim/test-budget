package mobi.sevenwinds.app.author

import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

object AuthorService {
    suspend fun addAuthor(authorRecord: AuthorRecord): AuthorRecord {
        val authorEntity = transaction {
            AuthorEntity.new {
                name = authorRecord.name
                createdAt = DateTime.now()
            }
        }
        return authorEntity.toResponse()
    }
}
