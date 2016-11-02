package com.github.plombardi89.pgnotify

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.impl.JDBCClientImpl


class PGNotifiableJDBCClient(
    vertx: Vertx,
    config: JsonObject,
    datasourceName: String) : JDBCClientImpl(vertx, config, datasourceName) {


}