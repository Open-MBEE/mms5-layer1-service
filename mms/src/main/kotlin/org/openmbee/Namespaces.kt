package org.openmbee

import org.apache.jena.datatypes.BaseDatatype
import org.apache.jena.rdf.model.impl.PropertyImpl
import org.apache.jena.rdf.model.impl.ResourceImpl
import org.apache.jena.shared.PrefixMapping

class PrefixMapBuilder(other: PrefixMapBuilder?=null, setup: (PrefixMapBuilder.() -> PrefixMapBuilder)?=null) {
    var map = HashMap<String, String>()

    init {
        if(null != other) map.putAll(other.map)
        if(null != setup) setup(this)
    }

    fun add(vararg adds: Pair<String, String>): PrefixMapBuilder {
        map.putAll(adds)
        return this
    }

    operator fun get(prefix: String): String? {
        return map[prefix]
    }

    override fun toString(): String {
        return map.entries.fold("") {
            out, (key, value) -> out + "prefix $key: <$value>\n"
        }
    }

    fun toPrefixMappings(): PrefixMapping {
        return PrefixMapping.Factory.create().run {
            setNsPrefixes(map)
        }
    }
}

val SPARQL_PREFIXES = PrefixMapBuilder() {
    add(
        "rdf" to "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
        "rdfs" to "http://www.w3.org/2000/01/rdf-schema#",
        "owl" to "http://www.w3.org/2002/07/owl#",
        "xsd" to "http://www.w3.org/2001/XMLSchema#",
        "dct" to "http://purl.org/dc/terms/",
    )

    with("https://openmbee.org/rdf/mms") {
        add(
            "mms" to "$this/ontology/",
            "mms-object" to "$this/objects/",
            "mms-datatype" to "$this/datatypes/",
        )
    }

    with(ROOT_CONTEXT) {
        add(
            "m" to "$this/",
            "m-object" to "$this/objects/",
            "m-graph" to "$this/graphs/",
            "m-org" to "$this/orgs/",
            "m-project" to "$this/projects/",
            "m-user" to "$this/users/",
            "m-group" to "$this/groups/",
            "m-policy" to "$this/policies/",
        )
    }
}

fun prefixesFor(
    userId: String?=null,
    orgId: String?=null,
    projectId: String?=null,
    branchId: String?=null,
    commitId: String?=null,
    transactionId: String?=null,
    source: PrefixMapBuilder?= SPARQL_PREFIXES
): PrefixMapBuilder {
    return PrefixMapBuilder(source) {
        if(null != userId) {
            with("$ROOT_CONTEXT/users/$userId") {
                add(
                    "mu" to this,
                )
            }
        }

        if(null != orgId) {
            with("$ROOT_CONTEXT/orgs/$orgId") {
                add(
                    "mo" to this,
                )
            }
        }

        if(null != transactionId) {
            with("$ROOT_CONTEXT/transactions/$transactionId") {
                add(
                    "mt" to this,
                )
            }
        }

        if(null != projectId) {
            with("$ROOT_CONTEXT/projects/$projectId") {
                add(
                    "mp" to this,
                    "mp-branch" to "$this/branches/",
                    "mp-lock" to "$this/locks/",
                    "mp-graph" to "$this/graphs/",
                    "mp-commit" to "$this/commits/",
                )

                if(null != branchId) {
                    with("$this/branches/$branchId") {
                        add(
                            "mpb" to this,
                        )
                    }
                }

                if(null != commitId) {
                    with("$this/commits/$commitId") {
                        add(
                            "mpc" to this,
                            "mpc-data" to "$this/data"
                        )
                    }
                }
            }
        }

        this
    }
}


object MMS {
    private val _BASE = SPARQL_PREFIXES["mms"]

    // classes
    val Org = ResourceImpl("${_BASE}Org")
    val Project = ResourceImpl("${_BASE}Project")

    // object properties
    val id  = PropertyImpl("${_BASE}id")

    // transaction properties
    val created = PropertyImpl("${_BASE}created")
    val serviceId = PropertyImpl("${_BASE}serviceId")
    val org = PropertyImpl("${_BASE}org")
    val project = PropertyImpl("${_BASE}project")
    val user = PropertyImpl("${_BASE}user")
    val completed = PropertyImpl("${_BASE}completed")
    val requestBody = PropertyImpl("${_BASE}requestBody")
    val requestPath = PropertyImpl("${_BASE}requestPath")

    val orgId = PropertyImpl("${_BASE}orgId")
    val projectId = PropertyImpl("${_BASE}projectId")
    val commitId = PropertyImpl("${_BASE}commitId")

    // access control properties
    val implies = PropertyImpl("${_BASE}implies")
    val inherits = PropertyImpl("${_BASE}inherits")
}

object MMS_DATATYPE {
    private val _BASE = SPARQL_PREFIXES["mms-datatype"]

    val commitMessage = BaseDatatype("${_BASE}commitMessage")
}
