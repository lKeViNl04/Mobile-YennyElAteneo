package com.example.yennyelateneo.data.supabase


import com.example.yennyelateneo.data.model.Book
import com.example.yennyelateneo.data.model.Bookk
import com.example.yennyelateneo.data.model.toJavaBook
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object SupabaseBookService {


    @JvmStatic
    suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseManager.supabase
                .from("books")
                .select()
                .decodeList<Bookk>()
                .map { it.toJavaBook() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @JvmStatic
    suspend fun getBooksByIds(bookIds: List<Long>): List<Book> = withContext(Dispatchers.IO) {
        if (bookIds.isEmpty()) return@withContext emptyList()

        return@withContext try {

            val formattedIds = bookIds.joinToString(",", "(", ")")

            val books = SupabaseManager.supabase.from("books")
                .select {
                    filter {
                        filter(column = "id", operator = FilterOperator.IN, value = formattedIds)
                    }
                }
                .decodeList<Bookk>()
                .map { it.toJavaBook() }

            books
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @JvmStatic
    suspend fun getBookById(id: Long): Book? = withContext(Dispatchers.IO) {
        return@withContext try {
            SupabaseManager.supabase
                .from("books")
                .select {
                    filter { eq("id", id) }
                    limit(1)
                }
                .decodeSingle<Bookk>()
                .toJavaBook()
        } catch (e: Exception) {
            null
        }
    }


    @JvmStatic
    suspend fun deleteBookById(BookId: Long): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val bookk = SupabaseManager.supabase.from("books")
                .select {
                    filter {
                        eq("id", BookId)
                    }
                }
                .decodeSingle<Bookk>()

            if (!bookk.image.isNullOrEmpty()) {

                val imagePath = bookk.image?.substringAfter("book.img/")?.removePrefix("/")

                SupabaseManager.supabase.storage
                    .from("book.img")
                    .delete(imagePath!!)
            }

            SupabaseManager.supabase.from("books")
                .delete {
                    filter {
                        eq("id", BookId)
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    suspend fun addBook(bookk: Bookk, imageBytes: ByteArray, fileName: String): Boolean =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val result = SupabaseManager.supabase
                    .storage
                    .from("book.img")
                    .upload(fileName, imageBytes) {
                        upsert = true
                    }

                val publicUrl = SupabaseManager.supabase
                    .storage
                    .from("book.img")
                    .publicUrl(fileName)

                val bookWithImage = bookk.copy(image = publicUrl)

                SupabaseManager.supabase
                    .from("books")
                    .insert(bookWithImage)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    @JvmStatic
    suspend fun updateBookById(bookk: Bookk, imageBytes: ByteArray?, fileName: String?): Boolean =
        withContext(Dispatchers.IO) {
            try {
                if (bookk.id == null) return@withContext false

                val response = SupabaseManager.supabase
                    .from("books")
                    .select {
                        filter { eq("id", bookk.id!!) }
                        limit(1)
                    }
                    .decodeSingle<Bookk>()

                val oldImageUrl = response.image

                val finalImageUrl = if (imageBytes != null && fileName != null) {
                    oldImageUrl?.let { previousUrl ->
                        val previousFileName = previousUrl.substringAfterLast("/")
                        if (previousFileName.isNotBlank()) {
                            SupabaseManager.supabase
                                .storage
                                .from("book.img")
                                .delete(listOf(previousFileName))
                        }
                    }

                    SupabaseManager.supabase
                        .storage
                        .from("book.img")
                        .upload(fileName, imageBytes) {
                            upsert = true
                        }

                    SupabaseManager.supabase
                        .storage
                        .from("book.img")
                        .publicUrl(fileName)
                } else {
                    oldImageUrl
                }

                SupabaseManager.supabase
                    .from("books")
                    .update(
                        update = {
                            set("title", bookk.title)
                            set("author", bookk.author)
                            set("description", bookk.description)
                            set("image", finalImageUrl)
                            set("price", bookk.price)
                        },
                        request = {
                            filter { eq("id", bookk.id!!) }
                        }
                    )
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }



    @JvmStatic
    fun getBooksBlocking(): List<Book> = runBlocking {
        getBooks()
    }

    @JvmStatic
    fun getBooksByIdsBlocking(bookIds: List<Long>): List<Book> = runBlocking {
        getBooksByIds(bookIds)
    }

    @JvmStatic
    fun getBookByIdBlocking(bookId: Long): Book? = runBlocking {
        getBookById(bookId)
    }

    @JvmStatic
    fun deleteBookByIdBlocking(bookId: Long): Boolean = runBlocking {
        deleteBookById(bookId)
    }

    @JvmStatic
    fun addBookBlocking(book: Bookk, imageBytes: ByteArray, fileName: String): Boolean =
        runBlocking {
            addBook(book, imageBytes, fileName)
        }

    @JvmStatic
    fun updateBookByIdBlocking(book: Bookk, imageBytes: ByteArray?, fileName: String?): Boolean =
        runBlocking {
            updateBookById(book, imageBytes, fileName)
        }

}
