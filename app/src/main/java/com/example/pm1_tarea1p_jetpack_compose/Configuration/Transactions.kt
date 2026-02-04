package com.example.pm1_tarea1p_jetpack_compose.Configuration

object Transactions {

    // DB name
    const val dbname = "DBPM"
    const val dbversion = 1

    // DB Tablas
    const val tbpersons = "personas"

    // DB fields
    const val id = "id"
    const val nombres = "nombres"
    const val apellidos = "apellidos"
    const val edad = "edad"
    const val correo = "correo"
    const val foto = "foto"

    // DLL
    const val CreateTablePersonas = "CREATE TABLE $tbpersons ( $id INTEGER PRIMARY KEY AUTOINCREMENT , $nombres TEXT , $apellidos TEXT , $edad INTEGER , $correo TEXT , $foto TEXT  )"

    // DLL Drop
    const val DropTablePersonas = "DROP TABLE IF EXISTS$tbpersons"

    // DML
    const val SelectTablePersonas = "SELECT * FROM$tbpersons"
}