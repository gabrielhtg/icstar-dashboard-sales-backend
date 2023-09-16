# ICStar Dashboard Sales - Backend

Quick Navigation :
- [Setup Account Database](#setup-account-database)
- [Setup Database](#setup-database)
- [User API Spec](#user-api-spec)

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

Untuk setup database, cukup jalankan file `database-creator.sql` yang ada di directory project.

## User API Spec

Berikut adalah user API Spec :
- Register User <br>
  ```
  /api/register-user
  ```
  Ini digunakan untuk mendaftarkan user. Dibutuhkan request body seperti berikut ini :
   
  ```json
  {
    "email" : "usertest@example.com",
    "password" : "testpw",
    "firstName" : "User",
    "lastName" : "Test",
    "admin" : true
  }
  ```
  

- Remove User <br>
  ```
  /api/remove-user
  ```
  API ini digunakan untuk remove user dari database. Dibutuhkan request header dengan nama `email` sebagai parameter
  pada API ini. Berikut contohnya `email : usertest@example.com`. Email yang dilampirkan disini adalah email user yang
  akan dihapus datanya.