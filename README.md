# ICStar Dashboard Sales - Backend

## Setup Account Database

Pertama-tama kita harus Setup dulu database kita.

1. Buka cmd/terminal.
2. Ketikkan `mariadb`
3. Setelah masuk ke mariadb buat user baru dengan perintah berikut ini

    ```sql
    CREATE USER 'icstar'@'localhost' IDENTIFIED BY 'optimis';
    ```
4. Kemudian grant all permissions.
    ```sql
    GRANT ALL PRIVILEGES ON * . * TO 'icstar'@'localhost';
    ```
5. Kemudian flush privileges
    ```sql
    FLUSH PRIVILEGES;
    ```

## Setup Database

Untuk setup database, cukup jalankan file `database.sql` yang ada di directory project.
