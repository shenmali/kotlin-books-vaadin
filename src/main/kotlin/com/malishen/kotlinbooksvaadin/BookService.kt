package com.malishen.kotlinbooksvaadin

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.annotation.PostConstruct

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val webClientBuilder: WebClient.Builder
) {

    private val webClient = webClientBuilder.baseUrl("https://openlibrary.org").build()

    // H2'den tüm kitapları getir
    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    // ID'ye göre kitap getir
    fun getBookById(id: Long): Book? {
        return bookRepository.findById(id).orElse(null)
    }

    // API'den kitap ara ve H2'ye kaydet
    fun searchAndSaveBooksFromApi(query: String): List<Book> {
        val response = webClient.get()
            .uri("/search.json?q={query}&limit=10", query)
            .retrieve()
            .bodyToMono(OpenLibraryResponse::class.java)
            .block() ?: return emptyList()

        val books = response.docs.map { doc: OpenLibraryDoc ->
            Book(
                title = doc.title,
                author = doc.authorName?.firstOrNull(),
                publishYear = doc.firstPublishYear,
                isbn = doc.isbn?.firstOrNull(),
                coverUrl = doc.coverId?.let { coverId -> "https://covers.openlibrary.org/b/id/$coverId-M.jpg" }
            )
        }

        return bookRepository.saveAll(books)
    }

    // İlk yüklemede örnek veriler
    @PostConstruct
    fun loadSampleBooks() {
        if (bookRepository.count() == 0L) {
            searchAndSaveBooksFromApi("kotlin programming")
        }
    }
}

// Open Library API response'u için data class'lar
data class OpenLibraryResponse(
    val docs: List<OpenLibraryDoc>
)

data class OpenLibraryDoc(
    val title: String,
    @field:JsonProperty("author_name")
    val authorName: List<String>?,
    @field:JsonProperty("first_publish_year")
    val firstPublishYear: Int?,
    val isbn: List<String>?,
    @field:JsonProperty("cover_i")
    val coverId: Int?
)