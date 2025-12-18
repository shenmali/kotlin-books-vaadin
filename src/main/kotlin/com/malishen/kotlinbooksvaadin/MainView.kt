package com.malishen.kotlinbooksvaadin

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink

@Route("")
class MainView : VerticalLayout() {

    init {
        // TextField - Kullanıcıdan isim al
        val nameField = TextField("Adınız")

        // Merhaba butonu - Notification göster
        val greetButton = Button("Merhaba De!") {
            Notification.show("Merhaba ${nameField.value}!")
        }

        // Temizle butonu - TextField'ı temizle
        val clearButton = Button("Temizle") {
            nameField.clear()
        }

        // Kitap listesine git butonu
        val booksLink = RouterLink("📚 Kitap Listesi", BooksView::class.java)

        // Layout'a componentleri ekle
        add(nameField, greetButton, clearButton, booksLink)
    }
}