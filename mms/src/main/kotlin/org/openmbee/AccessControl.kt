package org.openmbee

import java.util.*

const val SPARQL_BGP_USER_EXISTS = """
    # user must exist
    graph m-graph:AccessControl.Members {
        mu: a mms:User .
    }
"""

enum class Permission(val id: String) {
    CREATE_ORG("CreateOrg"),
    READ_ORG("ReadOrg"),
    UPDATE_ORG("UpdateOrg"),
    DELETE_ORG("DeleteOrg"),

    CREATE_PROJECT("CreateProject"),
    READ_PROJECT("ReadProject"),
    UPDATE_PROJECT("UpdateProject"),
    DELETE_PROJECT("DeleteProject"),
}

enum class Scope(val id: String) {
    CLUSTER("Cluster"),
    ORG("Org"),
    PROJECT("Project"),
    BRANCH("Branch"),
}

enum class Role(val id: String) {
    ADMIN_METADATA("AdminMetadata"),
    ADMIN_MODEL("AdminModel"),
}

fun permittedActionSparqlBgp(permission: Permission, scope: Scope): String {
    return """
        # user exists and may belong to some group
        graph m-graph:AccessControl.Members {
            mu: a mms:User .
            
            optional {
                mu: mms:group ?group .
                ?group a mms:Group .
            }
        }
        
        # a policy exists that applies to this user/group within the given scope
        graph m-graph:AccessControl.Policies {
            ?policy a mms:Policy ;
                mms:scope ?scope ;
                mms:role ?role ;
                .
    
            mms-object:Scope.${scope.id} mms:inherits* ?scope .
            
            {
                # policy about user
                ?policy mms:subject mu: .
            } union {
                # or policy about group user belongs to
                ?policy mms:subject ?group .
            }
        }
        
        # 
        graph m-graph:AccessControl.Definitions {
            ?role a mms:Role ;
                mms:permissions ?directRolePermissions ;
                .
            
            ?directRolePermissions a mms:Permission ;
                mms:implies* mms-object:Permission.${permission.id} ;
                .
        }
    """.trimIndent()
}

fun autoPolicySparqlBgp(builder: InsertBuilder, prefixes: PrefixMapBuilder, scope: Scope, roles: List<Role>): InsertBuilder {
    return builder.run {
        graph("m-graph:AccessControl") {
            raw(
                """
                ${prefixes["m-policy"]}Auto${scope.id}Owner.${UUID.randomUUID()} a mms:Policy ;
                    mms:subject mu: ;
                    mms:scope mms-object:Scope.${scope.id} ;
                    mms:role ${roles.joinToString(",") { "mms-object:Role.${it.id}" }}  ;
                    .
            """
            )
        }
    }
}
