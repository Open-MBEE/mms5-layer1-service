package org.openmbee.mms5.util


class RemoteBackend : SparqlBackend {
    override fun start() {}

    override fun stop() {}

    override fun getQueryUrl(): String {
        return System.getenv("MMS5_QUERY_URL")
    }

    override fun getUpdateUrl(): String {
        return System.getenv("MMS5_UPDATE_URL")
    }

    override fun getGspdUrl(): String {
        return System.getenv("MMS5_GRAPH_STORE_PROTOCOL_URL")
    }

}