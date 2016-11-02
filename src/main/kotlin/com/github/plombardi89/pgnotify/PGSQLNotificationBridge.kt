package com.github.plombardi89.pgnotify

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import kotlinx.support.jdk7.use
import org.postgresql.PGConnection
import java.sql.Connection


class PGSQLNotificationBridge : AbstractVerticle() {

  /**
   * Database connection. Never closes since we're continually polling for notifications.
   */
  private lateinit var conn: Connection

  override fun start(future: Future<Void>?) {
    conn = getConnection()
    conn.createStatement().use { it.execute("LISTEN active_nodes") }
    future?.complete()
  }

  override fun start() {
    vertx.setPeriodic(500) { onTimerExecute(it) }
  }

  fun onTimerExecute(timerId: Long) {
    vertx.executeBlocking<JsonArray>(
        { fut ->
          // cast the JDBC connection to the underlying PostgreSQL driver implementation so we can have access to the
          // full postgres JDBC driver.
          val postgres = conn.unwrap(PGConnection::class.java)

          // execute a dummy query which does nothing but cause the underlying Postgres connection to populate the
          // notifications from the database.
          conn.createStatement().use { stmt -> stmt.executeQuery("SELECT 1").use { it } }

          val payload = postgres.notifications?.fold(JsonArray()) { acc, notification ->
            acc.add(JsonObject(mapOf(
                "name" to notification.name,
                "parameter" to notification.parameter,
                "processId" to notification.pid
            )))
          }

          fut.complete(payload)
        },
        false,
        { res ->
          if (res.succeeded()) {
            vertx.eventBus().publish("nodes", res.result())
          }
        })
  }

  private fun getConnection(): Connection {

  }
}

