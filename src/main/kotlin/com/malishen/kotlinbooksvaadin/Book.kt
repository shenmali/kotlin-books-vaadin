package com.malishen.kotlinbooksvaadin

import jakarta.persistence.*

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column
    val author: String? = null,

    @Column
    val publishYear: Int? = null,

    @Column
    val isbn: String? = null,

    @Column(length = 1000)
    val coverUrl: String? = null
)