package com.azriel.inventaris059

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.sql.DriverManager

class MigrationAndIsolationTest059 {

    @Test
    fun testMigrationAndUserIsolation059() {
        Class.forName("org.sqlite.JDBC")
        val connection059 = DriverManager.getConnection("jdbc:sqlite::memory:")

        connection059.use { conn ->
            val stmt059 = conn.createStatement()

            // 1. Inisialisasi skema Versi 1
            stmt059.execute(
                """
                CREATE TABLE IF NOT EXISTS user_059 (
                    id059 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    username059 TEXT NOT NULL,
                    password059 TEXT NOT NULL
                )
                """.trimIndent()
            )
            stmt059.execute("CREATE UNIQUE INDEX IF NOT EXISTS index_user_059_username059 ON user_059 (username059)")

            stmt059.execute(
                """
                CREATE TABLE IF NOT EXISTS item_059 (
                    id059 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    userId059 INTEGER NOT NULL,
                    nama059 TEXT NOT NULL,
                    jumlah059 INTEGER NOT NULL,
                    harga059 REAL NOT NULL,
                    FOREIGN KEY(userId059) REFERENCES user_059(id059) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            stmt059.execute("CREATE INDEX IF NOT EXISTS index_item_059_userId059 ON item_059 (userId059)")

            // 2. Insert data lama (User A & User B + item) pada Versi 1
            stmt059.execute("INSERT INTO user_059 (id059, username059, password059) VALUES (1, 'userA059', 'hashA059')")
            stmt059.execute("INSERT INTO user_059 (id059, username059, password059) VALUES (2, 'userB059', 'hashB059')")
            stmt059.execute("INSERT INTO item_059 (id059, userId059, nama059, jumlah059, harga059) VALUES (10, 1, 'Kemeja A', 5, 100000.0)")
            stmt059.execute("INSERT INTO item_059 (id059, userId059, nama059, jumlah059, harga059) VALUES (20, 2, 'Sepatu B', 2, 250000.0)")

            // 3. Jalankan SQL Migrasi 1 -> 2 yang sama persis dengan AppDatabase059.MIGRATION_1_2_059
            stmt059.execute("ALTER TABLE item_059 ADD COLUMN fotoPath059 TEXT DEFAULT NULL")
            stmt059.execute("ALTER TABLE item_059 ADD COLUMN deskripsi059 TEXT DEFAULT NULL")
            stmt059.execute("ALTER TABLE item_059 ADD COLUMN kategori059 TEXT DEFAULT NULL")

            // 4. Verifikasi data lama tetap utuh setelah migrasi
            val rsOld059 = stmt059.executeQuery("SELECT * FROM item_059 WHERE id059 = 10")
            assertTrue("Barang lama User A harus tetap ada", rsOld059.next())
            assertEquals("Kemeja A", rsOld059.getString("nama059"))
            assertEquals(5, rsOld059.getInt("jumlah059"))
            assertEquals(100000.0, rsOld059.getDouble("harga059"), 0.001)
            assertNull("Kolom baru fotoPath059 harus null secara default", rsOld059.getString("fotoPath059"))
            assertNull("Kolom baru deskripsi059 harus null secara default", rsOld059.getString("deskripsi059"))
            assertNull("Kolom baru kategori059 harus null secara default", rsOld059.getString("kategori059"))
            rsOld059.close()

            // 5. Insert data baru versi 2 untuk User A dan User B dengan kategori dan foto
            stmt059.execute(
                """
                INSERT INTO item_059 (id059, userId059, nama059, jumlah059, harga059, fotoPath059, deskripsi059, kategori059)
                VALUES (11, 1, 'Kaos A', 10, 50000.0, '/path/fotoA.jpg', 'Kaos katun', 'Pakaian')
                """.trimIndent()
            )
            stmt059.execute(
                """
                INSERT INTO item_059 (id059, userId059, nama059, jumlah059, harga059, fotoPath059, deskripsi059, kategori059)
                VALUES (12, 1, 'Jaket A', 3, 150000.0, NULL, 'Jaket jeans', 'Pakaian')
                """.trimIndent()
            )
            stmt059.execute(
                """
                INSERT INTO item_059 (id059, userId059, nama059, jumlah059, harga059, fotoPath059, deskripsi059, kategori059)
                VALUES (21, 2, 'Laptop B', 1, 8000000.0, '/path/fotoB.jpg', 'Laptop kerja', 'Elektronik')
                """.trimIndent()
            )

            // 6. Uji query DISTINCT kategori untuk User A (id 1)
            val rsKatA059 = stmt059.executeQuery(
                """
                SELECT DISTINCT kategori059 FROM item_059 
                WHERE userId059 = 1 AND kategori059 IS NOT NULL AND TRIM(kategori059) != '' 
                ORDER BY kategori059 ASC
                """.trimIndent()
            )
            val listKatA059 = mutableListOf<String>()
            while (rsKatA059.next()) {
                listKatA059.add(rsKatA059.getString("kategori059"))
            }
            rsKatA059.close()

            assertEquals("User A hanya punya 1 kategori unik ('Pakaian')", listOf("Pakaian"), listKatA059)
            assertFalse("User A tidak boleh melihat kategori Elektronik milik User B", listKatA059.contains("Elektronik"))

            // 7. Uji query DISTINCT kategori untuk User B (id 2)
            val rsKatB059 = stmt059.executeQuery(
                """
                SELECT DISTINCT kategori059 FROM item_059 
                WHERE userId059 = 2 AND kategori059 IS NOT NULL AND TRIM(kategori059) != '' 
                ORDER BY kategori059 ASC
                """.trimIndent()
            )
            val listKatB059 = mutableListOf<String>()
            while (rsKatB059.next()) {
                listKatB059.add(rsKatB059.getString("kategori059"))
            }
            rsKatB059.close()

            assertEquals("User B hanya punya 1 kategori unik ('Elektronik')", listOf("Elektronik"), listKatB059)
            assertFalse("User B tidak boleh melihat kategori Pakaian milik User A", listKatB059.contains("Pakaian"))

            // 8. Uji isolasi foto & barang: User B tidak boleh melihat item maupun foto milik User A
            val rsItemsB059 = stmt059.executeQuery("SELECT * FROM item_059 WHERE userId059 = 2")
            val itemsBIds059 = mutableListOf<Long>()
            val itemsBPhotos059 = mutableListOf<String?>()
            while (rsItemsB059.next()) {
                itemsBIds059.add(rsItemsB059.getLong("id059"))
                itemsBPhotos059.add(rsItemsB059.getString("fotoPath059"))
            }
            rsItemsB059.close()

            assertEquals(listOf(20L, 21L), itemsBIds059)
            assertFalse("Foto User A tidak boleh ada di list User B", itemsBPhotos059.contains("/path/fotoA.jpg"))
            assertTrue("Foto User B harus ada di list User B", itemsBPhotos059.contains("/path/fotoB.jpg"))
        }
    }
}
