package com.malishen.kotlinbooksvaadin

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink

@Route("books")
class BooksView(
    private val bookService: BookService
) : VerticalLayout() {

    private val grid = Grid(Book::class.java, false)
    private val searchField = TextField("Kitap Ara")

    init {
        // Başlık
        add(H1("📚 Kitap Listesi"))

        // Ana sayfaya dön linki
        val homeLink = RouterLink("🏠 Ana Sayfa", MainView::class.java)
        add(homeLink)

        // Arama alanı ve buton
        val searchButton = Button("API'den Ara ve Kaydet") {
            val query = searchField.value
            if (query.isNotBlank()) {
                bookService.searchAndSaveBooksFromApi(query)
                refreshGrid()
            }
        }

        add(searchField, searchButton)

        // Grid yapılandırması
        configureGrid()
        add(grid)

        // İlk yükleme
        refreshGrid()
    }

    private fun configureGrid() {
        grid.addColumn { it.title }.setHeader("Başlık").setAutoWidth(true)
        grid.addColumn { it.author ?: "Bilinmiyor" }.setHeader("Yazar").setAutoWidth(true)
        grid.addColumn { it.publishYear?.toString() ?: "-" }.setHeader("Yıl").setAutoWidth(true)
        grid.addColumn { it.isbn ?: "-" }.setHeader("ISBN").setAutoWidth(true)

        // Grid'e tıklama özelliği ekle
        grid.addItemClickListener { event ->
            val book = event.item
            book.id?.let { bookId ->
                // Detay sayfasına yönlendir
                UI.getCurrent().navigate(BookDetailView::class.java, bookId)
            }
        }

        // Satırların tıklanabilir olduğunu göstermek için CSS
        grid.element.style.set("cursor", "pointer")
    }

    private fun refreshGrid() {
        val books = bookService.getAllBooks()
        grid.setItems(books)
    }
}